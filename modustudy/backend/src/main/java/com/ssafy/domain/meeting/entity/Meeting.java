package com.ssafy.domain.meeting.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meeting")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Meeting extends BaseEntity {
    @Column(name = "study_id", nullable = false)
    private Long studyId;

    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "channel_id")
    private Long channelId;

    @Column(name = "title", length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type", nullable = false)
    private MeetingType meetingType;

    @Column(name = "started_at")
    private java.time.LocalDateTime startedAt;

    @Column(name = "ended_at")
    private java.time.LocalDateTime endedAt;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "participant_count")
    private Integer participantCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MeetingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "recording_status", nullable = false)
    private RecordingStatus recordingStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "stt_status", nullable = false)
    private SttStatus sttStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "summary_status", nullable = false)
    private SummaryStatus summaryStatus;

    @Column(name = "auto_share_summary", nullable = false)
    private Boolean autoShareSummary;

    @Column(name = "share_channel_id")
    private Long shareChannelId;

    @Builder
    private Meeting(Long studyId, Long sessionId, Long channelId, String title, MeetingType meetingType,
                    java.time.LocalDateTime startedAt, java.time.LocalDateTime endedAt,
                    Integer durationSeconds, Integer participantCount, MeetingStatus status,
                    RecordingStatus recordingStatus, SttStatus sttStatus, SummaryStatus summaryStatus,
                    Boolean autoShareSummary, Long shareChannelId) {
        this.studyId = studyId;
        this.sessionId = sessionId;
        this.channelId = channelId;
        this.title = title;
        this.meetingType = meetingType;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.durationSeconds = durationSeconds;
        this.participantCount = participantCount;
        this.status = status;
        this.recordingStatus = recordingStatus;
        this.sttStatus = sttStatus;
        this.summaryStatus = summaryStatus;
        this.autoShareSummary = autoShareSummary;
        this.shareChannelId = shareChannelId;
    }

    public static Meeting start(Long studyId, Long sessionId, Long channelId, String title, MeetingType meetingType,
                                boolean autoShareSummary, Long shareChannelId, java.time.LocalDateTime startedAt) {
        return Meeting.builder()
                .studyId(studyId)
                .sessionId(sessionId)
                .channelId(channelId)
                .title(title)
                .meetingType(meetingType)
                .startedAt(startedAt)
                .participantCount(0)
                .status(MeetingStatus.IN_PROGRESS)
                .recordingStatus(RecordingStatus.RECORDING)
                .sttStatus(SttStatus.PENDING)
                .summaryStatus(SummaryStatus.PENDING)
                .autoShareSummary(autoShareSummary)
                .shareChannelId(shareChannelId)
                .build();
    }

    public void end(java.time.LocalDateTime endedAt, int participantCount) {
        this.endedAt = endedAt;
        if (startedAt != null) {
            this.durationSeconds = (int) java.time.Duration.between(startedAt, endedAt).getSeconds();
        }
        this.participantCount = participantCount;
        this.status = MeetingStatus.ENDED;
    }

    public void updateParticipantCount(int participantCount) {
        this.participantCount = participantCount;
    }

    public void updateRecordingStatus(RecordingStatus recordingStatus) {
        this.recordingStatus = recordingStatus;
    }

    public void updateSttStatus(SttStatus sttStatus) {
        this.sttStatus = sttStatus;
    }

    public void updateSummaryStatus(SummaryStatus summaryStatus) {
        this.summaryStatus = summaryStatus;
    }

    public void updateShareOption(boolean autoShareSummary, Long shareChannelId) {
        this.autoShareSummary = autoShareSummary;
        this.shareChannelId = shareChannelId;
    }
}
