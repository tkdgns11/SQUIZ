package com.ssafy.conference.recording;

import com.ssafy.conference.recording.dto.RecordingCompleteRequest;
import com.ssafy.conference.recording.dto.RecordingDetailResponse;
import com.ssafy.conference.recording.dto.RecordingSummaryItem;
import com.ssafy.conference.recording.dto.SummaryUpsertRequest;
import com.ssafy.conference.recording.dto.TranscriptUpsertRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordingService {
  private static final Logger logger = LoggerFactory.getLogger(RecordingService.class);

  private final RecordingRepository recordingRepository;
  private final TranscriptRepository transcriptRepository;
  private final SummaryRepository summaryRepository;
  private final ProcessingJobRepository jobRepository;
  private final RecorderClient recorderClient;
  private final boolean mockProcessing;

  public RecordingService(
      RecordingRepository recordingRepository,
      TranscriptRepository transcriptRepository,
      SummaryRepository summaryRepository,
      ProcessingJobRepository jobRepository,
      RecorderClient recorderClient,
      @Value("${app.processing.mock:true}") boolean mockProcessing
  ) {
    this.recordingRepository = recordingRepository;
    this.transcriptRepository = transcriptRepository;
    this.summaryRepository = summaryRepository;
    this.jobRepository = jobRepository;
    this.recorderClient = recorderClient;
    this.mockProcessing = mockProcessing;
  }

  @Transactional
  public Recording startRecording(String roomId) {
    Recording recording = new Recording();
    recording.setRoomId(roomId);
    recording.setStatus(RecordingStatus.RECORDING);
    recording.setStartedAt(Instant.now());
    Recording saved = recordingRepository.save(recording);
    recorderClient.start(saved);
    return saved;
  }

  @Transactional
  public Recording stopRecording(Long recordingId) {
    Recording recording = getRecording(recordingId);
    if (recording.getEndedAt() == null) {
      recording.setEndedAt(Instant.now());
    }
    updateDuration(recording);
    recording.setStatus(RecordingStatus.UPLOADING);
    recorderClient.stop(recording);
    Recording saved = recordingRepository.save(recording);
    ensureJob(saved, JobType.STT);
    ensureJob(saved, JobType.SUMMARY);
    if (mockProcessing) {
      applyMockResults(saved);
    }
    return saved;
  }

  @Transactional
  public Recording handleRecorderComplete(RecordingCompleteRequest request) {
    Recording recording = getRecording(request.getRecordingId());
    recording.setVideoUrl(request.getVideoUrl());
    recording.setAudioUrl(request.getAudioUrl());
    if (recording.getEndedAt() == null) {
      recording.setEndedAt(Instant.now());
    }
    updateDuration(recording);
    recording.setStatus(RecordingStatus.UPLOADING);
    Recording saved = recordingRepository.save(recording);
    ensureJob(saved, JobType.STT);
    ensureJob(saved, JobType.SUMMARY);
    if (mockProcessing) {
      applyMockResults(saved);
    }
    return saved;
  }

  @Transactional
  public Transcript upsertTranscript(TranscriptUpsertRequest request) {
    Recording recording = getRecording(request.getRecordingId());
    Transcript transcript = transcriptRepository.findByRecordingId(recording.getId()).orElseGet(Transcript::new);
    transcript.setRecording(recording);
    transcript.setFullText(request.getFullText());
    transcript.setLanguage(request.getLanguage());
    transcript.setSttProvider(request.getSttProvider());
    Transcript saved = transcriptRepository.save(transcript);
    markJobSuccess(recording.getId(), JobType.STT);
    return saved;
  }

  @Transactional
  public Summary upsertSummary(SummaryUpsertRequest request) {
    Recording recording = getRecording(request.getRecordingId());
    Summary summary = summaryRepository.findByRecordingId(recording.getId()).orElseGet(Summary::new);
    summary.setRecording(recording);
    summary.setSummaryText(request.getSummaryText());
    summary.setModel(request.getModel());
    Summary saved = summaryRepository.save(summary);
    markJobSuccess(recording.getId(), JobType.SUMMARY);
    updateReadyStatus(recording.getId());
    return saved;
  }

  @Transactional(readOnly = true)
  public List<RecordingSummaryItem> listByRoom(String roomId) {
    return recordingRepository.findByRoomIdOrderByCreatedAtDesc(roomId).stream()
        .map(recording -> new RecordingSummaryItem(
            recording.getId(),
            recording.getStatus().name(),
            recording.getDurationSec(),
            recording.getStartedAt(),
            recording.getEndedAt()))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Optional<RecordingDetailResponse> getDetail(Long recordingId) {
    return recordingRepository.findById(recordingId).map(recording -> {
      RecordingDetailResponse response = new RecordingDetailResponse();
      response.setRecordingId(recording.getId());
      response.setRoomId(recording.getRoomId());
      response.setStatus(recording.getStatus().name());
      response.setVideoUrl(recording.getVideoUrl());
      response.setAudioUrl(recording.getAudioUrl());
      response.setDurationSec(recording.getDurationSec());
      response.setStartedAt(recording.getStartedAt());
      response.setEndedAt(recording.getEndedAt());
      transcriptRepository.findByRecordingId(recording.getId())
          .ifPresent(item -> response.setTranscript(item.getFullText()));
      summaryRepository.findByRecordingId(recording.getId())
          .ifPresent(item -> response.setSummary(item.getSummaryText()));
      return response;
    });
  }

  private Recording getRecording(Long recordingId) {
    return recordingRepository.findById(recordingId)
        .orElseThrow(() -> new IllegalArgumentException("Recording not found: " + recordingId));
  }

  private void updateDuration(Recording recording) {
    if (recording.getStartedAt() != null && recording.getEndedAt() != null) {
      long seconds = Duration.between(recording.getStartedAt(), recording.getEndedAt()).getSeconds();
      recording.setDurationSec(Math.toIntExact(Math.max(0, seconds)));
    }
  }

  private void ensureJob(Recording recording, JobType jobType) {
    jobRepository.findByRecordingIdAndJobType(recording.getId(), jobType).orElseGet(() -> {
      ProcessingJob job = new ProcessingJob();
      job.setRecording(recording);
      job.setJobType(jobType);
      job.setStatus(JobStatus.PENDING);
      job.setAttemptCount(0);
      return jobRepository.save(job);
    });
  }

  private void markJobSuccess(Long recordingId, JobType jobType) {
    jobRepository.findByRecordingIdAndJobType(recordingId, jobType).ifPresent(job -> {
      job.setStatus(JobStatus.SUCCESS);
      jobRepository.save(job);
    });
  }

  private void updateReadyStatus(Long recordingId) {
    Recording recording = getRecording(recordingId);
    boolean hasTranscript = transcriptRepository.findByRecordingId(recordingId).isPresent();
    boolean hasSummary = summaryRepository.findByRecordingId(recordingId).isPresent();
    if (hasTranscript && hasSummary) {
      recording.setStatus(RecordingStatus.READY);
      recordingRepository.save(recording);
    }
  }

  private void applyMockResults(Recording recording) {
    if (transcriptRepository.findByRecordingId(recording.getId()).isEmpty()) {
      Transcript transcript = new Transcript();
      transcript.setRecording(recording);
      transcript.setFullText("Mock transcript for recording " + recording.getId());
      transcript.setLanguage("en");
      transcript.setSttProvider("mock");
      transcriptRepository.save(transcript);
      markJobSuccess(recording.getId(), JobType.STT);
    }

    if (summaryRepository.findByRecordingId(recording.getId()).isEmpty()) {
      Summary summary = new Summary();
      summary.setRecording(recording);
      summary.setSummaryText("Mock summary for recording " + recording.getId());
      summary.setModel("mock");
      summaryRepository.save(summary);
      markJobSuccess(recording.getId(), JobType.SUMMARY);
    }

    recording.setStatus(RecordingStatus.READY);
    recordingRepository.save(recording);
    logger.info("Mock processing completed for recording {}", recording.getId());
  }
}
