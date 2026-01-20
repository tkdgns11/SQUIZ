package com.ssafy.domain.study.controller;

import com.ssafy.domain.study.dto.request.ApplicationCreateRequest;
import com.ssafy.domain.study.dto.request.ApplicationProcessRequest;
import com.ssafy.domain.study.dto.response.ApplicationResponse;
import com.ssafy.domain.study.entity.ApplicationStatus;
import com.ssafy.domain.study.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final ApplicationService applicationService;

    // ============================================================
    // 신청 생성
    // ============================================================

    /**
     * 스터디 신청 생성
     */
    @PostMapping("/study/{studyId}/applications")
    public ResponseEntity<ApplicationResponse> createApplication(
            @PathVariable Long studyId,
            @Valid @RequestBody ApplicationCreateRequest request,
            @RequestHeader("user-id") Long userId) {

        log.info("API 호출 - 스터디 신청 생성: studyId={}, userId={}", studyId, userId);

        ApplicationResponse response = applicationService.createApplication(studyId, request, userId);

        log.info("API 응답 - 신청 생성 완료: applicationId={}", response.getApplicationId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ============================================================
    // 신청 목록 조회
    // ============================================================

    /**
     * 스터디별 신청 목록 조회
     */
    @GetMapping("/study/{studyId}/applications")
    public ResponseEntity<Page<ApplicationResponse>> getApplicationsByStudy(
            @PathVariable Long studyId,
            @RequestParam(required = false) ApplicationStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("API 호출 - 스터디별 신청 목록 조회: studyId={}, status={}, page={}, size={}",
                studyId, status, pageable.getPageNumber(), pageable.getPageSize());

        Page<ApplicationResponse> response = applicationService.getApplicationByStudy(studyId, status, pageable);

        log.info("API 응답 - 신청 목록: totalElements={}, totalPages={}",
                response.getTotalElements(), response.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * 사용자별 신청 내역 조회
     */
    @GetMapping("/user/{userId}/applications")
    public ResponseEntity<Page<ApplicationResponse>> getApplicationsByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) ApplicationStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("API 호출 - 사용자별 신청 내역 조회: userId={}, status={}, page={}, size={}",
                userId, status, pageable.getPageNumber(), pageable.getPageSize());

        Page<ApplicationResponse> response = applicationService.getApplicationByUser(userId, status, pageable);

        log.info("API 응답 - 신청 내역: totalElements={}, totalPages={}",
                response.getTotalElements(), response.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * 신청 상세 조회
     */
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<ApplicationResponse> getApplication(
            @PathVariable Long applicationId) {

        log.info("API 호출 - 신청 상세 조회: applicationId={}", applicationId);

        ApplicationResponse response = applicationService.getApplication(applicationId);

        log.info("API 응답 - 신청 상세: applicationId={}, status={}",
                response.getApplicationId(), response.getStatus());

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 신청 승인/거절
    // ============================================================

    /**
     * 신청 승인
     */
    @PatchMapping("/study/{studyId}/applications/{applicationId}/approve")
    public ResponseEntity<ApplicationResponse> approveApplication(
            @PathVariable Long studyId,
            @PathVariable Long applicationId,
            @RequestHeader("user-id") Long leaderId) {

        log.info("API 호출 - 신청 승인: studyId={}, applicationId={}, leaderId={}",
                studyId, applicationId, leaderId);

        ApplicationResponse response = applicationService.approveApplication(studyId, applicationId, leaderId);

        log.info("API 응답 - 승인 완료: applicationId={}, status={}",
                response.getApplicationId(), response.getStatus());

        return ResponseEntity.ok(response);
    }

    /**
     * 신청 거절
     */
    @PatchMapping("/study/{studyId}/applications/{applicationId}/reject")
    public ResponseEntity<ApplicationResponse> rejectApplication(
            @PathVariable Long studyId,
            @PathVariable Long applicationId,
            @RequestBody ApplicationProcessRequest request,
            @RequestHeader("user-id") Long leaderId) {

        log.info("API 호출 - 신청 거절: studyId={}, applicationId={}, leaderId={}, reason={}",
                studyId, applicationId, leaderId, request.getRejectedReason());

        ApplicationResponse response = applicationService.rejectApplication(
                studyId, applicationId, leaderId, request.getRejectedReason());

        log.info("API 응답 - 거절 완료: applicationId={}, status={}",
                response.getApplicationId(), response.getStatus());

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // 내 신청 내역 조회 (편의 기능)
    // ============================================================

    /**
     * 내 신청 내역 조회
     */
    @GetMapping("/my/applications")
    public ResponseEntity<Page<ApplicationResponse>> getMyApplications(
            @RequestHeader("user-id") Long userId,
            @RequestParam(required = false) ApplicationStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("API 호출 - 내 신청 내역 조회: userId={}, status={}", userId, status);

        Page<ApplicationResponse> response = applicationService.getApplicationByUser(userId, status, pageable);

        log.info("API 응답 - 내 신청 내역: totalElements={}", response.getTotalElements());

        return ResponseEntity.ok(response);
    }
}