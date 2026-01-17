package com.ssafy.domain.meeting.dto.response;

import java.time.LocalDateTime;

public record MeetingListItemResponse(
        Long id,
        String title,
        MeetingSessionResponse session,
        LocalDateTime startedAt,
        LocalDateTime endedAt,
        Integer durationSeconds,
        Integer participantCount,
        Boolean hasSummary,
        Boolean hasTranscript,
        Integer photoCount
) {
}
