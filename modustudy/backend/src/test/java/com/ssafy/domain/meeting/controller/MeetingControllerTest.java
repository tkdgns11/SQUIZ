package com.ssafy.domain.meeting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.common.auth.SsafyUserDetails;
import com.ssafy.domain.meeting.dto.request.MeetingActionItemRequest;
import com.ssafy.domain.meeting.dto.request.MeetingKeywordUpdateRequest;
import com.ssafy.domain.meeting.dto.request.MeetingParticipantSummaryRequest;
import com.ssafy.domain.meeting.dto.request.MeetingRecordingRequest;
import com.ssafy.domain.meeting.dto.request.MeetingRequest;
import com.ssafy.domain.meeting.dto.request.MeetingSummaryUpdateRequest;
import com.ssafy.domain.meeting.dto.request.MeetingTranscriptRequest;
import com.ssafy.domain.meeting.dto.response.MeetingActionItemResponse;
import com.ssafy.domain.meeting.dto.response.MeetingChannelResponse;
import com.ssafy.domain.meeting.dto.response.MeetingDetailResponse;
import com.ssafy.domain.meeting.dto.response.MeetingEndResponse;
import com.ssafy.domain.meeting.dto.response.MeetingJoinResponse;
import com.ssafy.domain.meeting.dto.response.MeetingListItemResponse;
import com.ssafy.domain.meeting.dto.response.MeetingParticipantResponse;
import com.ssafy.domain.meeting.dto.response.MeetingParticipantSummaryResponse;
import com.ssafy.domain.meeting.dto.response.MeetingPhotoResponse;
import com.ssafy.domain.meeting.dto.response.MeetingRecordingResponse;
import com.ssafy.domain.meeting.dto.response.MeetingResponse;
import com.ssafy.domain.meeting.dto.response.MeetingSessionResponse;
import com.ssafy.domain.meeting.dto.response.MeetingSummaryResponse;
import com.ssafy.domain.meeting.dto.response.MeetingTranscriptItemResponse;
import com.ssafy.domain.meeting.dto.response.MeetingTranscriptPageResponse;
import com.ssafy.domain.meeting.dto.response.MeetingUserResponse;
import com.ssafy.domain.meeting.entity.ActionItemStatus;
import com.ssafy.domain.meeting.entity.MeetingType;
import com.ssafy.domain.meeting.entity.SummaryStatus;
import com.ssafy.domain.meeting.service.MeetingService;
import com.ssafy.db.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class MeetingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MeetingService meetingService;

    @Test
    @DisplayName("미팅 목록 조회: 유형/날짜 필터 포함")
    void listMeetings() throws Exception {
        MeetingListItemResponse item = new MeetingListItemResponse(
                1L,
                "weekly",
                new MeetingSessionResponse(10L, 1, "session"),
                "WEEKLY",
                LocalDateTime.of(2025, 1, 15, 19, 0),
                LocalDateTime.of(2025, 1, 15, 20, 30),
                5400,
                5,
                true,
                true,
                2
        );
        PageImpl<MeetingListItemResponse> page = new PageImpl<>(
                List.of(item),
                PageRequest.of(0, 20),
                1
        );
        when(meetingService.listMeetings(eq(1L), eq(MeetingType.DAILY),
                eq(LocalDate.parse("2025-01-01")), eq(LocalDate.parse("2025-01-31")),
                any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/studies/1/meetings")
                        .param("meetingType", "DAILY")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-31")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].meetingType").value("WEEKLY"));
    }

    @Test
    @DisplayName("미팅 상세 조회: 상태/요약/참가자 포함")
    void getMeetingDetail() throws Exception {
        MeetingSummaryResponse summary = new MeetingSummaryResponse(
                1L,
                "summary",
                List.of(new MeetingActionItemResponse(1L, "todo", null, ActionItemStatus.TODO)),
                List.of("DP"),
                "DONE",
                LocalDateTime.of(2025, 1, 15, 20, 40)
        );
        MeetingDetailResponse detail = new MeetingDetailResponse(
                1L,
                "title",
                new MeetingSessionResponse(10L, 1, "session"),
                new MeetingChannelResponse(2L, "voice"),
                "DAILY",
                LocalDateTime.of(2025, 1, 15, 19, 0),
                LocalDateTime.of(2025, 1, 15, 20, 30),
                5400,
                "ENDED",
                "READY",
                "DONE",
                "DONE",
                true,
                12L,
                List.of(new MeetingParticipantResponse(1L, "user", LocalDateTime.now(), null)),
                List.of("DP"),
                summary
        );
        when(meetingService.getMeetingDetail(1L, 1L)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/studies/1/meetings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.meetingType").value("DAILY"))
                .andExpect(jsonPath("$.data.summary.status").value("DONE"));
    }

    @Test
    @DisplayName("미팅 시작: 유형/자동공유 옵션 포함")
    void startMeeting() throws Exception {
        MeetingResponse response = new MeetingResponse(
                1L,
                "title",
                "meeting-1",
                "IN_PROGRESS",
                "DAILY",
                "RECORDING",
                "PENDING",
                "PENDING"
        );
        when(meetingService.startMeeting(eq(1L), any())).thenReturn(response);

        MeetingRequest request = new MeetingRequest("title", 10L, 2L, MeetingType.DAILY, true, 12L);
        mockMvc.perform(post("/api/v1/studies/1/meetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data.recordingStatus").value("RECORDING"));
    }

    @Test
    @DisplayName("미팅 종료: 요약 상태 반환")
    void endMeeting() throws Exception {
        when(meetingService.endMeeting(1L, 2L)).thenReturn(new MeetingEndResponse(5400, 5, "PROCESSING"));

        mockMvc.perform(put("/api/v1/studies/1/meetings/2/end"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.summaryStatus").value("PROCESSING"));
    }

    @Test
    @DisplayName("미팅 참여/퇴장")
    void joinAndLeaveMeeting() throws Exception {
        when(meetingService.joinMeeting(1L, 2L, 1L))
                .thenReturn(new MeetingJoinResponse("meeting-2", List.of()));
        doNothing().when(meetingService).leaveMeeting(1L, 2L, 1L);

        mockMvc.perform(post("/api/v1/studies/1/meetings/2/join")
                        .with(authentication(authUser(1L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roomToken").value("meeting-2"));

        mockMvc.perform(post("/api/v1/studies/1/meetings/2/leave")
                        .with(authentication(authUser(1L))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("요약 조회/업데이트")
    void summaryEndpoints() throws Exception {
        MeetingSummaryResponse summary = new MeetingSummaryResponse(
                1L,
                "summary",
                List.of(new MeetingActionItemResponse(1L, "todo", null, ActionItemStatus.TODO)),
                List.of("DP"),
                "DONE",
                LocalDateTime.of(2025, 1, 15, 20, 40)
        );
        when(meetingService.getSummary(1L, 2L)).thenReturn(summary);
        when(meetingService.upsertSummary(eq(1L), eq(2L), any())).thenReturn(summary);

        mockMvc.perform(get("/api/v1/studies/1/meetings/2/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DONE"));

        MeetingSummaryUpdateRequest updateRequest = new MeetingSummaryUpdateRequest(
                "summary",
                List.of(new MeetingActionItemRequest("todo", null, ActionItemStatus.TODO)),
                List.of("DP"),
                SummaryStatus.DONE
        );
        mockMvc.perform(put("/api/v1/studies/1/meetings/2/summary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.summary").value("summary"));
    }

    @Test
    @DisplayName("전사 조회/저장: startMs/endMs 포함")
    void transcriptEndpoints() throws Exception {
        MeetingTranscriptItemResponse item = new MeetingTranscriptItemResponse(
                1L,
                new MeetingUserResponse(1L, "user"),
                "hello",
                120,
                120000,
                121000,
                LocalDateTime.of(2025, 1, 15, 19, 2)
        );
        MeetingTranscriptPageResponse page = new MeetingTranscriptPageResponse(
                List.of(item),
                1,
                false
        );
        when(meetingService.getTranscripts(eq(1L), eq(2L), any())).thenReturn(page);
        when(meetingService.addTranscript(eq(1L), eq(2L), any())).thenReturn(item);

        mockMvc.perform(get("/api/v1/studies/1/meetings/2/transcript")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].startMs").value(120000));

        MeetingTranscriptRequest request = new MeetingTranscriptRequest(1L, "hello", 120, 120000, 121000, true);
        mockMvc.perform(post("/api/v1/studies/1/meetings/2/transcript")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.content").value("hello"));
    }

    @Test
    @DisplayName("사진 조회/등록")
    void photoEndpoints() throws Exception {
        MeetingPhotoResponse photo = new MeetingPhotoResponse(1L, "meeting/2/photo.png",
                LocalDateTime.of(2025, 1, 15, 20, 10), false);
        when(meetingService.getPhotos(1L, 2L)).thenReturn(List.of(photo));
        when(meetingService.addPhoto(eq(1L), eq(2L), any())).thenReturn(photo);

        mockMvc.perform(get("/api/v1/studies/1/meetings/2/photos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].imageUrl").value("meeting/2/photo.png"));

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "photo.png",
                MediaType.IMAGE_PNG_VALUE,
                "png".getBytes(StandardCharsets.UTF_8)
        );
        mockMvc.perform(multipart("/api/v1/studies/1/meetings/2/photos")
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @DisplayName("키워드 업데이트/음소거")
    void updateKeywordsAndMute() throws Exception {
        doNothing().when(meetingService).updateKeywords(eq(1L), eq(2L), any());
        doNothing().when(meetingService).updateParticipantMute(1L, 2L, 1L, true);

        MeetingKeywordUpdateRequest request = new MeetingKeywordUpdateRequest(List.of("DP"));
        mockMvc.perform(put("/api/v1/studies/1/meetings/2/keywords")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Keywords updated"));

        mockMvc.perform(put("/api/v1/studies/1/meetings/2/participants/1/mute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"muted\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Participant updated"));
    }

    @Test
    @DisplayName("녹음 조회/저장")
    void recordingEndpoints() throws Exception {
        MeetingRecordingResponse response = new MeetingRecordingResponse(
                1L,
                "s3://bucket/meeting.wav",
                "wav",
                5400,
                LocalDateTime.of(2025, 1, 15, 19, 0),
                LocalDateTime.of(2025, 1, 15, 20, 30),
                123456L,
                "READY",
                LocalDateTime.of(2025, 1, 15, 20, 31)
        );
        when(meetingService.getRecording(1L, 2L)).thenReturn(response);
        when(meetingService.upsertRecording(eq(1L), eq(2L), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/studies/1/meetings/2/recording"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("READY"));

        MeetingRecordingRequest request = new MeetingRecordingRequest(
                "s3://bucket/meeting.wav",
                "wav",
                5400,
                LocalDateTime.of(2025, 1, 15, 19, 0),
                LocalDateTime.of(2025, 1, 15, 20, 30),
                123456L,
                null
        );
        mockMvc.perform(put("/api/v1/studies/1/meetings/2/recording")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.recordingUrl").value("s3://bucket/meeting.wav"));
    }

    @Test
    @DisplayName("참가자 요약 조회/저장")
    void participantSummaryEndpoints() throws Exception {
        MeetingParticipantSummaryResponse response = new MeetingParticipantSummaryResponse(
                1L,
                1L,
                "summary",
                LocalDateTime.of(2025, 1, 15, 20, 40)
        );
        when(meetingService.getParticipantSummaries(1L, 2L)).thenReturn(List.of(response));
        when(meetingService.upsertParticipantSummaries(eq(1L), eq(2L), any()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/studies/1/meetings/2/participant-summaries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].summary").value("summary"));

        mockMvc.perform(put("/api/v1/studies/1/meetings/2/participant-summaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                List.of(new MeetingParticipantSummaryRequest(1L, "summary")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].userId").value(1));
    }

    @Test
    @DisplayName("액션 아이템 조회/생성/수정")
    void actionItemEndpoints() throws Exception {
        MeetingActionItemResponse response = new MeetingActionItemResponse(
                11L,
                "todo",
                1L,
                ActionItemStatus.TODO
        );
        when(meetingService.getActionItems(1L, 2L)).thenReturn(List.of(response));
        when(meetingService.addActionItem(eq(1L), eq(2L), any())).thenReturn(response);
        when(meetingService.updateActionItem(eq(1L), eq(2L), eq(11L), any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/studies/1/meetings/2/action-items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(11));

        MeetingActionItemRequest request = new MeetingActionItemRequest("todo", 1L, ActionItemStatus.TODO);
        mockMvc.perform(post("/api/v1/studies/1/meetings/2/action-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.assigneeId").value(1));

        mockMvc.perform(put("/api/v1/studies/1/meetings/2/action-items/11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new MeetingActionItemRequest(null, 2L, ActionItemStatus.DONE))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("TODO"));
    }

    @Test
    @DisplayName("내보내기: Markdown/PDF")
    void exportEndpoints() throws Exception {
        String markdown = "# Meeting Summary";
        byte[] pdf = "pdf".getBytes(StandardCharsets.UTF_8);
        when(meetingService.exportMeetingMarkdown(1L, 2L)).thenReturn(markdown);
        when(meetingService.exportMeetingPdf(1L, 2L)).thenReturn(pdf);

        mockMvc.perform(get("/api/v1/studies/1/meetings/2/export")
                        .param("format", "MARKDOWN"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/markdown"))
                .andExpect(content().bytes(markdown.getBytes(StandardCharsets.UTF_8)));

        mockMvc.perform(get("/api/v1/studies/1/meetings/2/export")
                        .param("format", "PDF"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdf));
    }

    private UsernamePasswordAuthenticationToken authUser(Long userId) {
        User user = new User();
        user.setUserId("user-" + userId);
        ReflectionTestUtils.setField(user, "id", userId);
        return new UsernamePasswordAuthenticationToken(new SsafyUserDetails(user), null, List.of());
    }
}
