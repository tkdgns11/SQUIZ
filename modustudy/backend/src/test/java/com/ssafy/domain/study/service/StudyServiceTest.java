package com.ssafy.domain.study.service;

import com.ssafy.common.exception.StudyException;
import com.ssafy.domain.study.dto.request.StudyCreateRequest;
import com.ssafy.domain.study.dto.request.StudyUpdateRequest;
import com.ssafy.domain.study.dto.response.StudyResponse;
import com.ssafy.domain.study.entity.*;
import com.ssafy.domain.study.repository.StudyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class StudyServiceTest {

    @Mock
    private StudyRepository studyRepository;

    @InjectMocks
    private StudyService studyService;

    // ============================================================
    // 스터디 생성 테스트
    // ============================================================

    @Test
    @DisplayName("정상적인 입력으로 스터디 생성 성공")
    void createStudy_WithValidInput_ShouldReturnStudyResponse() {
        // given
        Long leaderId = 1L;
        StudyCreateRequest request = StudyCreateRequest.builder()
                .name("알고리즘 스터디")
                .description("백준 문제 풀이")
                .topic("알고리즘")
                .studyType(StudyType.PLANNED)
                .meetingType(MeetingType.ONLINE)
                .startDate(LocalDate.of(2025, 2, 1))
                .endDate(LocalDate.of(2025, 5, 1))
                .recruitStartDate(LocalDate.of(2025, 1, 15))
                .recruitEndDate(LocalDate.of(2025, 1, 31))
                .build();

        Study savedStudy = Study.builder()
                .id(1L)
                .leaderId(leaderId)
                .name("알고리즘 스터디")
                .status(Status.DRAFT)
                .build();

        given(studyRepository.save(any(Study.class))).willReturn(savedStudy);

        // when
        StudyResponse response = studyService.createStudy(request, leaderId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("알고리즘 스터디");
        assertThat(response.getStatus()).isEqualTo(Status.DRAFT);
        verify(studyRepository, times(1)).save(any(Study.class));
    }

    @Test
    @DisplayName("오프라인 스터디 생성 시 지역 정보 없으면 예외 발생")
    void createStudy_OfflineWithoutRegion_ShouldThrowException() {
        // given
        Long leaderId = 1L;
        StudyCreateRequest request = StudyCreateRequest.builder()
                .name("오프라인 스터디")
                .topic("알고리즘")
                .studyType(StudyType.PLANNED)
                .meetingType(MeetingType.OFFLINE)
                .regionId(null)  // 지역 정보 없음
                .startDate(LocalDate.of(2025, 2, 1))
                .endDate(LocalDate.of(2025, 5, 1))
                .build();

        // when & then
        assertThatThrownBy(() -> studyService.createStudy(request, leaderId))
                .isInstanceOf(StudyException.InvalidStudyRequestException.class)
                .hasMessage("오프라인/혼합 스터디는 지역 정보가 필수입니다");
    }

    @Test
    @DisplayName("종료일이 시작일보다 앞서면 예외 발생")
    void createStudy_InvalidDateRange_ShouldThrowException() {
        // given
        Long leaderId = 1L;
        StudyCreateRequest request = StudyCreateRequest.builder()
                .name("테스트 스터디")
                .topic("알고리즘")
                .studyType(StudyType.PLANNED)
                .meetingType(MeetingType.ONLINE)
                .startDate(LocalDate.of(2025, 5, 1))
                .endDate(LocalDate.of(2025, 2, 1))  // 시작일보다 앞섬
                .build();

        // when & then
        assertThatThrownBy(() -> studyService.createStudy(request, leaderId))
                .isInstanceOf(StudyException.InvalidStudyRequestException.class)
                .hasMessage("종료일은 시작일보다 늦어야 합니다");
    }

    // ============================================================
    // 스터디 수정 테스트
    // ============================================================

    @Test
    @DisplayName("스터디장이 스터디 수정 성공")
    void updateStudy_WithLeader_ShouldReturnUpdatedStudy() {
        // given
        Long studyId = 1L;
        Long leaderId = 1L;

        Study existingStudy = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .name("기존 스터디")
                .maxMembers(6)
                .build();

        StudyUpdateRequest request = StudyUpdateRequest.builder()
                .name("수정된 스터디")
                .maxMembers(10)
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(existingStudy));

        // when
        StudyResponse response = studyService.updateStudy(studyId, request, leaderId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("수정된 스터디");
        assertThat(response.getMaxMembers()).isEqualTo(10);
    }

    @Test
    @DisplayName("스터디장이 아닌 사용자가 수정 시도 시 예외 발생")
    void updateStudy_WithoutLeaderPermission_ShouldThrowException() {
        // given
        Long studyId = 1L;
        Long leaderId = 1L;
        Long otherUserId = 999L;

        Study existingStudy = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .name("기존 스터디")
                .build();

        StudyUpdateRequest request = StudyUpdateRequest.builder()
                .name("수정된 스터디")
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(existingStudy));

        // when & then
        assertThatThrownBy(() -> studyService.updateStudy(studyId, request, otherUserId))
                .isInstanceOf(StudyException.NotStudyLeaderException.class)
                .hasMessage("스터디를 수정할 권한이 없습니다");
    }

    @Test
    @DisplayName("존재하지 않는 스터디 수정 시 예외 발생")
    void updateStudy_NonExistentStudy_ShouldThrowException() {
        // given
        Long studyId = 999L;
        Long leaderId = 1L;
        StudyUpdateRequest request = StudyUpdateRequest.builder()
                .name("수정된 스터디")
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyService.updateStudy(studyId, request, leaderId))
                .isInstanceOf(StudyException.StudyNotFoundException.class);
    }

    // ============================================================
    // 스터디 삭제 테스트
    // ============================================================

    @Test
    @DisplayName("스터디장이 DRAFT 상태 스터디 삭제 성공")
    void deleteStudy_DraftStatus_ShouldDeleteSuccessfully() {
        // given
        Long studyId = 1L;
        Long leaderId = 1L;

        Study study = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .status(Status.DRAFT)
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

        // when
        studyService.deleteStudy(studyId, leaderId);

        // then
        verify(studyRepository, times(1)).delete(study);
    }

    @Test
    @DisplayName("진행 중인 스터디 삭제 시 예외 발생")
    void deleteStudy_InProgressStatus_ShouldThrowException() {
        // given
        Long studyId = 1L;
        Long leaderId = 1L;

        Study study = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .status(Status.IN_PROGRESS)
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

        // when & then
        assertThatThrownBy(() -> studyService.deleteStudy(studyId, leaderId))
                .isInstanceOf(StudyException.CannotDeleteStudyException.class)
                .hasMessage("진행 중이거나 완료된 스터디는 삭제할 수 없습니다");
    }

    @Test
    @DisplayName("완료된 스터디 삭제 시 예외 발생")
    void deleteStudy_CompletedStatus_ShouldThrowException() {
        // given
        Long studyId = 1L;
        Long leaderId = 1L;

        Study study = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .status(Status.COMPLETED)
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

        // when & then
        assertThatThrownBy(() -> studyService.deleteStudy(studyId, leaderId))
                .isInstanceOf(StudyException.CannotDeleteStudyException.class)
                .hasMessage("진행 중이거나 완료된 스터디는 삭제할 수 없습니다");
    }

    // ============================================================
    // 스터디 상태 변경 테스트
    // ============================================================

    @Test
    @DisplayName("DRAFT에서 RECRUITING으로 상태 변경 성공")
    void updateStudyStatus_DraftToRecruiting_ShouldChangeStatus() {
        // given
        Long studyId = 1L;
        Long leaderId = 1L;

        Study study = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .status(Status.DRAFT)
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

        // when
        StudyResponse response = studyService.updateStudyStatus(studyId, Status.RECRUITING, leaderId);

        // then
        assertThat(response.getStatus()).isEqualTo(Status.RECRUITING);
    }

    @Test
    @DisplayName("완료된 스터디 상태 변경 시 예외 발생")
    void updateStudyStatus_CompletedStudy_ShouldThrowException() {
        // given
        Long studyId = 1L;
        Long leaderId = 1L;

        Study study = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .status(Status.COMPLETED)
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

        // when & then
        assertThatThrownBy(() -> studyService.updateStudyStatus(studyId, Status.RECRUITING, leaderId))
                .isInstanceOf(StudyException.InvalidStatusTransitionException.class)
                .hasMessage("완료된 스터디는 상태를 변경할 수 없습니다");
    }

    // ============================================================
    // 모집 기간 연장 테스트
    // ============================================================

    @Test
    @DisplayName("모집 중인 스터디 기간 연장 성공")
    void extendRecruitment_RecruitingStudy_ShouldExtendSuccessfully() {
        // given
        Long studyId = 1L;
        Long leaderId = 1L;
        LocalDate newEndDate = LocalDate.of(2025, 2, 15);

        Study study = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .status(Status.RECRUITING)
                .extensionCount(0)
                .recruitEndDate(LocalDate.of(2025, 1, 31))
                .recruitStartDate(LocalDate.of(2025, 1, 15))
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

        // when
        StudyResponse response = studyService.extendRecruitment(studyId, newEndDate, leaderId);

        // then
        assertThat(response.getRecruitEndDate()).isEqualTo(newEndDate);
        assertThat(response.getExtensionCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("2회 연장 시도 시 예외 발생")
    void extendRecruitment_SecondAttempt_ShouldThrowException() {
        // given
        Long studyId = 1L;
        Long leaderId = 1L;
        LocalDate newEndDate = LocalDate.of(2025, 2, 20);

        Study study = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .status(Status.RECRUITING)
                .extensionCount(1)  // 이미 1회 연장함
                .recruitEndDate(LocalDate.of(2025, 2, 15))
                .recruitStartDate(LocalDate.of(2025, 1, 15))
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

        // when & then
        assertThatThrownBy(() -> studyService.extendRecruitment(studyId, newEndDate, leaderId))
                .isInstanceOf(StudyException.MaxExtensionReachedException.class)
                .hasMessage("모집 기간은 최대 1회만 연장 가능합니다");
    }

    @Test
    @DisplayName("모집 중이 아닌 스터디 연장 시도 시 예외 발생")
    void extendRecruitment_NotRecruitingStudy_ShouldThrowException() {
        // given
        Long studyId = 1L;
        Long leaderId = 1L;
        LocalDate newEndDate = LocalDate.of(2025, 2, 15);

        Study study = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .status(Status.IN_PROGRESS)  // 진행중
                .extensionCount(0)
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

        // when & then
        assertThatThrownBy(() -> studyService.extendRecruitment(studyId, newEndDate, leaderId))
                .isInstanceOf(StudyException.NotRecruitingException.class)
                .hasMessage("모집 중인 스터디만 기간을 연장할 수 있습니다");
    }

    // ============================================================
    // 스터디 조회 테스트
    // ============================================================

    @Test
    @DisplayName("ID로 스터디 조회 성공")
    void getStudyById_ExistingStudy_ShouldReturnStudy() {
        // given
        Long studyId = 1L;
        Study study = Study.builder()
                .id(studyId)
                .name("알고리즘 스터디")
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

        // when
        Study found = studyService.getStudyById(studyId);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(studyId);
        assertThat(found.getName()).isEqualTo("알고리즘 스터디");
    }

    @Test
    @DisplayName("존재하지 않는 스터디 조회 시 예외 발생")
    void getStudyById_NonExistentStudy_ShouldThrowException() {
        // given
        Long studyId = 999L;
        given(studyRepository.findById(studyId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyService.getStudyById(studyId))
                .isInstanceOf(StudyException.StudyNotFoundException.class);
    }

    @Test
    @DisplayName("스터디 존재 여부 확인 - 존재함")
    void existsStudy_ExistingStudy_ShouldReturnTrue() {
        // given
        Long studyId = 1L;
        given(studyRepository.existsById(studyId)).willReturn(true);

        // when
        boolean exists = studyService.existsStudy(studyId);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("스터디 존재 여부 확인 - 존재하지 않음")
    void existsStudy_NonExistentStudy_ShouldReturnFalse() {
        // given
        Long studyId = 999L;
        given(studyRepository.existsById(studyId)).willReturn(false);

        // when
        boolean exists = studyService.existsStudy(studyId);

        // then
        assertThat(exists).isFalse();
    }
}