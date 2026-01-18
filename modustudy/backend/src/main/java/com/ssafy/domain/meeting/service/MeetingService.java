package com.ssafy.domain.meeting.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.domain.meeting.dto.request.MeetingActionItemRequest;
import com.ssafy.domain.meeting.dto.request.MeetingKeywordUpdateRequest;
import com.ssafy.domain.meeting.dto.request.MeetingParticipantSummaryRequest;
import com.ssafy.domain.meeting.dto.request.MeetingRecordingRequest;
import com.ssafy.domain.meeting.dto.request.MeetingRequest;
import com.ssafy.domain.meeting.dto.request.MeetingSummaryUpdateRequest;
import com.ssafy.domain.meeting.dto.request.MeetingTranscriptRequest;
import com.ssafy.domain.meeting.dto.response.*;
import com.ssafy.domain.meeting.entity.ActionItemStatus;
import com.ssafy.domain.meeting.entity.Meeting;
import com.ssafy.domain.meeting.entity.MeetingActionItem;
import com.ssafy.domain.meeting.entity.MeetingParticipant;
import com.ssafy.domain.meeting.entity.MeetingParticipantSummary;
import com.ssafy.domain.meeting.entity.MeetingPhoto;
import com.ssafy.domain.meeting.entity.MeetingRecording;
import com.ssafy.domain.meeting.entity.MeetingStatus;
import com.ssafy.domain.meeting.entity.MeetingSummary;
import com.ssafy.domain.meeting.entity.MeetingTranscript;
import com.ssafy.domain.meeting.entity.MeetingType;
import com.ssafy.domain.meeting.entity.RecordingStatus;
import com.ssafy.domain.meeting.entity.SttStatus;
import com.ssafy.domain.meeting.entity.SummaryStatus;
import com.ssafy.domain.meeting.repository.MeetingActionItemRepository;
import com.ssafy.domain.meeting.repository.MeetingParticipantRepository;
import com.ssafy.domain.meeting.repository.MeetingParticipantSummaryRepository;
import com.ssafy.domain.meeting.repository.MeetingPhotoRepository;
import com.ssafy.domain.meeting.repository.MeetingRecordingRepository;
import com.ssafy.domain.meeting.repository.MeetingRepository;
import com.ssafy.domain.meeting.repository.MeetingSummaryRepository;
import com.ssafy.domain.meeting.repository.MeetingTranscriptRepository;
import lombok.RequiredArgsConstructor;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final MeetingSummaryRepository meetingSummaryRepository;
    private final MeetingTranscriptRepository meetingTranscriptRepository;
    private final MeetingPhotoRepository meetingPhotoRepository;
    private final MeetingRecordingRepository meetingRecordingRepository;
    private final MeetingParticipantSummaryRepository meetingParticipantSummaryRepository;
    private final MeetingActionItemRepository meetingActionItemRepository;
    private final ObjectMapper objectMapper;
    @Value("${meeting.pdf.font-path:}")
    private String pdfFontPath;

    @Transactional(readOnly = true)
    public Page<MeetingListItemResponse> listMeetings(Long studyId, MeetingType meetingType,
                                                      LocalDate startDate, LocalDate endDate, Pageable pageable) {
        LocalDateTime startAt = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime endAt = endDate == null ? null : endDate.plusDays(1).atStartOfDay().minusNanos(1);
        return meetingRepository.searchMeetings(studyId, meetingType, startAt, endAt, pageable)
                .map(meeting -> new MeetingListItemResponse(
                        meeting.getId(),
                        meeting.getTitle(),
                        meeting.getSessionId() == null ? null : new MeetingSessionResponse(meeting.getSessionId(), null, null),
                        (meeting.getMeetingType() == null ? MeetingType.OTHER : meeting.getMeetingType()).name(),
                        meeting.getStartedAt(),
                        meeting.getEndedAt(),
                        meeting.getDurationSeconds(),
                        meeting.getParticipantCount(),
                        meetingSummaryRepository.existsByMeetingId(meeting.getId()),
                        meetingTranscriptRepository.existsByMeetingId(meeting.getId()),
                        meetingPhotoRepository.countByMeetingId(meeting.getId())
                ));
    }

    @Transactional(readOnly = true)
    public MeetingDetailResponse getMeetingDetail(Long studyId, Long meetingId) {
        Meeting meeting = getMeetingOrThrow(studyId, meetingId);
        List<MeetingParticipantResponse> participants = meetingParticipantRepository.findByMeetingId(meetingId).stream()
                .map(participant -> new MeetingParticipantResponse(
                        participant.getUserId(),
                        null,
                        participant.getJoinedAt(),
                        participant.getLeftAt()))
                .toList();

        MeetingSummary summary = meetingSummaryRepository.findByMeetingId(meetingId).orElse(null);
        SummaryStatus summaryStatus = resolveSummaryStatus(meeting);
        MeetingSummaryResponse summaryResponse = summary == null ? null : new MeetingSummaryResponse(
                summary.getId(),
                summary.getSummary(),
                parseActionItems(summary.getActionItemsJson()),
                parseKeywords(summary.getKeywordsJson()),
                summaryStatus.name(),
                summary.getCreatedAt()
        );

        return new MeetingDetailResponse(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getSessionId() == null ? null : new MeetingSessionResponse(meeting.getSessionId(), null, null),
                meeting.getChannelId() == null ? null : new MeetingChannelResponse(meeting.getChannelId(), null),
                (meeting.getMeetingType() == null ? MeetingType.OTHER : meeting.getMeetingType()).name(),
                meeting.getStartedAt(),
                meeting.getEndedAt(),
                meeting.getDurationSeconds(),
                meeting.getStatus().name(),
                meeting.getRecordingStatus().name(),
                meeting.getSttStatus().name(),
                summaryStatus.name(),
                meeting.getAutoShareSummary(),
                meeting.getShareChannelId(),
                participants,
                summary == null ? List.of() : parseKeywords(summary.getKeywordsJson()),
                summaryResponse
        );
    }

    @Transactional
    public MeetingResponse startMeeting(Long studyId, MeetingRequest request) {
        if (meetingRepository.existsByStudyIdAndStatus(studyId, MeetingStatus.IN_PROGRESS)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "MEETING_IN_PROGRESS");
        }
        MeetingType meetingType = request.meetingType() == null ? MeetingType.OTHER : request.meetingType();
        boolean autoShareSummary = Boolean.TRUE.equals(request.autoShareSummary());
        Meeting meeting = Meeting.start(studyId, request.sessionId(), request.channelId(),
                request.title(), meetingType, autoShareSummary, request.shareChannelId(), LocalDateTime.now());
        Meeting saved = meetingRepository.save(meeting);
        return new MeetingResponse(saved.getId(), saved.getTitle(), buildRoomToken(saved), saved.getStatus().name(),
                saved.getMeetingType().name(), saved.getRecordingStatus().name(), saved.getSttStatus().name(),
                resolveSummaryStatus(saved).name());
    }

    @Transactional
    public MeetingEndResponse endMeeting(Long studyId, Long meetingId) {
        Meeting meeting = getMeetingOrThrow(studyId, meetingId);
        if (meeting.getStatus() == MeetingStatus.ENDED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "MEETING_ALREADY_ENDED");
        }
        List<MeetingParticipant> participants = meetingParticipantRepository.findByMeetingId(meetingId);
        LocalDateTime endedAt = LocalDateTime.now();
        for (MeetingParticipant participant : participants) {
            if (participant.getLeftAt() == null) {
                participant.leave(endedAt);
            }
        }
        int participantCount = participants.size();
        meeting.end(endedAt, participantCount);
        meeting.updateSummaryStatus(SummaryStatus.PROCESSING);
        meetingSummaryRepository.findByMeetingId(meetingId)
                .orElseGet(() -> meetingSummaryRepository.save(MeetingSummary.createEmpty(meetingId)));
        return new MeetingEndResponse(meeting.getDurationSeconds(), meeting.getParticipantCount(),
                meeting.getSummaryStatus().name());
    }

    @Transactional
    public MeetingJoinResponse joinMeeting(Long studyId, Long meetingId, Long userId) {
        Meeting meeting = getMeetingOrThrow(studyId, meetingId);
        if (meeting.getStatus() == MeetingStatus.ENDED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "MEETING_ALREADY_ENDED");
        }
        MeetingParticipant participant = meetingParticipantRepository.findByMeetingIdAndUserId(meetingId, userId)
                .orElseGet(() -> MeetingParticipant.join(meetingId, userId, LocalDateTime.now()));
        if (participant.getId() != null) {
            participant.rejoin(LocalDateTime.now());
        }
        meetingParticipantRepository.save(participant);
        meeting.updateParticipantCount(meetingParticipantRepository.countByMeetingId(meetingId));
        return new MeetingJoinResponse(buildRoomToken(meeting), Collections.emptyList());
    }

    @Transactional
    public void leaveMeeting(Long studyId, Long meetingId, Long userId) {
        Meeting meeting = getMeetingOrThrow(studyId, meetingId);
        MeetingParticipant participant = meetingParticipantRepository.findByMeetingIdAndUserId(meetingId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "NOT_IN_MEETING"));
        participant.leave(LocalDateTime.now());
        meeting.updateParticipantCount(meetingParticipantRepository.countByMeetingId(meetingId));
    }

    @Transactional(readOnly = true)
    public MeetingSummaryResponse getSummary(Long studyId, Long meetingId) {
        Meeting meeting = getMeetingOrThrow(studyId, meetingId);
        MeetingSummary summary = meetingSummaryRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SUMMARY_NOT_READY"));
        SummaryStatus currentStatus = resolveSummaryStatus(meeting);
        if (currentStatus == SummaryStatus.PENDING && summary.getSummary() != null) {
            meeting.updateSummaryStatus(SummaryStatus.DONE);
        }
        if (resolveSummaryStatus(meeting) != SummaryStatus.DONE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SUMMARY_NOT_READY");
        }
        return new MeetingSummaryResponse(
                summary.getId(),
                summary.getSummary(),
                parseActionItems(summary.getActionItemsJson()),
                parseKeywords(summary.getKeywordsJson()),
                resolveSummaryStatus(meeting).name(),
                summary.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public MeetingTranscriptPageResponse getTranscripts(Long studyId, Long meetingId, Pageable pageable) {
        getMeetingOrThrow(studyId, meetingId);
        Page<MeetingTranscript> page = meetingTranscriptRepository
                .findByMeetingIdOrderByTimestampSecondsAsc(meetingId, pageable);
        List<MeetingTranscriptItemResponse> content = page.stream()
                .map(transcript -> new MeetingTranscriptItemResponse(
                        transcript.getId(),
                        new MeetingUserResponse(transcript.getUserId(), null),
                        transcript.getContent(),
                        transcript.getTimestampSeconds(),
                        transcript.getStartMs(),
                        transcript.getEndMs(),
                        transcript.getCreatedAt()))
                .toList();
        return new MeetingTranscriptPageResponse(content, page.getTotalElements(), page.hasNext());
    }

    @Transactional(readOnly = true)
    public List<MeetingPhotoResponse> getPhotos(Long studyId, Long meetingId) {
        getMeetingOrThrow(studyId, meetingId);
        return meetingPhotoRepository.findByMeetingIdOrderByCapturedAtDesc(meetingId).stream()
                .map(photo -> new MeetingPhotoResponse(
                        photo.getId(),
                        photo.getImageUrl(),
                        photo.getCapturedAt(),
                        photo.getIsSelected()))
                .toList();
    }

    @Transactional
    public MeetingPhotoResponse addPhoto(Long studyId, Long meetingId, MultipartFile image) {
        getMeetingOrThrow(studyId, meetingId);
        if (image == null || image.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IMAGE_REQUIRED");
        }
        String originalFilename = image.getOriginalFilename();
        String safeName = originalFilename == null ? "photo" : originalFilename.replace("\\", "_").replace("/", "_");
        String imageUrl = "meeting/" + meetingId + "/" + safeName;
        MeetingPhoto saved = meetingPhotoRepository.save(
                MeetingPhoto.capture(meetingId, imageUrl, LocalDateTime.now()));
        return new MeetingPhotoResponse(saved.getId(), saved.getImageUrl(), saved.getCapturedAt(), saved.getIsSelected());
    }

    @Transactional
    public void updateKeywords(Long studyId, Long meetingId, MeetingKeywordUpdateRequest request) {
        getMeetingOrThrow(studyId, meetingId);
        MeetingSummary summary = meetingSummaryRepository.findByMeetingId(meetingId)
                .orElseGet(() -> meetingSummaryRepository.save(MeetingSummary.createEmpty(meetingId)));
        summary.updateKeywordsJson(writeJson(request.keywords()));
    }

    @Transactional
    public void updateSummaryStatus(Long studyId, Long meetingId, SummaryStatus status) {
        Meeting meeting = getMeetingOrThrow(studyId, meetingId);
        meeting.updateSummaryStatus(status);
    }

    @Transactional
    public MeetingSummaryResponse upsertSummary(Long studyId, Long meetingId, MeetingSummaryUpdateRequest request) {
        Meeting meeting = getMeetingOrThrow(studyId, meetingId);
        MeetingSummary summary = meetingSummaryRepository.findByMeetingId(meetingId)
                .orElseGet(() -> meetingSummaryRepository.save(MeetingSummary.createEmpty(meetingId)));
        if (request.summary() != null) {
            summary.updateSummary(request.summary());
        }
        if (request.actionItems() != null) {
            summary.updateActionItemsJson(writeJson(request.actionItems()));
        }
        if (request.keywords() != null) {
            summary.updateKeywordsJson(writeJson(request.keywords()));
        }
        SummaryStatus status = request.status();
        if (status == null) {
            status = request.summary() == null ? resolveSummaryStatus(meeting) : SummaryStatus.DONE;
        }
        if (status != null) {
            meeting.updateSummaryStatus(status);
        }
        return new MeetingSummaryResponse(
                summary.getId(),
                summary.getSummary(),
                parseActionItems(summary.getActionItemsJson()),
                parseKeywords(summary.getKeywordsJson()),
                resolveSummaryStatus(meeting).name(),
                summary.getCreatedAt()
        );
    }

    @Transactional
    public void updateParticipantMute(Long studyId, Long meetingId, Long userId, boolean muted) {
        getMeetingOrThrow(studyId, meetingId);
        MeetingParticipant participant = meetingParticipantRepository.findByMeetingIdAndUserId(meetingId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "NOT_IN_MEETING"));
        participant.updateMute(muted);
    }

    @Transactional
    public MeetingTranscriptItemResponse addTranscript(Long studyId, Long meetingId, MeetingTranscriptRequest request) {
        Meeting meeting = getMeetingOrThrow(studyId, meetingId);
        MeetingTranscript saved = meetingTranscriptRepository.save(MeetingTranscript.builder()
                .meetingId(meetingId)
                .userId(request.userId())
                .content(request.content())
                .timestampSeconds(request.timestampSeconds())
                .startMs(request.startMs())
                .endMs(request.endMs())
                .build());
        if (meeting.getSttStatus() == SttStatus.PENDING) {
            meeting.updateSttStatus(SttStatus.PROCESSING);
        }
        if (Boolean.TRUE.equals(request.isFinal())) {
            meeting.updateSttStatus(SttStatus.DONE);
        }
        return new MeetingTranscriptItemResponse(
                saved.getId(),
                new MeetingUserResponse(saved.getUserId(), null),
                saved.getContent(),
                saved.getTimestampSeconds(),
                saved.getStartMs(),
                saved.getEndMs(),
                saved.getCreatedAt()
        );
    }

    @Transactional
    public MeetingTranscriptItemResponse addTranscript(Long studyId, Long meetingId, Long userId,
                                                       String content, Integer timestampSeconds) {
        MeetingTranscriptRequest request = new MeetingTranscriptRequest(userId, content, timestampSeconds, null, null, true);
        return addTranscript(studyId, meetingId, request);
    }

    @Transactional
    public MeetingRecordingResponse upsertRecording(Long studyId, Long meetingId, MeetingRecordingRequest request) {
        Meeting meeting = getMeetingOrThrow(studyId, meetingId);
        if (meetingRecordingRepository.findByMeetingId(meetingId).isEmpty()
                && (request.recordingUrl() == null || request.recordingUrl().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "RECORDING_URL_REQUIRED");
        }
        MeetingRecording recording = meetingRecordingRepository.findByMeetingId(meetingId)
                .orElseGet(() -> MeetingRecording.builder()
                        .meetingId(meetingId)
                        .recordingUrl(request.recordingUrl())
                        .format(request.format())
                        .durationSeconds(request.durationSeconds())
                        .startedAt(request.startedAt())
                        .endedAt(request.endedAt())
                        .fileSize(request.fileSize())
                        .status(request.status() == null ? RecordingStatus.UPLOADING : request.status())
                        .build());
        if (recording.getId() == null) {
            recording = meetingRecordingRepository.save(recording);
        } else {
            recording.updateDetails(request.recordingUrl(), request.format(), request.durationSeconds(),
                    request.startedAt(), request.endedAt(), request.fileSize());
        }
        if (request.status() != null) {
            recording.updateStatus(request.status());
        }
        if (request.status() == RecordingStatus.READY) {
            meeting.updateRecordingStatus(RecordingStatus.READY);
        }
        return new MeetingRecordingResponse(
                recording.getId(),
                recording.getRecordingUrl(),
                recording.getFormat(),
                recording.getDurationSeconds(),
                recording.getStartedAt(),
                recording.getEndedAt(),
                recording.getFileSize(),
                recording.getStatus().name(),
                recording.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public MeetingRecordingResponse getRecording(Long studyId, Long meetingId) {
        getMeetingOrThrow(studyId, meetingId);
        MeetingRecording recording = meetingRecordingRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RECORDING_NOT_FOUND"));
        return new MeetingRecordingResponse(
                recording.getId(),
                recording.getRecordingUrl(),
                recording.getFormat(),
                recording.getDurationSeconds(),
                recording.getStartedAt(),
                recording.getEndedAt(),
                recording.getFileSize(),
                recording.getStatus().name(),
                recording.getCreatedAt()
        );
    }

    @Transactional
    public List<MeetingParticipantSummaryResponse> upsertParticipantSummaries(Long studyId, Long meetingId,
                                                                              List<MeetingParticipantSummaryRequest> requests) {
        getMeetingOrThrow(studyId, meetingId);
        List<MeetingParticipantSummary> summaries = requests.stream()
                .map(request -> meetingParticipantSummaryRepository.findByMeetingIdAndUserId(meetingId, request.userId())
                        .map(existing -> {
                            existing.updateSummary(request.summary());
                            return existing;
                        })
                        .orElseGet(() -> meetingParticipantSummaryRepository.save(
                                MeetingParticipantSummary.builder()
                                        .meetingId(meetingId)
                                        .userId(request.userId())
                                        .summary(request.summary())
                                        .build())))
                .toList();
        return summaries.stream()
                .map(summary -> new MeetingParticipantSummaryResponse(
                        summary.getId(),
                        summary.getUserId(),
                        summary.getSummary(),
                        summary.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeetingParticipantSummaryResponse> getParticipantSummaries(Long studyId, Long meetingId) {
        getMeetingOrThrow(studyId, meetingId);
        return meetingParticipantSummaryRepository.findByMeetingId(meetingId).stream()
                .map(summary -> new MeetingParticipantSummaryResponse(
                        summary.getId(),
                        summary.getUserId(),
                        summary.getSummary(),
                        summary.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeetingActionItemResponse> getActionItems(Long studyId, Long meetingId) {
        getMeetingOrThrow(studyId, meetingId);
        return meetingActionItemRepository.findByMeetingId(meetingId).stream()
                .map(item -> new MeetingActionItemResponse(
                        item.getId(),
                        item.getContent(),
                        item.getAssigneeId(),
                        item.getStatus()))
                .toList();
    }

    @Transactional
    public MeetingActionItemResponse addActionItem(Long studyId, Long meetingId, MeetingActionItemRequest request) {
        getMeetingOrThrow(studyId, meetingId);
        if (request.content() == null || request.content().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ACTION_ITEM_CONTENT_REQUIRED");
        }
        ActionItemStatus status = request.status() == null ? ActionItemStatus.TODO : request.status();
        MeetingActionItem saved = meetingActionItemRepository.save(MeetingActionItem.builder()
                .meetingId(meetingId)
                .content(request.content())
                .assigneeId(request.assigneeId())
                .status(status)
                .build());
        return new MeetingActionItemResponse(
                saved.getId(),
                saved.getContent(),
                saved.getAssigneeId(),
                saved.getStatus());
    }

    @Transactional
    public MeetingActionItemResponse updateActionItem(Long studyId, Long meetingId, Long actionItemId,
                                                      MeetingActionItemRequest request) {
        getMeetingOrThrow(studyId, meetingId);
        MeetingActionItem item = meetingActionItemRepository.findById(actionItemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ACTION_ITEM_NOT_FOUND"));
        if (!item.getMeetingId().equals(meetingId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ACTION_ITEM_MISMATCH");
        }
        if (request.assigneeId() != null) {
            item.updateAssignee(request.assigneeId());
        }
        if (request.status() != null) {
            item.updateStatus(request.status());
        }
        if (request.content() != null) {
            item.updateContent(request.content());
        }
        return new MeetingActionItemResponse(
                item.getId(),
                item.getContent(),
                item.getAssigneeId(),
                item.getStatus());
    }

    @Transactional(readOnly = true)
    public String exportMeetingMarkdown(Long studyId, Long meetingId) {
        Meeting meeting = getMeetingOrThrow(studyId, meetingId);
        MeetingSummary summary = meetingSummaryRepository.findByMeetingId(meetingId).orElse(null);
        StringBuilder builder = new StringBuilder();
        builder.append("# Meeting Summary").append("\n\n");
        builder.append("- Title: ").append(meeting.getTitle()).append("\n");
        builder.append("- Type: ").append(meeting.getMeetingType().name()).append("\n");
        builder.append("- Started At: ").append(meeting.getStartedAt()).append("\n");
        builder.append("- Ended At: ").append(meeting.getEndedAt()).append("\n\n");
        if (summary != null && summary.getSummary() != null) {
            builder.append("## Overall Summary").append("\n").append(summary.getSummary()).append("\n\n");
        }
        List<MeetingParticipantSummary> participantSummaries = meetingParticipantSummaryRepository.findByMeetingId(meetingId);
        if (!participantSummaries.isEmpty()) {
            builder.append("## Participant Summaries").append("\n");
            for (MeetingParticipantSummary participantSummary : participantSummaries) {
                builder.append("- User ").append(participantSummary.getUserId()).append(": ")
                        .append(participantSummary.getSummary()).append("\n");
            }
            builder.append("\n");
        }
        List<MeetingActionItem> actionItems = meetingActionItemRepository.findByMeetingId(meetingId);
        if (!actionItems.isEmpty()) {
            builder.append("## Action Items").append("\n");
            for (MeetingActionItem item : actionItems) {
                builder.append("- [").append(item.getStatus().name()).append("] ")
                        .append(item.getContent());
                if (item.getAssigneeId() != null) {
                    builder.append(" (assignee: ").append(item.getAssigneeId()).append(")");
                }
                builder.append("\n");
            }
            builder.append("\n");
        }
        builder.append("## Transcripts").append("\n");
        List<MeetingTranscript> transcripts = meetingTranscriptRepository
                .findByMeetingIdOrderByTimestampSecondsAsc(meetingId);
        for (MeetingTranscript transcript : transcripts) {
            builder.append("- [").append(transcript.getTimestampSeconds()).append("s] ")
                    .append("User ").append(transcript.getUserId()).append(": ")
                    .append(transcript.getContent()).append("\n");
        }
        return builder.toString();
    }

    @Transactional(readOnly = true)
    public byte[] exportMeetingPdf(Long studyId, Long meetingId) {
        String markdown = exportMeetingMarkdown(studyId, meetingId);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            BaseFont baseFont = resolvePdfBaseFont();
            Font titleFont = new Font(baseFont, 16, Font.BOLD);
            Font sectionFont = new Font(baseFont, 13, Font.BOLD);
            Font bodyFont = new Font(baseFont, 11);
            for (String line : markdown.split("\n")) {
                if (line.startsWith("# ")) {
                    document.add(new Paragraph(line.substring(2), titleFont));
                } else if (line.startsWith("## ")) {
                    Paragraph paragraph = new Paragraph(line.substring(3), sectionFont);
                    paragraph.setSpacingBefore(10f);
                    document.add(paragraph);
                } else {
                    Paragraph paragraph = new Paragraph(line, bodyFont);
                    paragraph.setLeading(0f, 1.4f);
                    paragraph.setAlignment(Element.ALIGN_LEFT);
                    document.add(paragraph);
                }
            }
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "PDF_EXPORT_FAILED");
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "PDF_FONT_LOAD_FAILED");
        }
    }

    private Meeting getMeetingOrThrow(Long studyId, Long meetingId) {
        return meetingRepository.findByIdAndStudyId(meetingId, studyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MEETING_NOT_FOUND"));
    }

    private List<String> parseKeywords(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            return List.of();
        }
    }

    private List<MeetingActionItemResponse> parseActionItems(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<MeetingActionItemResponse>>() {});
        } catch (IOException e) {
            return List.of();
        }
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(Objects.requireNonNullElse(value, List.of()));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "JSON_SERIALIZE_FAILED");
        }
    }

    private String buildRoomToken(Meeting meeting) {
        return "meeting-" + meeting.getId();
    }

    private SummaryStatus resolveSummaryStatus(Meeting meeting) {
        return meeting.getSummaryStatus() == null ? SummaryStatus.PENDING : meeting.getSummaryStatus();
    }

    private BaseFont resolvePdfBaseFont() throws IOException, DocumentException {
        if (pdfFontPath != null && !pdfFontPath.isBlank()) {
            return BaseFont.createFont(pdfFontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        }
        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
    }
}
