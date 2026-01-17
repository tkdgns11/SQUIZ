package com.ssafy.domain.study.service;

import com.ssafy.domain.study.entity.Status;
import com.ssafy.domain.study.entity.Study;
import com.ssafy.domain.study.repository.StudyRepository;
import com.ssafy.domain.study.repository.StudySearchCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudyService {

    private final StudyRepository studyRepository;

    /**
     * 전체 스터디 목록 조회 (공개된 스터디만, DRAFT 제외)
     */
    public Page<Study> getAllStudies(Pageable pageable) {
        log.info("전체 스터디 목록 조회 - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return studyRepository.findAllPublicStudies(Status.DRAFT, pageable);
    }

    /**
     * 모집중인 스터디 목록 조회
     */
    public Page<Study> getRecruitingStudies(Pageable pageable) {
        log.info("모집중인 스터디 목록 조회 - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return studyRepository.findRecruitingStudies(pageable);
    }

    /**
     * 스터디 검색/필터링 (동적 쿼리)
     */
    public Page<Study> searchStudies(StudySearchCondition condition, Pageable pageable) {
        log.info("스터디 검색 - 조건: keyword={}, topic={}, status={}, meetingType={}",
                condition.getKeyword(),
                condition.getTopic(),
                condition.getStatus(),
                condition.getMeetingType());

        return studyRepository.searchStudies(condition, pageable);
    }

    /**
     * 스터디장별 스터디 목록 조회
     */
    public Page<Study> getStudiesByLeader(Long leaderId, Pageable pageable) {
        log.info("스터디장 {} 의 스터디 목록 조회", leaderId);

        return studyRepository.findByLeaderId(leaderId, pageable);
    }

    /**
     * 스터디장의 특정 상태 스터디 목록 조회
     */
    public Page<Study> getStudiesByLeaderAndStatus(Long leaderId, Status status, Pageable pageable) {
        log.info("스터디장 {} 의 {} 상태 스터디 목록 조회", leaderId, status);

        return studyRepository.findByLeaderIdAndStatus(leaderId, status, pageable);
    }

    /**
     * 스터디 상세 조회
     */
    public Study getStudyById(Long studyId) {
        log.info("스터디 상세 조회 - ID: {}", studyId);

        return studyRepository.findById(studyId)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 스터디 ID: {}", studyId);
                    return new IllegalArgumentException("존재하지 않는 스터디입니다. ID: " + studyId);
                });
    }

    /**
     * 특정 상태의 스터디 개수 조회
     */
    public Long countStudiesByStatus(Status status) {
        log.info("스터디 개수 조회 - 상태: {}", status);

        return studyRepository.countByStatus(status);
    }

    /**
     * 스터디 존재 여부 확인
     */
    public boolean existsStudy(Long studyId) {
        boolean exists = studyRepository.existsById(studyId);
        log.info("스터디 존재 확인 - ID: {}, 존재 여부: {}", studyId, exists);

        return exists;
    }
}