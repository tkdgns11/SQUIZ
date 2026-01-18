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

    @Column(name = "start_ms")
    private Integer startMs;

    @Column(name = "end_ms")
    private Integer endMs;

    @Builder
    private MeetingTranscript(Long meetingId, Long userId, String content, Integer timestampSeconds,
                              Integer startMs, Integer endMs) {
        this.meetingId = meetingId;
        this.userId = userId;
        this.content = content;
        this.timestampSeconds = timestampSeconds;
        this.startMs = startMs;
        this.endMs = endMs;
    }
}
