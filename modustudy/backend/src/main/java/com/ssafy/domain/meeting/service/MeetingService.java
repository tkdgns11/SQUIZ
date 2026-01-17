package com.ssafy.domain.meeting.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.domain.meeting.dto.request.MeetingKeywordUpdateRequest;
import com.ssafy.domain.meeting.dto.request.MeetingRequest;
import com.ssafy.domain.meeting.dto.request.MeetingSummaryUpdateRequest;
import com.ssafy.domain.meeting.dto.request.MeetingTranscriptRequest;
import com.ssafy.domain.meeting.dto.response.*;
import com.ssafy.domain.meeting.entity.Meeting;
import com.ssafy.domain.meeting.entity.MeetingParticipant;
import com.ssafy.domain.meeting.entity.MeetingPhoto;
import com.ssafy.domain.meeting.entity.MeetingStatus;
import com.ssafy.domain.meeting.entity.MeetingSummary;
import com.ssafy.domain.meeting.entity.MeetingTranscript;
import com.ssafy.domain.meeting.entity.SummaryStatus;
import com.ssafy.domain.meeting.repository.MeetingParticipantRepository;
import com.ssafy.domain.meeting.repository.MeetingPhotoRepository;
import com.ssafy.domain.meeting.repository.MeetingRepository;
import com.ssafy.domain.meeting.repository.MeetingSummaryRepository;
import com.ssafy.domain.meeting.repository.MeetingTranscriptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
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
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Page<MeetingListItemResponse> listMeetings(Long studyId, Pageable pageable) {
        return meetingRepository.findByStudyId(studyId, pageable)
                .map(meeting -> new MeetingListItemResponse(
                        meeting.getId(),
                        meeting.getTitle(),
                        meeting.getSessionId() == null ? null : new MeetingSessionResponse(meeting.getSessionId(), null, null),
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
        MeetingSummaryResponse summaryResponse = summary == null ? null : new MeetingSummaryResponse(
                summary.getId(),
                summary.getSummary(),
                parseActionItems(summary.getActionItemsJson()),
                parseKeywords(summary.getKeywordsJson()),
                summary.getSummaryStatus().name(),
                summary.getCreatedAt()
        );

        return new MeetingDetailResponse(
                meeting.getId(),
                meeting.getTitle(),
                meeting.getSessionId() == null ? null : new MeetingSessionResponse(meeting.getSessionId(), null, null),
                meeting.getChannelId() == null ? null : new MeetingChannelResponse(meeting.getChannelId(), null),
                meeting.getStartedAt(),
                meeting.getEndedAt(),
                meeting.getDurationSeconds(),
                meeting.getStatus().name(),
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
        Meeting meeting = Meeting.start(studyId, request.sessionId(), request.channelId(),
                request.title(), LocalDateTime.now());
        Meeting saved = meetingRepository.save(meeting);
        return new MeetingResponse(saved.getId(), saved.getTitle(), buildRoomToken(saved), saved.getStatus().name());
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
        MeetingSummary summary = meetingSummaryRepository.findByMeetingId(meetingId)
                .orElseGet(() -> meetingSummaryRepository.save(MeetingSummary.createEmpty(meetingId)));
        if (summary.getSummaryStatus() == SummaryStatus.PENDING || summary.getSummaryStatus() == null) {
            summary.updateSummaryStatus(SummaryStatus.PROCESSING);
        }
        String summaryStatus = summary.getSummaryStatus().name();
        return new MeetingEndResponse(meeting.getDurationSeconds(), meeting.getParticipantCount(), summaryStatus);
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
        getMeetingOrThrow(studyId, meetingId);
        MeetingSummary summary = meetingSummaryRepository.findByMeetingId(meetingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SUMMARY_NOT_READY"));
        if (summary.getSummaryStatus() == null && summary.getSummary() != null) {
            summary.updateSummaryStatus(SummaryStatus.COMPLETED);
        }
        if (summary.getSummaryStatus() != SummaryStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "SUMMARY_NOT_READY");
        }
        return new MeetingSummaryResponse(
                summary.getId(),
                summary.getSummary(),
                parseActionItems(summary.getActionItemsJson()),
                parseKeywords(summary.getKeywordsJson()),
                summary.getSummaryStatus().name(),
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
        getMeetingOrThrow(studyId, meetingId);
        MeetingSummary summary = meetingSummaryRepository.findByMeetingId(meetingId)
                .orElseGet(() -> meetingSummaryRepository.save(MeetingSummary.createEmpty(meetingId)));
        summary.updateSummaryStatus(status);
    }

    @Transactional
    public MeetingSummaryResponse upsertSummary(Long studyId, Long meetingId, MeetingSummaryUpdateRequest request) {
        getMeetingOrThrow(studyId, meetingId);
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
            status = request.summary() == null ? summary.getSummaryStatus() : SummaryStatus.COMPLETED;
        }
        if (status != null) {
            summary.updateSummaryStatus(status);
        }
        return new MeetingSummaryResponse(
                summary.getId(),
                summary.getSummary(),
                parseActionItems(summary.getActionItemsJson()),
                parseKeywords(summary.getKeywordsJson()),
                summary.getSummaryStatus().name(),
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
        getMeetingOrThrow(studyId, meetingId);
        MeetingTranscript saved = meetingTranscriptRepository.save(MeetingTranscript.builder()
                .meetingId(meetingId)
                .userId(request.userId())
                .content(request.content())
                .timestampSeconds(request.timestampSeconds())
                .build());
        return new MeetingTranscriptItemResponse(
                saved.getId(),
                new MeetingUserResponse(saved.getUserId(), null),
                saved.getContent(),
                saved.getTimestampSeconds(),
                saved.getCreatedAt()
        );
    }

    @Transactional
    public MeetingTranscriptItemResponse addTranscript(Long studyId, Long meetingId, Long userId,
                                                       String content, Integer timestampSeconds) {
        MeetingTranscriptRequest request = new MeetingTranscriptRequest(userId, content, timestampSeconds, true);
        return addTranscript(studyId, meetingId, request);
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
}
