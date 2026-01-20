package com.ssafy.domain.study.controller;

import com.ssafy.domain.study.dto.response.LeaderInfoResponse;
import com.ssafy.domain.study.dto.response.LeaderReviewResponse;
import com.ssafy.domain.study.service.LeaderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/study/{studyId}/leader")
@RequiredArgsConstructor
@Slf4j
public class LeaderController {

    private final LeaderService leaderService;

    /**
     * 스터디장 정보 조회
     */
    @GetMapping
    public ResponseEntity<LeaderInfoResponse> getLeaderInfo(
            @PathVariable Long studyId) {

        log.info("API 호출 - 스터디장 정보 조회: studyId={}", studyId);

        LeaderInfoResponse response = leaderService.getLeaderInfo(studyId);

        log.info("API 응답 - 스터디장 정보: userId={}, name={}, rating={}",
                response.getUserId(), response.getName(), response.getLeaderRating());

        return ResponseEntity.ok(response);
    }

    /**
     * 스터디장 리뷰 목록 조회
     */
    @GetMapping("/reviews")
    public ResponseEntity<Page<LeaderReviewResponse>> getLeaderReviews(
            @PathVariable Long studyId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("API 호출 - 스터디장 리뷰 목록 조회: studyId={}, page={}, size={}",
                studyId, pageable.getPageNumber(), pageable.getPageSize());


        Page<LeaderReviewResponse> response = leaderService.getLeaderReviews(studyId, pageable);

        log.info("API 응답 - 리뷰 목록: totalElements={}, totalPages={}",
                response.getTotalElements(), response.getTotalPages());

        return ResponseEntity.ok(response);
    }
}
