package com.ssafy.squiz.integration.api;

import com.ssafy.common.websocket.WebSocketMessageBrokerSettings;
import com.ssafy.domain.meeting.controller.MeetingSttController;
import com.ssafy.domain.meeting.dto.request.MeetingTranscriptRequest;
import com.ssafy.domain.meeting.dto.response.MeetingSttMessageResponse;
import com.ssafy.domain.meeting.dto.response.MeetingTranscriptItemResponse;
import com.ssafy.domain.meeting.dto.response.MeetingUserResponse;
import com.ssafy.domain.meeting.service.MeetingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// 테스트 실행:
//
//  cd backend
//  .\gradlew.bat test --tests "com.ssafy.squiz.integration.api.MeetingSttApiTest"

@SpringBootTest(classes = MeetingSttApiTest.TestApplication.class)
class MeetingSttApiTest {

    @Autowired
    private MeetingSttController meetingSttController;

    @MockBean
    private MeetingService meetingService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    @DisplayName("STT 메시지 수신 후 브로드캐스트")
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

        ArgumentCaptor<MeetingSttMessageResponse> messageCaptor =
                ArgumentCaptor.forClass(MeetingSttMessageResponse.class);
        verify(messagingTemplate).convertAndSend(
                eq(WebSocketMessageBrokerSettings.TOPIC_PREFIX + "/studies/1/meetings/2/stt"),
                messageCaptor.capture()
        );
        MeetingSttMessageResponse message = messageCaptor.getValue();
        assertEquals("STT", message.type());
        assertEquals("hello", message.data().content());
        assertEquals(true, message.data().isFinal());
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            JpaRepositoriesAutoConfiguration.class
    })
    @Import(MeetingSttController.class)
    static class TestApplication {
    }
}
