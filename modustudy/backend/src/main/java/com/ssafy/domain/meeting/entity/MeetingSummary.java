package com.ssafy.domain.meeting.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "meeting_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MeetingSummary extends BaseEntity {
    @Column(name = "meeting_id", nullable = false, unique = true)
    private Long meetingId;

    @Lob
    @Column(name = "summary")
    private String summary;

    @Lob
    @Column(name = "action_items")
    private String actionItemsJson;

    @Lob
    @Column(name = "keywords")
    private String keywordsJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "summary_status", nullable = false)
    private SummaryStatus summaryStatus;

    @Builder
    private MeetingSummary(Long meetingId, String summary, String actionItemsJson, String keywordsJson,
                           SummaryStatus summaryStatus) {
        this.meetingId = meetingId;
        this.summary = summary;
        this.actionItemsJson = actionItemsJson;
        this.keywordsJson = keywordsJson;
        this.summaryStatus = summaryStatus;
    }

    public static MeetingSummary createEmpty(Long meetingId) {
        return MeetingSummary.builder()
                .meetingId(meetingId)
                .actionItemsJson("[]")
                .keywordsJson("[]")
                .summaryStatus(SummaryStatus.PENDING)
                .build();
    }

    public void updateSummary(String summary) {
        this.summary = summary;
    }

    public void updateActionItemsJson(String actionItemsJson) {
        this.actionItemsJson = actionItemsJson;
    }

    public void updateKeywordsJson(String keywordsJson) {
        this.keywordsJson = keywordsJson;
    }

    public void updateSummaryStatus(SummaryStatus summaryStatus) {
        this.summaryStatus = summaryStatus;
    }
}
