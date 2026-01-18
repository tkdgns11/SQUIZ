package com.ssafy.domain.study.repository;

import com.ssafy.domain.study.entity.*;
import com.ssafy.global.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StudyRepository QueryDSL 동적 쿼리 테스트
 * searchStudies() 메서드 집중 테스트
 */
@DataJpaTest
@Import({StudyRepositoryImpl.class, QueryDslConfig.class})
class StudyRepositoryQueryDslTest {

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Study study1 = Study.builder()
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
                .build();

        Study study2 = Study.builder()
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
                .build();

        Study study3 = Study.builder()
                .leaderId(2L)
                .name("번개 알고리즘")
                .description("이번 주말에 모여요")
                .topic("알고리즘")
                .format("문제풀이")
                .studyType(StudyType.LIGHTNING)
                .meetingType(MeetingType.ONLINE)
                .status(Status.DRAFT)
                .isPublic(true)
                .maxMembers(10)
                .difficulty(Difficulty.ADVANCED)
                .build();

        studyRepository.save(study1);
        studyRepository.save(study2);
        studyRepository.save(study3);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("동적 검색 - 조건 없음 (전체 조회)")
    void searchWithoutCondition() {
        // given
        StudySearchCondition condition = new StudySearchCondition();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(2); // DRAFT 제외
    }

    @Test
    @DisplayName("동적 검색 - 키워드 검색 (이름)")
    void searchByKeyword() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .keyword("알고리즘")
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(1); // DRAFT 제외되어 1개
        assertThat(result.getContent().get(0).getName()).contains("알고리즘");
    }

    @Test
    @DisplayName("동적 검색 - 키워드 검색 (설명)")
    void searchByKeywordInDescription() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .keyword("백준")
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDescription()).contains("백준");
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
    @DisplayName("동적 검색 - 스터디 타입 필터")
    void searchByStudyType() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .studyType(StudyType.PLANNED)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(s -> s.getStudyType() == StudyType.PLANNED);
    }

    @Test
    @DisplayName("동적 검색 - 상태 필터")
    void searchByStatus() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .status(Status.RECRUITING)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).allMatch(s -> s.getStatus() == Status.RECRUITING);
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
    @DisplayName("동적 검색 - 지역 필터")
    void searchByRegion() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .regionId(1L)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRegionId()).isEqualTo(1L);
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
        Study found = result.getContent().get(0);
        assertThat(found.getName()).contains("알고리즘");
        assertThat(found.getMeetingType()).isEqualTo(MeetingType.ONLINE);
        assertThat(found.getStatus()).isEqualTo(Status.RECRUITING);
    }

    @Test
    @DisplayName("동적 검색 - 복합 조건 (주제 + 난이도 + 진행방식)")
    void searchByComplexConditions() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .topic("CS")
                .difficulty(Difficulty.BEGINNER)
                .meetingType(MeetingType.OFFLINE)
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        Study found = result.getContent().get(0);
        assertThat(found.getTopic()).isEqualTo("CS");
        assertThat(found.getDifficulty()).isEqualTo(Difficulty.BEGINNER);
        assertThat(found.getMeetingType()).isEqualTo(MeetingType.OFFLINE);
    }

    @Test
    @DisplayName("동적 검색 - 조건에 맞는 결과 없음")
    void searchWithNoResult() {
        // given
        StudySearchCondition condition = StudySearchCondition.builder()
                .keyword("존재하지않는스터디")
                .build();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Study> result = studyRepository.searchStudies(condition, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }
}
