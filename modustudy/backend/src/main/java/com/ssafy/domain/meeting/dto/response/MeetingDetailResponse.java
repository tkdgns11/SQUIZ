package com.ssafy.domain.meeting.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record MeetingDetailResponse(
        Long id,
        String title,
        MeetingSessionResponse session,
        MeetingChannelResponse channel,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        Integer durationSeconds,
        String status,
        List<MeetingParticipantResponse> participants,
        List<String> keywords,
        MeetingSummaryResponse summary
) {
}
