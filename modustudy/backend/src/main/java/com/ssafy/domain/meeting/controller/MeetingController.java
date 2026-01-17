package com.ssafy.domain.meeting.controller;

import com.ssafy.common.auth.SsafyUserDetails;
import com.ssafy.common.response.ApiResponse;
import com.ssafy.common.response.PageResponse;
import com.ssafy.domain.meeting.dto.request.MeetingKeywordUpdateRequest;
import com.ssafy.domain.meeting.dto.request.MeetingMuteRequest;
import com.ssafy.domain.meeting.dto.request.MeetingRequest;
import com.ssafy.domain.meeting.dto.request.MeetingSummaryUpdateRequest;
import com.ssafy.domain.meeting.dto.request.MeetingTranscriptRequest;
import com.ssafy.domain.meeting.dto.response.*;
import com.ssafy.domain.meeting.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/studies/{studyId}/meetings")
@Tag(name = "Meeting", description = "미팅 API")
public class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    @Operation(summary = "미팅 목록", description = "스터디 미팅 기록 목록을 페이지 단위로 조회한다.")
    public ResponseEntity<PageResponse<MeetingListItemResponse>> list(
            @PathVariable Long studyId,
            Pageable pageable
    ) {
        Page<MeetingListItemResponse> page = meetingService.listMeetings(studyId, pageable);
        return ResponseEntity.ok(PageResponse.of(page));
    }

    @GetMapping("/{meetingId}")
    @Operation(summary = "미팅 상세", description = "미팅 상세 정보 및 참가자, 요약 정보를 조회한다.")
    public ResponseEntity<ApiResponse<MeetingDetailResponse>> detail(
            @PathVariable Long studyId,
            @PathVariable Long meetingId
    ) {
        return ResponseEntity.ok(ApiResponse.success(meetingService.getMeetingDetail(studyId, meetingId)));
    }

    @PostMapping
    @Operation(summary = "미팅 시작", description = "미팅을 시작하고 화상회의 룸 토큰을 반환한다.")
    public ResponseEntity<ApiResponse<MeetingResponse>> start(
            @PathVariable Long studyId,
            @Valid @RequestBody MeetingRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(meetingService.startMeeting(studyId, request)));
    }

    @PutMapping("/{meetingId}/end")
    @Operation(summary = "미팅 종료", description = "미팅을 종료하고 요약 생성 상태를 반환한다.")
    public ResponseEntity<ApiResponse<MeetingEndResponse>> end(
            @PathVariable Long studyId,
            @PathVariable Long meetingId
    ) {
        return ResponseEntity.ok(ApiResponse.success(meetingService.endMeeting(studyId, meetingId)));
    }

    @PostMapping("/{meetingId}/join")
    @Operation(summary = "미팅 참여", description = "미팅에 참여하고 WebRTC 접속 정보를 반환한다.")
    public ResponseEntity<ApiResponse<MeetingJoinResponse>> join(
            @PathVariable Long studyId,
            @PathVariable Long meetingId,
            @AuthenticationPrincipal SsafyUserDetails userDetails
    ) {
        Long userId = userDetails == null ? null : userDetails.getUser().getId();
        return ResponseEntity.ok(ApiResponse.success(meetingService.joinMeeting(studyId, meetingId, requireUserId(userId))));
    }

    @PostMapping("/{meetingId}/leave")
    @Operation(summary = "미팅 퇴장", description = "미팅에서 퇴장 처리한다.")
    public ResponseEntity<ApiResponse<Void>> leave(
            @PathVariable Long studyId,
            @PathVariable Long meetingId,
            @AuthenticationPrincipal SsafyUserDetails userDetails
    ) {
        Long userId = userDetails == null ? null : userDetails.getUser().getId();
        meetingService.leaveMeeting(studyId, meetingId, requireUserId(userId));
        return ResponseEntity.ok(ApiResponse.success());
    }

    @GetMapping("/{meetingId}/summary")
    @Operation(summary = "미팅 요약 조회", description = "요약 생성 완료된 미팅의 요약을 조회한다.")
    public ResponseEntity<ApiResponse<MeetingSummaryResponse>> summary(
            @PathVariable Long studyId,
            @PathVariable Long meetingId
    ) {
        return ResponseEntity.ok(ApiResponse.success(meetingService.getSummary(studyId, meetingId)));
    }

    @PutMapping("/{meetingId}/summary")
    @Operation(summary = "미팅 요약 업데이트", description = "요약/키워드/액션아이템/상태를 저장한다.")
    public ResponseEntity<ApiResponse<MeetingSummaryResponse>> upsertSummary(
            @PathVariable Long studyId,
            @PathVariable Long meetingId,
            @RequestBody MeetingSummaryUpdateRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(meetingService.upsertSummary(studyId, meetingId, request)));
    }

    @GetMapping("/{meetingId}/transcript")
    @Operation(summary = "미팅 전사 조회", description = "미팅 전사 로그를 페이지 단위로 조회한다.")
    public ResponseEntity<ApiResponse<MeetingTranscriptPageResponse>> transcript(
            @PathVariable Long studyId,
            @PathVariable Long meetingId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(meetingService.getTranscripts(studyId, meetingId, pageable)));
    }

    @PostMapping("/{meetingId}/transcript")
    @Operation(summary = "미팅 전사 저장", description = "전사 한 건을 저장한다.")
    public ResponseEntity<ApiResponse<MeetingTranscriptItemResponse>> addTranscript(
            @PathVariable Long studyId,
            @PathVariable Long meetingId,
            @Valid @RequestBody MeetingTranscriptRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(meetingService.addTranscript(studyId, meetingId, request)));
    }

    @GetMapping("/{meetingId}/photos")
    @Operation(summary = "미팅 사진 목록", description = "미팅 스냅샷 목록을 조회한다.")
    public ResponseEntity<ApiResponse<List<MeetingPhotoResponse>>> photos(
            @PathVariable Long studyId,
            @PathVariable Long meetingId
    ) {
        return ResponseEntity.ok(ApiResponse.success(meetingService.getPhotos(studyId, meetingId)));
    }

    @PostMapping("/{meetingId}/photos")
    @Operation(summary = "미팅 사진 등록", description = "미팅 스냅샷 이미지를 저장한다.")
    public ResponseEntity<ApiResponse<MeetingPhotoResponse>> addPhoto(
            @PathVariable Long studyId,
            @PathVariable Long meetingId,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(meetingService.addPhoto(studyId, meetingId, image)));
    }

    @PutMapping("/{meetingId}/keywords")
    @Operation(summary = "미팅 키워드 업데이트", description = "미팅 키워드 목록을 저장한다.")
    public ResponseEntity<ApiResponse<Void>> updateKeywords(
            @PathVariable Long studyId,
            @PathVariable Long meetingId,
            @Valid @RequestBody MeetingKeywordUpdateRequest request
    ) {
        meetingService.updateKeywords(studyId, meetingId, request);
        return ResponseEntity.ok(ApiResponse.success("Keywords updated"));
    }

    @PutMapping("/{meetingId}/participants/{userId}/mute")
    @Operation(summary = "참가자 음소거", description = "미팅 참가자의 음소거 상태를 변경한다.")
    public ResponseEntity<ApiResponse<Void>> muteParticipant(
            @PathVariable Long studyId,
            @PathVariable Long meetingId,
            @PathVariable Long userId,
            @Valid @RequestBody MeetingMuteRequest request
    ) {
        meetingService.updateParticipantMute(studyId, meetingId, userId, request.muted());
        return ResponseEntity.ok(ApiResponse.success("Participant updated"));
    }

    private Long requireUserId(Long userId) {
        if (userId == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED");
        }
        return userId;
    }
}
