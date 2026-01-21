package com.ssafy.domain.quiz.service;

import com.ssafy.domain.gamification.entity.Badge;
import com.ssafy.domain.gamification.entity.BadgeCategory;
import com.ssafy.domain.gamification.repository.BadgeRepository;
import com.ssafy.domain.quiz.dto.response.BadgeInfo;
import com.ssafy.domain.quiz.dto.response.QuizCourseDetailResponse;
import com.ssafy.domain.quiz.dto.response.QuizCourseListItem;
import com.ssafy.domain.quiz.dto.response.QuizCourseListResponse;
import com.ssafy.domain.quiz.dto.response.SectionSummary;
import com.ssafy.domain.quiz.entity.QuizCourse;
import com.ssafy.domain.quiz.entity.QuizCourseSection;
import com.ssafy.domain.quiz.repository.QuizCourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * QuizCourseService 단위 테스트.
 *
 * 테스트 대상: {@link QuizCourseService#getCourseList()}, {@link QuizCourseService#getCourseDetail(Long)}
 */
@ExtendWith(MockitoExtension.class)
class QuizCourseServiceTest {

    @InjectMocks
    private QuizCourseService quizCourseService;

    @Mock
    private QuizCourseRepository quizCourseRepository;

    @Mock
    private BadgeRepository badgeRepository;

    @Nested
    @DisplayName("getCourseList 메서드는")
    class GetCourseList {

        @Test
        @DisplayName("활성 코스가 없으면 빈 목록을 반환한다")
        void returnEmptyListWhenNoActiveCourses() {
            // given
            given(quizCourseRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc())
                    .willReturn(Collections.emptyList());

            // when
            QuizCourseListResponse response = quizCourseService.getCourseList();

            // then
            assertThat(response).isNotNull();
            assertThat(response.courses()).isEmpty();
            verify(quizCourseRepository).findAllByIsActiveTrueOrderBySortOrderAscIdAsc();
        }

        @Test
        @DisplayName("코스가 있고 배지 코드가 있으면 배지 이름을 포함해 반환한다")
        void returnCoursesWithBadgeName() {
            // given
            QuizCourse javaCourse = createQuizCourse(1L, "JAVA", "Java 마스터",
                    "Java 기초부터 고급까지", 5, "JAVA_MASTER", 0);

            Badge javaBadge = createBadge(1L, "JAVA_MASTER", "Java 마스터");

            given(quizCourseRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc())
                    .willReturn(List.of(javaCourse));
            given(badgeRepository.findByCodeIn(List.of("JAVA_MASTER")))
                    .willReturn(List.of(javaBadge));

            // when
            QuizCourseListResponse response = quizCourseService.getCourseList();

            // then
            assertThat(response.courses()).hasSize(1);
            QuizCourseListItem item = response.courses().get(0);
            assertThat(item.id()).isEqualTo(1L);
            assertThat(item.code()).isEqualTo("JAVA");
            assertThat(item.name()).isEqualTo("Java 마스터");
            assertThat(item.description()).isEqualTo("Java 기초부터 고급까지");
            assertThat(item.totalSections()).isEqualTo(5);
            assertThat(item.badgeCode()).isEqualTo("JAVA_MASTER");
            assertThat(item.badgeName()).isEqualTo("Java 마스터");
        }

        @Test
        @DisplayName("배지 코드가 null인 코스는 배지 이름도 null로 반환한다")
        void returnNullBadgeNameWhenBadgeCodeIsNull() {
            // given
            QuizCourse courseWithNoBadge = createQuizCourse(1L, "TEST", "테스트 코스",
                    "테스트 설명", 3, null, 0);

            given(quizCourseRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc())
                    .willReturn(List.of(courseWithNoBadge));

            // when
            QuizCourseListResponse response = quizCourseService.getCourseList();

            // then
            assertThat(response.courses()).hasSize(1);
            QuizCourseListItem item = response.courses().get(0);
            assertThat(item.badgeCode()).isNull();
            assertThat(item.badgeName()).isNull();
        }

        @Test
        @DisplayName("여러 코스가 정렬 순서대로 반환된다")
        void returnCoursesInSortOrder() {
            // given
            QuizCourse course1 = createQuizCourse(1L, "JAVA", "Java 마스터",
                    "Java 설명", 5, "JAVA_MASTER", 1);
            QuizCourse course2 = createQuizCourse(2L, "PYTHON", "Python 기초",
                    "Python 설명", 4, "PYTHON_MASTER", 2);
            QuizCourse course3 = createQuizCourse(3L, "CS_BASIC", "CS 기초",
                    "CS 설명", 6, "CS_MASTER", 3);

            Badge javaBadge = createBadge(1L, "JAVA_MASTER", "Java 마스터");
            Badge pythonBadge = createBadge(2L, "PYTHON_MASTER", "Python 마스터");
            Badge csBadge = createBadge(3L, "CS_MASTER", "CS 마스터");

            given(quizCourseRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc())
                    .willReturn(List.of(course1, course2, course3));
            given(badgeRepository.findByCodeIn(List.of("JAVA_MASTER", "PYTHON_MASTER", "CS_MASTER")))
                    .willReturn(List.of(javaBadge, pythonBadge, csBadge));

            // when
            QuizCourseListResponse response = quizCourseService.getCourseList();

            // then
            assertThat(response.courses()).hasSize(3);
            assertThat(response.courses().get(0).code()).isEqualTo("JAVA");
            assertThat(response.courses().get(1).code()).isEqualTo("PYTHON");
            assertThat(response.courses().get(2).code()).isEqualTo("CS_BASIC");
        }

        @Test
        @DisplayName("일부 코스만 배지 코드가 있으면 해당 코스만 배지 이름이 있다")
        void returnMixedBadgeNames() {
            // given
            QuizCourse courseWithBadge = createQuizCourse(1L, "JAVA", "Java 마스터",
                    "Java 설명", 5, "JAVA_MASTER", 1);
            QuizCourse courseWithoutBadge = createQuizCourse(2L, "INTRO", "입문 코스",
                    "입문 설명", 2, null, 2);

            Badge javaBadge = createBadge(1L, "JAVA_MASTER", "Java 마스터");

            given(quizCourseRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc())
                    .willReturn(List.of(courseWithBadge, courseWithoutBadge));
            given(badgeRepository.findByCodeIn(List.of("JAVA_MASTER")))
                    .willReturn(List.of(javaBadge));

            // when
            QuizCourseListResponse response = quizCourseService.getCourseList();

            // then
            assertThat(response.courses()).hasSize(2);
            assertThat(response.courses().get(0).badgeName()).isEqualTo("Java 마스터");
            assertThat(response.courses().get(1).badgeName()).isNull();
        }

        @Test
        @DisplayName("동일한 배지 코드를 가진 코스가 여러 개여도 정상 처리된다")
        void handleDuplicateBadgeCodes() {
            // given
            QuizCourse course1 = createQuizCourse(1L, "JAVA_BASIC", "Java 기초",
                    "Java 기초 설명", 3, "JAVA_MASTER", 1);
            QuizCourse course2 = createQuizCourse(2L, "JAVA_ADVANCED", "Java 고급",
                    "Java 고급 설명", 5, "JAVA_MASTER", 2);

            Badge javaBadge = createBadge(1L, "JAVA_MASTER", "Java 마스터");

            given(quizCourseRepository.findAllByIsActiveTrueOrderBySortOrderAscIdAsc())
                    .willReturn(List.of(course1, course2));
            given(badgeRepository.findByCodeIn(List.of("JAVA_MASTER")))
                    .willReturn(List.of(javaBadge));

            // when
            QuizCourseListResponse response = quizCourseService.getCourseList();

            // then
            assertThat(response.courses()).hasSize(2);
            assertThat(response.courses().get(0).badgeName()).isEqualTo("Java 마스터");
            assertThat(response.courses().get(1).badgeName()).isEqualTo("Java 마스터");
        }
    }

    @Nested
    @DisplayName("getCourseDetail 메서드는")
    class GetCourseDetail {

        @Test
        @DisplayName("코스 상세를 섹션과 배지 정보까지 포함해 반환한다")
        void returnCourseDetailWithSectionsAndBadge() {
            // given
            QuizCourse course = createQuizCourse(1L, "JAVA", "Java 마스터",
                    "Java 기초부터 고급까지", 5, "JAVA_MASTER", 0);
            QuizCourseSection section1 = createQuizCourseSection(1L, 1, "기본 문법",
                    "변수, 자료형, 연산자", 10, 70);
            QuizCourseSection section2 = createQuizCourseSection(2L, 2, "객체지향",
                    "클래스, 상속, 다형성", 15, 70);
            ReflectionTestUtils.setField(course, "sections", List.of(section1, section2));

            Badge badge = createBadge(1L, "JAVA_MASTER", "Java 마스터");

            given(quizCourseRepository.findByIdWithSections(1L))
                    .willReturn(Optional.of(course));
            given(badgeRepository.findByCode("JAVA_MASTER"))
                    .willReturn(Optional.of(badge));

            // when
            QuizCourseDetailResponse response = quizCourseService.getCourseDetail(1L);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.code()).isEqualTo("JAVA");
            assertThat(response.name()).isEqualTo("Java 마스터");
            assertThat(response.description()).isEqualTo("Java 기초부터 고급까지");
            assertThat(response.totalSections()).isEqualTo(5);
            assertThat(response.badge()).isEqualTo(new BadgeInfo("JAVA_MASTER", "Java 마스터", "테스트 배지 설명"));
            assertThat(response.sections()).hasSize(2);
            SectionSummary firstSection = response.sections().get(0);
            assertThat(firstSection.sectionNumber()).isEqualTo(1);
            assertThat(firstSection.name()).isEqualTo("기본 문법");
            assertThat(firstSection.description()).isEqualTo("변수, 자료형, 연산자");
            assertThat(firstSection.totalQuestions()).isEqualTo(10);
            assertThat(firstSection.passScore()).isEqualTo(70);
        }

        @Test
        @DisplayName("코스가 없으면 404 예외를 던진다")
        void throwNotFoundWhenCourseMissing() {
            // given
            given(quizCourseRepository.findByIdWithSections(99L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> quizCourseService.getCourseDetail(99L))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining(String.valueOf(HttpStatus.NOT_FOUND.value()));
        }

        @Test
        @DisplayName("비활성 코스는 404 예외를 던진다")
        void throwNotFoundWhenCourseInactive() {
            // given
            QuizCourse course = createQuizCourse(1L, "JAVA", "Java 마스터",
                    "Java 기초부터 고급까지", 5, "JAVA_MASTER", 0);
            ReflectionTestUtils.setField(course, "isActive", false);

            given(quizCourseRepository.findByIdWithSections(1L))
                    .willReturn(Optional.of(course));

            // when & then
            assertThatThrownBy(() -> quizCourseService.getCourseDetail(1L))
                    .isInstanceOf(ResponseStatusException.class)
                    .hasMessageContaining(String.valueOf(HttpStatus.NOT_FOUND.value()));
        }

        @Test
        @DisplayName("배지 코드만 있고 배지가 없으면 코드만 반환한다")
        void returnBadgeCodeOnlyWhenBadgeMissing() {
            // given
            QuizCourse course = createQuizCourse(1L, "JAVA", "Java 마스터",
                    "Java 기초부터 고급까지", 5, "JAVA_MASTER", 0);
            ReflectionTestUtils.setField(course, "sections", Collections.emptyList());

            given(quizCourseRepository.findByIdWithSections(1L))
                    .willReturn(Optional.of(course));
            given(badgeRepository.findByCode("JAVA_MASTER"))
                    .willReturn(Optional.empty());

            // when
            QuizCourseDetailResponse response = quizCourseService.getCourseDetail(1L);

            // then
            assertThat(response.badge()).isEqualTo(new BadgeInfo("JAVA_MASTER", null, null));
        }

        @Test
        @DisplayName("섹션이 없는 코스도 빈 배열로 정상 반환한다")
        void returnEmptySectionsWhenNoSections() {
            // given
            QuizCourse course = createQuizCourse(1L, "JAVA", "Java 마스터",
                    "Java 기초부터 고급까지", 5, "JAVA_MASTER", 0);
            ReflectionTestUtils.setField(course, "sections", Collections.emptyList());

            Badge badge = createBadge(1L, "JAVA_MASTER", "Java 마스터");

            given(quizCourseRepository.findByIdWithSections(1L))
                    .willReturn(Optional.of(course));
            given(badgeRepository.findByCode("JAVA_MASTER"))
                    .willReturn(Optional.of(badge));

            // when
            QuizCourseDetailResponse response = quizCourseService.getCourseDetail(1L);

            // then
            assertThat(response.sections()).isEmpty();
        }

        @Test
        @DisplayName("배지 코드가 없으면 badge는 null로 반환한다")
        void returnNullBadgeWhenNoBadgeCode() {
            // given
            QuizCourse course = createQuizCourse(1L, "JAVA", "Java 마스터",
                    "Java 기초부터 고급까지", 5, null, 0);
            ReflectionTestUtils.setField(course, "sections", Collections.emptyList());

            given(quizCourseRepository.findByIdWithSections(1L))
                    .willReturn(Optional.of(course));

            // when
            QuizCourseDetailResponse response = quizCourseService.getCourseDetail(1L);

            // then
            assertThat(response.badge()).isNull();
        }
    }

    private QuizCourse createQuizCourse(Long id, String code, String name,
            String description, Integer totalSections, String badgeCode, Integer sortOrder) {
        try {
            Constructor<QuizCourse> constructor = QuizCourse.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            QuizCourse course = constructor.newInstance();
            ReflectionTestUtils.setField(course, "id", id);
            ReflectionTestUtils.setField(course, "code", code);
            ReflectionTestUtils.setField(course, "name", name);
            ReflectionTestUtils.setField(course, "description", description);
            ReflectionTestUtils.setField(course, "totalSections", totalSections);
            ReflectionTestUtils.setField(course, "badgeCode", badgeCode);
            ReflectionTestUtils.setField(course, "sortOrder", sortOrder);
            ReflectionTestUtils.setField(course, "isActive", true);
            return course;
        } catch (Exception e) {
            throw new RuntimeException("QuizCourse 테스트 객체 생성 실패", e);
        }
    }

    private Badge createBadge(Long id, String code, String name) {
        try {
            Constructor<Badge> constructor = Badge.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Badge badge = constructor.newInstance();
            ReflectionTestUtils.setField(badge, "id", id);
            ReflectionTestUtils.setField(badge, "code", code);
            ReflectionTestUtils.setField(badge, "name", name);
            ReflectionTestUtils.setField(badge, "description", "테스트 배지 설명");
            ReflectionTestUtils.setField(badge, "category", BadgeCategory.MASTER);
            ReflectionTestUtils.setField(badge, "conditionType", "COURSE_COMPLETE");
            ReflectionTestUtils.setField(badge, "conditionValue", 1);
            return badge;
        } catch (Exception e) {
            throw new RuntimeException("Badge 테스트 객체 생성 실패", e);
        }
    }

    private QuizCourseSection createQuizCourseSection(Long quizCourseId, Integer sectionNumber, String name,
            String description, Integer totalQuestions, Integer passScore) {
        try {
            Constructor<QuizCourseSection> constructor = QuizCourseSection.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            QuizCourseSection section = constructor.newInstance();

            // 이 부분 복합키 quizCourseId로 변경되었는데, 변경되지 않아서 테스트 실패 했었음(수정 완료)
            ReflectionTestUtils.setField(section, "quizCourseId", quizCourseId);
            ReflectionTestUtils.setField(section, "sectionNumber", sectionNumber);
            ReflectionTestUtils.setField(section, "name", name);
            ReflectionTestUtils.setField(section, "description", description);
            ReflectionTestUtils.setField(section, "totalQuestions", totalQuestions);
            ReflectionTestUtils.setField(section, "passScore", passScore);
            return section;
        } catch (Exception e) {
            throw new RuntimeException("QuizCourseSection 테스트 객체 생성 실패", e);
        }
    }
}
