package com.ssafy.domain.meeting.controller;

import com.ssafy.common.websocket.WebSocketMessageBrokerSettings;
import com.ssafy.domain.meeting.dto.request.MeetingTranscriptRequest;
import com.ssafy.domain.meeting.dto.response.MeetingSttMessageResponse;
import com.ssafy.domain.meeting.dto.response.MeetingTranscriptItemResponse;
import com.ssafy.domain.meeting.dto.response.MeetingUserResponse;
import com.ssafy.domain.meeting.service.MeetingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class MeetingSttControllerTest {

    @Autowired
    private MeetingSttController meetingSttController;

    @MockBean
    private MeetingService meetingService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    @DisplayName("STT 메시지 수신 시 저장 후 브로드캐스트")
    void handleStt() {
        MeetingTranscriptRequest request = new MeetingTranscriptRequest(
                1L,
                "hello",
                120,
                120000,
                121000,
                true
        );
        MeetingTranscriptItemResponse saved = new MeetingTranscriptItemResponse(
                10L,
                new MeetingUserResponse(1L, "user"),
                "hello",
                120,
                120000,
                121000,
                LocalDateTime.of(2025, 1, 15, 19, 2)
        );
        when(meetingService.addTranscript(1L, 2L, request)).thenReturn(saved);

        meetingSttController.handleStt(1L, 2L, request);

        ArgumentCaptor<MeetingSttMessageResponse> messageCaptor = ArgumentCaptor.forClass(MeetingSttMessageResponse.class);
        verify(messagingTemplate).convertAndSend(
                eq(WebSocketMessageBrokerSettings.TOPIC_PREFIX + "/studies/1/meetings/2/stt"),
                messageCaptor.capture()
        );
        MeetingSttMessageResponse message = messageCaptor.getValue();
        assertEquals("STT", message.type());
        assertEquals("hello", message.data().content());
        assertEquals(true, message.data().isFinal());
    }
}
