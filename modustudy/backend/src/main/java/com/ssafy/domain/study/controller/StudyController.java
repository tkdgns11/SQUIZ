package com.ssafy.domain.study.controller;

import com.ssafy.domain.study.entity.Status;
import com.ssafy.domain.study.entity.Study;
import com.ssafy.domain.study.repository.StudySearchCondition;
import com.ssafy.domain.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/study")
@RequiredArgsConstructor
@Slf4j
public class StudyController {
    private final StudyService studyService;

    /**
     * 전체 스터디 목록 조회
     * GET /api/v1/study?page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<Study>> getAllStudies(
            @PageableDefault(size = 20) Pageable pageable) {

        log.info("API 호출 - 전체 스터디 목록 조회: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Study> studies = studyService.getAllStudies(pageable);

        return ResponseEntity.ok(studies);
    }

    /**
     * 모집중인 스터디 목록 조회
     * GET /api/v1/study/recruiting?page=0&size=20
     */
    @GetMapping("/recruiting")
    public ResponseEntity<Page<Study>> getRecruitingStudies(
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("API 호출 - 모집중인 스터디 목록 조회");

        Page<Study> studies = studyService.getRecruitingStudies(pageable);

        return ResponseEntity.ok(studies);
    }
    /**
     * 스터디 검색/필터링
     * GET /api/v1/study/search?keyword=알고리즘&meetingType=ONLINE&page=0&size=20
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Study>> searchStudies(
            @ModelAttribute StudySearchCondition condition,
            @PageableDefault(size = 20) Pageable pageable) {

        log.info("API 호출 - 스터디 검색 : keyword={}, topic={}",
                condition.getKeyword(), condition.getTopic());

        Page<Study> studies = studyService.searchStudies(condition, pageable);

        return ResponseEntity.ok(studies);
    }

    /**
     * 스터디장별 스터디 목록 조회
     * GET /api/v1/study/leader/{leaderId}?page=0&size=20
     */
    @GetMapping("/leader/{leaderId}")
    public ResponseEntity<Page<Study>> getStudiesByLeader(
            @PathVariable Long leaderId,
            @PageableDefault(size = 20) Pageable pageable) {

        log.info("API 호출 - 스터디장별 조회: leaderId={}", leaderId);

        Page<Study> studies = studyService.getStudiesByLeader(leaderId, pageable);

        return ResponseEntity.ok(studies);
    }

    /**
     * 스터디장의 특정 상태 스터디 목록 조회
     * GET /api/v1/study/leader/{leaderId}/status/{status}?page=0&size=20
     */
    @GetMapping("/leader/{leaderId}/status/{status}")
    public ResponseEntity<Page<Study>> getStudiesByLeaderAndStatus(
            @PathVariable Long leaderId,
            @PathVariable Status status,
            @PageableDefault(size = 20) Pageable pageable) {

        log.info("API 호출 - 스터디장 + 상태 조회: leaderId={}, status={}", leaderId, status);

        Page<Study> studies = studyService.getStudiesByLeaderAndStatus(leaderId, status, pageable);

        return ResponseEntity.ok(studies);
    }

    /**
     * 스터디 상세 조회
     * GET /api/v1/study/{studyId}
     */
    @GetMapping("/{studyId}")
    public ResponseEntity<Study> getStudyDetail(
            @PathVariable Long studyId) {

        log.info("API 호출 - 스터디 상세 조회: studyId={}", studyId);

        Study study = studyService.getStudyById(studyId);

        return ResponseEntity.ok(study);
    }

    /**
     * 특정 상태의 스터디 개수 조회
     * GET /api/v1/study/count?status=RECRUITING
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countStudies(
            @RequestParam Status status) {

        log.info("API 호출 - 스터디 개수 조회: status={}", status);

        Long count = studyService.countStudiesByStatus(status);

        return ResponseEntity.ok(count);
    }

    /**
     * 스터디 존재 여부 확인
     * GET /api/v1/study/{studyId}/exists
     */
    @GetMapping("/{studyId}/exists")
    public ResponseEntity<Boolean> existsStudy(
            @PathVariable Long studyId) {

        log.info("API 호출 - 스터디 존재 확인: studyId={}", studyId);

        boolean exists = studyService.existsStudy(studyId);

        return ResponseEntity.ok(exists);
    }
}
