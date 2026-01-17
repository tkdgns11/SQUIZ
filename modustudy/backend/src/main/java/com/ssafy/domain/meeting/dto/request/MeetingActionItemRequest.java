package com.ssafy.domain.meeting.dto.request;

public record MeetingActionItemRequest(
        String content,
        String assignee
) {
}
