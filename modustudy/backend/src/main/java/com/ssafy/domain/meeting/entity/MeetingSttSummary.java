package com.ssafy.domain.meeting.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "meeting_stt_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingSttSummary extends BaseEntity {
    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(name = "user_id")
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "track_type", nullable = false, length = 20)
    private MeetingTextTrackType trackType;

    @Column(name = "file_url", nullable = false, length = 500)
    private String fileUrl;

    @Lob
    @Column(name = "action_items")
    private String actionItemsJson;

    @Lob
    @Column(name = "keywords")
    private String keywordsJson;

    @Builder
    private MeetingSttSummary(Long meetingId, Long userId, MeetingTextTrackType trackType, String fileUrl,
                              String actionItemsJson, String keywordsJson) {
        this.meetingId = meetingId;
        this.userId = userId;
        this.trackType = trackType;
        this.fileUrl = fileUrl;
        this.actionItemsJson = actionItemsJson;
        this.keywordsJson = keywordsJson;
    }

    public void updateFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void updateActionItemsJson(String actionItemsJson) {
        this.actionItemsJson = actionItemsJson;
    }

    public void updateKeywordsJson(String keywordsJson) {
        this.keywordsJson = keywordsJson;
    }
}
