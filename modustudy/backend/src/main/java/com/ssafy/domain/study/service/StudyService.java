package com.ssafy.domain.study.service;

import com.ssafy.common.exception.StudyException;
import com.ssafy.domain.study.dto.request.StudyCreateRequest;
import com.ssafy.domain.study.dto.request.StudyUpdateRequest;
import com.ssafy.domain.study.dto.response.StudyResponse;
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

    // ============================================================
    // 스터디 조회 API
    // ============================================================

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
                    return new StudyException.StudyNotFoundException(studyId);
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

    // ============================================================
    // 스터디 CRUD API
    // ============================================================

    /**
     * 스터디 생성
     */
    @Transactional
    public StudyResponse createStudy(StudyCreateRequest request, Long leaderId) {
        log.info("스터디 생성 시작 - 스터디장: {}, 스터디명: {}", leaderId, request.getName());

        // 비즈니스 검증
        validateStudyCreate(request);

        // DTO -> Entity 변환
        Study study = request.toEntity(leaderId);

        // 저장
        Study savedStudy = studyRepository.save(study);

        log.info("스터디 생성 완료 - studyId: {}", savedStudy.getId());

        return StudyResponse.from(savedStudy);
    }

    /**
     * 스터디 수정
     */
    @Transactional
    public StudyResponse updateStudy(Long studyId, StudyUpdateRequest request, Long leaderId) {
        log.info("스터디 수정 시작 - studyId: {}, 요청자: {}", studyId, leaderId);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyException.StudyNotFoundException(studyId));

        // 권한 확인 (스터디장만 수정 가능)
        if (!study.getLeaderId().equals(leaderId)) {
            throw new StudyException.NotStudyLeaderException("스터디를 수정할 권한이 없습니다");
        }

        validateStudyUpdate(request);

        request.updateEntity(study);

        log.info("스터디 수정 완료 - studyId: {}", studyId);

        return StudyResponse.from(study);
    }

    /**
     * 스터디 삭제
     */
    @Transactional
    public void deleteStudy(Long studyId, Long leaderId) {
        log.info("스터디 삭제 시작 - studyId: {}, 요청자: {}", studyId, leaderId);

        // 스터디 조회
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyException.StudyNotFoundException(studyId));

        // 권한 확인 (스터디장만 삭제 가능)
        if (!study.getLeaderId().equals(leaderId)) {
            throw new StudyException.NotStudyLeaderException("스터디를 삭제할 권한이 없습니다");
        }

        // 진행 중이거나 완료된 스터디는 삭제 불가
        if (study.getStatus() == Status.IN_PROGRESS || study.getStatus() == Status.COMPLETED) {
            throw new StudyException.CannotDeleteStudyException();
        }

        studyRepository.delete(study);

        log.info("스터디 삭제 완료 - studyId: {}", studyId);
    }

    /**
     * 스터디 상태 변경
     */
    @Transactional
    public StudyResponse updateStudyStatus(Long studyId, Status newStatus, Long leaderId) {
        log.info("스터디 상태 변경 - studyId: {}, 새 상태: {}", studyId, newStatus);

        // 스터디 조회
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyException.StudyNotFoundException(studyId));

        // 권한 확인
        if (!study.getLeaderId().equals(leaderId)) {
            throw new StudyException.NotStudyLeaderException("스터디 상태를 변경할 권한이 없습니다");
        }

        // 상태 변환 검증
        validateStatusTransition(study.getStatus(), newStatus);

        // 상태 변경
        study.updateStatus(newStatus);

        log.info("스터디 상태 변경 완료 - studyId: {}, 이전: {} -> 새: {}",
                studyId, study.getStatus(), newStatus);

        return StudyResponse.from(study);
    }

    /**
     * 모집 기간 연장
     */
    @Transactional
    public StudyResponse extendRecruitment(Long studyId, java.time.LocalDate newEndDate, Long leaderId) {
        log.info("모집 기간 연장 - studyId: {}, 새 종료일: {}", studyId, newEndDate);

        // 스터디 조회
        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new StudyException.StudyNotFoundException(studyId));

        // 권한 확인
        if (!study.getLeaderId().equals(leaderId)) {
            throw new StudyException.NotStudyLeaderException("모집 기간을 연장할 권한이 없습니다");
        }

        // 모집 중인 상태에서만 가능
        if (study.getStatus() != Status.RECRUITING) {
            throw new StudyException.NotRecruitingException();
        }

        // 연장
        study.extendRecruitment(newEndDate);

        log.info("모집 기간 연장 완료 - studyId: {}, 연장 횟수: {}", studyId, study.getExtensionCount());

        return StudyResponse.from(study);
    }

    // ============================================================
    // Private 검증 메서드
    // ============================================================

    /**
     * 스터디 생성 검증
     */
    private void validateStudyCreate(StudyCreateRequest request) {
        // 오프라인/혼합 스터디는 지역 필수
        if (!request.isValidLocation()) {
            throw new StudyException.InvalidStudyRequestException("오프라인/혼합 스터디는 지역 정보가 필수입니다");
        }

        // 날짜 범위 검증
        if (!request.isValidDateRange()) {
            throw new StudyException.InvalidStudyRequestException("종료일은 시작일보다 늦어야 합니다");
        }

        // 모집 기간 검증
        if (!request.isValidRecruitmentPeriod()) {
            throw new StudyException.InvalidStudyRequestException("모집 종료일은 스터디 시작일보다 앞서야 합니다");
        }
    }

    /**
     * 스터디 수정 검증
     */
    private void validateStudyUpdate(StudyUpdateRequest request) {
        // 오프라인/혼합 스터디는 지역 필수
        if (!request.isValidLocation()) {
            throw new StudyException.InvalidStudyRequestException("오프라인/혼합 스터디는 지역 정보가 필수입니다");
        }

        // 날짜 범위 검증
        if (!request.isValidDateRange()) {
            throw new StudyException.InvalidStudyRequestException("종료일은 시작일보다 늦어야 합니다");
        }
    }

    /**
     * 상태 전환 검증
     */
    private void validateStatusTransition(Status currentStatus, Status newStatus) {
        // 완료된 스터디는 상태 변경 불가
        if (currentStatus == Status.COMPLETED) {
            throw new StudyException.InvalidStatusTransitionException("완료된 스터디는 상태를 변경할 수 없습니다");
        }

        // 취소된 스터디는 상태 변경 불가
        if (currentStatus == Status.CANCELLED) {
            throw new StudyException.InvalidStatusTransitionException("취소된 스터디는 상태를 변경할 수 없습니다");
        }

        // 진행 중인 스터디는 완료나 취소만 가능
        if (currentStatus == Status.IN_PROGRESS) {
            if (newStatus != Status.COMPLETED && newStatus != Status.CANCELLED) {
                throw new StudyException.InvalidStatusTransitionException("진행 중인 스터디는 완료나 취소만 가능합니다");
            }
        }
    }
}