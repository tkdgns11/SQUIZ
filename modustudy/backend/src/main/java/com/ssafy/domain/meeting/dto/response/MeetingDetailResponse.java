package com.ssafy.domain.meeting.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record MeetingDetailResponse(
        Long id,
        String title,
        MeetingSessionResponse session,
        MeetingChannelResponse channel,
        String meetingType,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        Integer durationSeconds,
        String status,
        String recordingStatus,
        String sttStatus,
        String summaryStatus,
        Boolean autoShareSummary,
        Long shareChannelId,
        List<MeetingParticipantResponse> participants,
        List<String> keywords,
        MeetingSummaryResponse summary
) {
}
