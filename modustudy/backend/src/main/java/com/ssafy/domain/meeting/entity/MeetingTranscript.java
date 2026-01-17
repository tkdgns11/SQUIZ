package com.ssafy.domain.meeting.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meeting_transcript")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingTranscript extends BaseEntity {
    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "timestamp_seconds", nullable = false)
    private Integer timestampSeconds;

    @Builder
    private MeetingTranscript(Long meetingId, Long userId, String content, Integer timestampSeconds) {
        this.meetingId = meetingId;
        this.userId = userId;
        this.content = content;
        this.timestampSeconds = timestampSeconds;
    }
}
