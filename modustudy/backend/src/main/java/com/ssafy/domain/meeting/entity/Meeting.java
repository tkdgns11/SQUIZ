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

    @Builder
    private Meeting(Long studyId, Long sessionId, Long channelId, String title,
                    java.time.LocalDateTime startedAt, java.time.LocalDateTime endedAt,
                    Integer durationSeconds, Integer participantCount, MeetingStatus status) {
        this.studyId = studyId;
        this.sessionId = sessionId;
        this.channelId = channelId;
        this.title = title;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.durationSeconds = durationSeconds;
        this.participantCount = participantCount;
        this.status = status;
    }

    public static Meeting start(Long studyId, Long sessionId, Long channelId, String title,
                                java.time.LocalDateTime startedAt) {
        return Meeting.builder()
                .studyId(studyId)
                .sessionId(sessionId)
                .channelId(channelId)
                .title(title)
                .startedAt(startedAt)
                .participantCount(0)
                .status(MeetingStatus.IN_PROGRESS)
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
}
