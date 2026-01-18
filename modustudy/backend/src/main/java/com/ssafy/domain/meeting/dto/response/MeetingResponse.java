package com.ssafy.domain.meeting.dto.response;

public record MeetingResponse(
        Long id,
        String title,
        String roomToken,
        String status,
        String meetingType,
        String recordingStatus,
        String sttStatus,
        String summaryStatus
) {
}
