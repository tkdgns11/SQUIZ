package com.ssafy.domain.meeting.dto.request;

import jakarta.validation.constraints.Size;

public record MeetingRequest(
        @Size(max = 200) String title,
        Long sessionId,
        Long channelId
) {
}
