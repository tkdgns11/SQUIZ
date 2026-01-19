package com.ssafy.domain.meeting.controller;

import com.ssafy.common.websocket.WebSocketMessageBrokerSettings;
import com.ssafy.domain.meeting.dto.request.MeetingTranscriptRequest;
import com.ssafy.domain.meeting.dto.response.MeetingSttDataResponse;
import com.ssafy.domain.meeting.dto.response.MeetingSttMessageResponse;
import com.ssafy.domain.meeting.dto.response.MeetingTranscriptItemResponse;
import com.ssafy.domain.meeting.service.MeetingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
// 테스트 실행:
//
//  cd backend
//  .\gradlew.bat test --tests "com.ssafy.squiz.integration.api.MeetingApiTest"
@Controller
@RequiredArgsConstructor
public class MeetingSttController {

    private final MeetingService meetingService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/studies/{studyId}/meetings/{meetingId}/stt")
    // STT 메시지를 저장하고 구독 채널로 브로드캐스트한다.
    public void handleStt(
            @DestinationVariable Long studyId,
            @DestinationVariable Long meetingId,
            @Valid MeetingTranscriptRequest request
    ) {
        MeetingTranscriptItemResponse saved = meetingService.addTranscript(studyId, meetingId, request);
        MeetingSttDataResponse data = new MeetingSttDataResponse(
                saved.user().id(),
                saved.user().nickname(),
                saved.content(),
                request.isFinal(),
                saved.timestampSeconds(),
                saved.startMs(),
                saved.endMs()
        );
        MeetingSttMessageResponse message = new MeetingSttMessageResponse("STT", data);
        messagingTemplate.convertAndSend(
                WebSocketMessageBrokerSettings.TOPIC_PREFIX + "/studies/" + studyId + "/meetings/" + meetingId + "/stt",
                message
        );
    }
}
