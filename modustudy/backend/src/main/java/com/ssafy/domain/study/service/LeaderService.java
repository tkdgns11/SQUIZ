package com.ssafy.domain.study.service;

import com.ssafy.common.exception.StudyException;
import com.ssafy.domain.study.dto.response.LeaderInfoResponse;
import com.ssafy.domain.study.dto.response.LeaderReviewResponse;
import com.ssafy.domain.study.entity.LeaderReview;
import com.ssafy.domain.study.entity.Study;
import com.ssafy.domain.study.repository.LeaderReviewRepository;
import com.ssafy.domain.study.repository.StudyRepository;
import com.ssafy.domain.user.entity.User;
import com.ssafy.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 스터디장 정보/리뷰 Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LeaderService {

    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final LeaderReviewRepository leaderReviewRepository;

    /**
     * 스터디장 정보 조회
     *
     * @param studyId 스터디 ID
     * @return 스터디장 정보
     */
    public LeaderInfoResponse getLeaderInfo(Long studyId) {
        log.info("스터디장 정보 조회 시작 - studyId: {}", studyId);

        // 1. 스터디 조회
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 스터디 - studyId: {}", studyId);
                    return new StudyException.StudyNotFoundException(studyId);
                });

        // 2. 스터디장 조회
        User leader = userRepository.findById(study.getLeaderId())
                .orElseThrow(() -> {
                    log.error("존재하지 않는 사용자 - userId: {}", study.getLeaderId());
                    return new IllegalArgumentException("존재하지 않는 사용자입니다: " + study.getLeaderId());
                });

        log.info("스터디장 정보 조회 완료 - leaderId: {}, name: {}, rating: {}, reviewCount: {}",
                leader.getId(), leader.getName(), leader.getLeaderRating(), leader.getLeaderReviewCount());

        // 3. DTO 변환 및 반환
        return LeaderInfoResponse.from(leader);
    }

    /**
     * 스터디장 리뷰 목록 조회
     *
     * @param studyId 스터디 ID
     * @param pageable 페이징 정보
     * @return 리뷰 목록 (페이징)
     */
    public Page<LeaderReviewResponse> getLeaderReviews(Long studyId, Pageable pageable) {
        log.info("스터디장 리뷰 목록 조회 시작 - studyId: {}, page: {}, size: {}",
                studyId, pageable.getPageNumber(), pageable.getPageSize());

        // 1. 스터디 조회
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 스터디 - studyId: {}", studyId);
                    return new StudyException.StudyNotFoundException(studyId);
                });

        // 2. 스터디장의 리뷰 조회
        Page<LeaderReview> reviews = leaderReviewRepository.findByLeaderId(study.getLeaderId(), pageable);

        log.info("스터디장 리뷰 조회 완료 - leaderId: {}, totalElements: {}, totalPages: {}",
                study.getLeaderId(), reviews.getTotalElements(), reviews.getTotalPages());

        // 3. DTO 변환 및 추가 정보 설정
        return reviews.map(review -> {
            LeaderReviewResponse response = LeaderReviewResponse.from(review);

            // 평가자 정보 설정
            userRepository.findById(review.getReviewerId()).ifPresent(reviewer -> {
                response.setReviewerInfo(reviewer.getName(), reviewer.getNickname());
                log.debug("평가자 정보 설정 - reviewerId: {}, name: {}", reviewer.getId(), reviewer.getName());
            });

            // 스터디 이름 설정
            studyRepository.findById(review.getStudyId()).ifPresent(reviewStudy -> {
                response.setStudyName(reviewStudy.getName());
                log.debug("스터디 이름 설정 - studyId: {}, name: {}", reviewStudy.getId(), reviewStudy.getName());
            });

            return response;
        });
    }

    /**
     * 특정 스터디장의 평균 평점 조회 (Optional)
     *
     * @param leaderId 스터디장 ID
     * @return 평균 평점
     */
    public Double getLeaderAverageRating(Long leaderId) {
        log.info("스터디장 평균 평점 조회 - leaderId: {}", leaderId);

        Double avgRating = leaderReviewRepository.calculateAverageRating(leaderId);

        log.info("평균 평점 조회 완료 - leaderId: {}, avgRating: {}", leaderId, avgRating);

        return avgRating != null ? avgRating : 0.0;
    }

    /**
     * 특정 스터디장의 리뷰 개수 조회 (Optional)
     *
     * @param leaderId 스터디장 ID
     * @return 리뷰 개수
     */
    public Long getLeaderReviewCount(Long leaderId) {
        log.info("스터디장 리뷰 개수 조회 - leaderId: {}", leaderId);

        Long count = leaderReviewRepository.countByLeaderId(leaderId);

        log.info("리뷰 개수 조회 완료 - leaderId: {}, count: {}", leaderId, count);

        return count;
    }
}