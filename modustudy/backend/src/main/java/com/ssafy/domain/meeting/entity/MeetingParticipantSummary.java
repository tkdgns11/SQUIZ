package com.ssafy.domain.meeting.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meeting_participant_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingParticipantSummary extends BaseEntity {
    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Lob
    @Column(name = "summary", nullable = false)
    private String summary;

    @Builder
    private MeetingParticipantSummary(Long meetingId, Long userId, String summary) {
        this.meetingId = meetingId;
        this.userId = userId;
        this.summary = summary;
    }

    public void updateSummary(String summary) {
        this.summary = summary;
    }
}
