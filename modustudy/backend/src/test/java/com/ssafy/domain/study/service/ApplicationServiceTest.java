package com.ssafy.domain.study.service;

import com.ssafy.common.exception.StudyException;
import com.ssafy.domain.study.dto.request.ApplicationCreateRequest;
import com.ssafy.domain.study.dto.response.ApplicationResponse;
import com.ssafy.domain.study.entity.ApplicationStatus;
import com.ssafy.domain.study.entity.Study;
import com.ssafy.domain.study.entity.StudyApplication;
import com.ssafy.domain.study.entity.StudyType;
import com.ssafy.domain.study.entity.MeetingType;
import com.ssafy.domain.study.entity.Status;
import com.ssafy.domain.study.repository.StudyApplicationRepository;
import com.ssafy.domain.study.repository.StudyRepository;
import com.ssafy.domain.user.entity.User;
import com.ssafy.domain.user.entity.Role;
import com.ssafy.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

/**
 * ApplicationService 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private StudyApplicationRepository applicationRepository;

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationService applicationService;

    // ============================================================
    // 신청 생성 테스트
    // ============================================================

    @Test
    @DisplayName("신청 생성 성공")
    void createApplication_Success() {
        // given
        Long studyId = 1L;
        Long userId = 10L;
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .message("열심히 하겠습니다!")
                .build();

        Study study = Study.builder()
                .id(studyId)
                .leaderId(2L)
                .name("알고리즘 스터디")
                .topic("알고리즘")
                .studyType(StudyType.PLANNED)
                .meetingType(MeetingType.ONLINE)
                .status(Status.RECRUITING)
                .build();

        User user = User.builder()
                .name("김싸피")
                .nickname("ssafy_kim")
                .email("kim@ssafy.com")
                .role(Role.USER)
                .build();

        StudyApplication savedApplication = StudyApplication.builder()
                .id(1L)
                .studyId(studyId)
                .userId(userId)
                .message(request.getMessage())
                .status(ApplicationStatus.PENDING)
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(applicationRepository.existsByStudyIdAndUserId(studyId, userId)).willReturn(false);
        given(applicationRepository.save(any(StudyApplication.class))).willReturn(savedApplication);

        // when
        ApplicationResponse response = applicationService.createApplication(studyId, request, userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getApplicationId()).isEqualTo(1L);
        assertThat(response.getStudyId()).isEqualTo(studyId);
        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getMessage()).isEqualTo("열심히 하겠습니다!");
        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.PENDING);

        verify(studyRepository, times(1)).findById(studyId);
        verify(userRepository, times(1)).findById(userId);
        verify(applicationRepository, times(1)).existsByStudyIdAndUserId(studyId, userId);
        verify(applicationRepository, times(1)).save(any(StudyApplication.class));
    }

    @Test
    @DisplayName("신청 생성 실패 - 존재하지 않는 스터디")
    void createApplication_StudyNotFound() {
        // given
        Long studyId = 999L;
        Long userId = 10L;
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .message("열심히 하겠습니다!")
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> applicationService.createApplication(studyId, request, userId))
                .isInstanceOf(StudyException.StudyNotFoundException.class);

        verify(studyRepository, times(1)).findById(studyId);
        verify(userRepository, never()).findById(any());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("신청 생성 실패 - 존재하지 않는 사용자")
    void createApplication_UserNotFound() {
        // given
        Long studyId = 1L;
        Long userId = 999L;
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .message("열심히 하겠습니다!")
                .build();

        Study study = Study.builder()
                .id(studyId)
                .leaderId(2L)
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> applicationService.createApplication(studyId, request, userId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 사용자");

        verify(studyRepository, times(1)).findById(studyId);
        verify(userRepository, times(1)).findById(userId);
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("신청 생성 실패 - 중복 신청")
    void createApplication_DuplicateApplication() {
        // given
        Long studyId = 1L;
        Long userId = 10L;
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .message("열심히 하겠습니다!")
                .build();

        Study study = Study.builder()
                .id(studyId)
                .leaderId(2L)
                .build();

        User user = User.builder().build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(applicationRepository.existsByStudyIdAndUserId(studyId, userId)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> applicationService.createApplication(studyId, request, userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 신청한 스터디");

        verify(applicationRepository, times(1)).existsByStudyIdAndUserId(studyId, userId);
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("신청 생성 실패 - 본인 스터디 신청")
    void createApplication_OwnStudy() {
        // given
        Long studyId = 1L;
        Long userId = 10L;
        ApplicationCreateRequest request = ApplicationCreateRequest.builder()
                .message("열심히 하겠습니다!")
                .build();

        Study study = Study.builder()
                .id(studyId)
                .leaderId(userId)  // 본인이 스터디장!
                .build();

        User user = User.builder().build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(applicationRepository.existsByStudyIdAndUserId(studyId, userId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> applicationService.createApplication(studyId, request, userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("본인이 만든 스터디");

        verify(applicationRepository, never()).save(any());
    }

    // ============================================================
    // 신청 목록 조회 테스트
    // ============================================================

    @Test
    @DisplayName("스터디별 신청 목록 조회 성공")
    void getApplicationsByStudy_Success() {
        // given
        Long studyId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Study study = Study.builder()
                .id(studyId)
                .name("알고리즘 스터디")
                .build();

        StudyApplication app1 = StudyApplication.builder()
                .id(1L)
                .studyId(studyId)
                .userId(10L)
                .status(ApplicationStatus.PENDING)
                .build();

        User user = User.builder()
                .name("김싸피")
                .nickname("ssafy_kim")
                .email("kim@ssafy.com")
                .build();

        Page<StudyApplication> applicationPage = new PageImpl<>(List.of(app1));

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));
        given(applicationRepository.findByStudyId(studyId, pageable)).willReturn(applicationPage);
        given(userRepository.findById(10L)).willReturn(Optional.of(user));

        // when
        Page<ApplicationResponse> response = applicationService.getApplicationByStudy(studyId, null, pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getStudyName()).isEqualTo("알고리즘 스터디");

        verify(studyRepository, times(1)).findById(studyId);
        verify(applicationRepository, times(1)).findByStudyId(studyId, pageable);
    }

    @Test
    @DisplayName("스터디별 신청 목록 조회 - 상태별 필터링")
    void getApplicationsByStudy_WithStatus() {
        // given
        Long studyId = 1L;
        ApplicationStatus status = ApplicationStatus.PENDING;
        Pageable pageable = PageRequest.of(0, 10);

        Study study = Study.builder()
                .id(studyId)
                .name("알고리즘 스터디")
                .build();

        Page<StudyApplication> applicationPage = new PageImpl<>(List.of());

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));
        given(applicationRepository.findByStudyIdAndStatus(studyId, status, pageable))
                .willReturn(applicationPage);

        // when
        Page<ApplicationResponse> response = applicationService.getApplicationByStudy(studyId, status, pageable);

        // then
        assertThat(response).isNotNull();
        verify(applicationRepository, times(1)).findByStudyIdAndStatus(studyId, status, pageable);
        verify(applicationRepository, never()).findByStudyId(any(), any());
    }

    @Test
    @DisplayName("사용자별 신청 내역 조회 성공")
    void getApplicationsByUser_Success() {
        // given
        Long userId = 10L;
        Pageable pageable = PageRequest.of(0, 10);

        User user = User.builder()
                .name("김싸피")
                .nickname("ssafy_kim")
                .email("kim@ssafy.com")
                .build();

        StudyApplication app1 = StudyApplication.builder()
                .id(1L)
                .studyId(1L)
                .userId(userId)
                .status(ApplicationStatus.PENDING)
                .build();

        Study study = Study.builder()
                .name("알고리즘 스터디")
                .build();

        Page<StudyApplication> applicationPage = new PageImpl<>(List.of(app1));

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(applicationRepository.findByUserId(userId, pageable)).willReturn(applicationPage);
        given(studyRepository.findById(1L)).willReturn(Optional.of(study));

        // when
        Page<ApplicationResponse> response = applicationService.getApplicationByUser(userId, null, pageable);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);

        verify(userRepository, times(1)).findById(userId);
        verify(applicationRepository, times(1)).findByUserId(userId, pageable);
    }

    // ============================================================
    // 신청 승인 테스트
    // ============================================================

    @Test
    @DisplayName("신청 승인 성공")
    void approveApplication_Success() {
        // given
        Long studyId = 1L;
        Long applicationId = 1L;
        Long leaderId = 2L;

        Study study = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .name("알고리즘 스터디")
                .build();

        StudyApplication application = StudyApplication.builder()
                .id(applicationId)
                .studyId(studyId)
                .userId(10L)
                .status(ApplicationStatus.PENDING)
                .build();

        User user = User.builder()
                .name("김싸피")
                .nickname("ssafy_kim")
                .email("kim@ssafy.com")
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));
        given(applicationRepository.findById(applicationId)).willReturn(Optional.of(application));
        given(applicationRepository.save(any(StudyApplication.class))).willReturn(application);
        given(userRepository.findById(10L)).willReturn(Optional.of(user));

        // when
        ApplicationResponse response = applicationService.approveApplication(studyId, applicationId, leaderId);

        // then
        assertThat(response).isNotNull();
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
        assertThat(application.getProcessedAt()).isNotNull();

        verify(studyRepository, times(1)).findById(studyId);
        verify(applicationRepository, times(1)).findById(applicationId);
        verify(applicationRepository, times(1)).save(application);
    }

    @Test
    @DisplayName("신청 승인 실패 - 권한 없음")
    void approveApplication_NotLeader() {
        // given
        Long studyId = 1L;
        Long applicationId = 1L;
        Long leaderId = 2L;
        Long notLeaderId = 999L;

        Study study = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

        // when & then
        assertThatThrownBy(() -> applicationService.approveApplication(studyId, applicationId, notLeaderId))
                .isInstanceOf(StudyException.NotStudyLeaderException.class);

        verify(applicationRepository, never()).findById(any());
        verify(applicationRepository, never()).save(any());
    }

    @Test
    @DisplayName("신청 승인 실패 - 이미 처리된 신청")
    void approveApplication_AlreadyProcessed() {
        // given
        Long studyId = 1L;
        Long applicationId = 1L;
        Long leaderId = 2L;

        Study study = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .build();

        StudyApplication application = StudyApplication.builder()
                .id(applicationId)
                .studyId(studyId)
                .userId(10L)
                .status(ApplicationStatus.APPROVED)  // 이미 승인됨!
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));
        given(applicationRepository.findById(applicationId)).willReturn(Optional.of(application));

        // when & then
        assertThatThrownBy(() -> applicationService.approveApplication(studyId, applicationId, leaderId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 처리된 신청");

        verify(applicationRepository, never()).save(any());
    }

    // ============================================================
    // 신청 거절 테스트
    // ============================================================

    @Test
    @DisplayName("신청 거절 성공")
    void rejectApplication_Success() {
        // given
        Long studyId = 1L;
        Long applicationId = 1L;
        Long leaderId = 2L;
        String reason = "정원 초과";

        Study study = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .name("알고리즘 스터디")
                .build();

        StudyApplication application = StudyApplication.builder()
                .id(applicationId)
                .studyId(studyId)
                .userId(10L)
                .status(ApplicationStatus.PENDING)
                .build();

        User user = User.builder()
                .name("김싸피")
                .nickname("ssafy_kim")
                .email("kim@ssafy.com")
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));
        given(applicationRepository.findById(applicationId)).willReturn(Optional.of(application));
        given(applicationRepository.save(any(StudyApplication.class))).willReturn(application);
        given(userRepository.findById(10L)).willReturn(Optional.of(user));

        // when
        ApplicationResponse response = applicationService.rejectApplication(studyId, applicationId, leaderId, reason);

        // then
        assertThat(response).isNotNull();
        assertThat(application.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
        assertThat(application.getRejectedReason()).isEqualTo(reason);
        assertThat(application.getProcessedAt()).isNotNull();

        verify(studyRepository, times(1)).findById(studyId);
        verify(applicationRepository, times(1)).findById(applicationId);
        verify(applicationRepository, times(1)).save(application);
    }

    @Test
    @DisplayName("신청 거절 실패 - 권한 없음")
    void rejectApplication_NotLeader() {
        // given
        Long studyId = 1L;
        Long applicationId = 1L;
        Long leaderId = 2L;
        Long notLeaderId = 999L;
        String reason = "정원 초과";

        Study study = Study.builder()
                .id(studyId)
                .leaderId(leaderId)
                .build();

        given(studyRepository.findById(studyId)).willReturn(Optional.of(study));

        // when & then
        assertThatThrownBy(() -> applicationService.rejectApplication(studyId, applicationId, notLeaderId, reason))
                .isInstanceOf(StudyException.NotStudyLeaderException.class);

        verify(applicationRepository, never()).findById(any());
        verify(applicationRepository, never()).save(any());
    }
}