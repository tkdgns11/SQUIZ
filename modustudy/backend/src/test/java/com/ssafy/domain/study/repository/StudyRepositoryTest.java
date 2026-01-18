package com.ssafy.domain.study.repository;

import com.ssafy.domain.study.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StudyRepository 테스트
 *
 * @DataJpaTest: JPA 관련 컴포넌트만 로드 (가볍고 빠름)
 */
@DataJpaTest
@EntityScan(basePackages = "com.ssafy.domain.study.entity")
@Import({StudyRepositoryImpl.class, com.ssafy.global.config.QueryDslConfig.class})
class StudyRepositoryTest {

    @Autowired
    private StudyRepository studyRepository;

    private Study study1;
    private Study study2;
    private Study study3;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        study1 = Study.builder()
                .leaderId(1L)
                .name("알고리즘 스터디")
                .description("백준 문제 풀이")
                .topic("알고리즘")
                .format("문제풀이")
                .studyType(StudyType.PLANNED)
                .meetingType(MeetingType.ONLINE)
                .status(Status.RECRUITING)
                .isPublic(true)
                .maxMembers(6)
                .difficulty(Difficulty.INTERMEDIATE)
                .scheduleDays("MON,WED,FRI")
                .scheduleTime(LocalTime.of(19, 0))
                .recruitStartDate(LocalDate.now())
                .recruitEndDate(LocalDate.now().plusDays(14))
                .startDate(LocalDate.now().plusDays(15))
                .endDate(LocalDate.now().plusMonths(3))
                .build();

        study2 = Study.builder()
                .leaderId(1L)
                .name("CS 기초 스터디")
                .description("운영체제, 네트워크 학습")
                .topic("CS")
                .format("독서")
                .studyType(StudyType.PLANNED)
                .meetingType(MeetingType.OFFLINE)
                .regionId(1L)
                .status(Status.RECRUITING)
                .isPublic(true)
                .maxMembers(4)
                .difficulty(Difficulty.BEGINNER)
                .scheduleDays("TUE,THU")
                .scheduleTime(LocalTime.of(20, 0))
                .recruitStartDate(LocalDate.now())
                .recruitEndDate(LocalDate.now().plusDays(7))
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusMonths(2))
                .build();

        study3 = Study.builder()
                .leaderId(2L)
                .name("번개 알고리즘")
                .description("이번 주말에 모여요")
                .topic("알고리즘")
                .format("문제풀이")
                .studyType(StudyType.LIGHTNING)
                .meetingType(MeetingType.ONLINE)
                .status(Status.DRAFT) // DRAFT 상태
                .isPublic(true)
                .maxMembers(10)
                .difficulty(Difficulty.ADVANCED)
                .build();

        studyRepository.save(study1);
        studyRepository.save(study2);
        studyRepository.save(study3);
    }

    @Test
    @DisplayName("공개 스터디 조회 - DRAFT 제외")
    void findAllPublicStudies() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.findAllPublicStudies(Status.DRAFT, pageable);

        // then
        assertThat(result.getContent()).hasSize(2); // DRAFT 제외
        assertThat(result.getContent()).extracting("status")
                .doesNotContain(Status.DRAFT);
    }

    @Test
    @DisplayName("모집중인 스터디만 조회")
    void findRecruitingStudies() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.findRecruitingStudies(pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(s -> s.getStatus() == Status.RECRUITING);
    }

    @Test
    @DisplayName("스터디장별 조회")
    void findByLeaderId() {
        // given
        Long leaderId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.findByLeaderId(leaderId, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(s -> s.getLeaderId().equals(leaderId));
    }

    @Test
    @DisplayName("상태별 스터디 개수 조회")
    void countByStatus() {
        // when
        Long recruitingCount = studyRepository.countByStatus(Status.RECRUITING);
        Long draftCount = studyRepository.countByStatus(Status.DRAFT);

        // then
        assertThat(recruitingCount).isEqualTo(2);
        assertThat(draftCount).isEqualTo(1);
    }

    @Test
    @DisplayName("동적 검색 - 키워드만")
    void searchByKeyword() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .keyword("알고리즘")
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(1); // DRAFT 제외되어 1개만
        assertThat(result.getContent().get(0).getName()).contains("알고리즘");
    }

    @Test
    @DisplayName("동적 검색 - 주제 필터")
    void searchByTopic() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .topic("CS")
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTopic()).isEqualTo("CS");
    }

    @Test
    @DisplayName("동적 검색 - 진행 방식 필터")
    void searchByMeetingType() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .meetingType(MeetingType.ONLINE)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(1); // DRAFT 제외
        assertThat(result.getContent()).allMatch(s -> s.getMeetingType() == MeetingType.ONLINE);
    }

    @Test
    @DisplayName("동적 검색 - 난이도 필터")
    void searchByDifficulty() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .difficulty(Difficulty.INTERMEDIATE)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDifficulty()).isEqualTo(Difficulty.INTERMEDIATE);
    }

    @Test
    @DisplayName("동적 검색 - 복합 조건 (키워드 + 진행방식 + 상태)")
    void searchByMultipleConditions() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .keyword("알고리즘")
                .meetingType(MeetingType.ONLINE)
                .status(Status.RECRUITING)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        Study foundStudy = result.getContent().get(0);
        assertThat(foundStudy.getName()).contains("알고리즘");
        assertThat(foundStudy.getMeetingType()).isEqualTo(MeetingType.ONLINE);
        assertThat(foundStudy.getStatus()).isEqualTo(Status.RECRUITING);
    }

    @Test
    @DisplayName("동적 검색 - 조건 없음 (전체 조회)")
    void searchWithoutCondition() {
        // given
        StudySearchCondition condition = new StudySearchCondition(); // 조건 없음
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(2); // DRAFT 제외
    }

    @Test
    @DisplayName("동적 검색 - 요일 필터")
    void searchByScheduleDays() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .scheduleDays("MON")
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getScheduleDays()).contains("MON");
    }

    @Test
    @DisplayName("동적 검색 - 최대 인원 이하 필터")
    void searchByMaxMembers() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .maxMembersLessThan(5)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getMaxMembers()).isLessThanOrEqualTo(5);
    }
}