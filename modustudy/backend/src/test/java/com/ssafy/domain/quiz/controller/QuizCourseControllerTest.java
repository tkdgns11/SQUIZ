package com.ssafy.domain.quiz.controller;

import com.ssafy.common.response.ApiResponse;
import com.ssafy.domain.quiz.dto.response.QuizCourseListItem;
import com.ssafy.domain.quiz.dto.response.QuizCourseListResponse;
import com.ssafy.domain.quiz.service.QuizCourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * QuizCourseController 단위 테스트.
 *
 * 테스트 대상: {@link QuizCourseController#getCourseList()}
 */
@ExtendWith(MockitoExtension.class)
class QuizCourseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuizCourseService quizCourseService;

    @InjectMocks
    private QuizCourseController quizCourseController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(quizCourseController).build();
    }

    @Nested
    @DisplayName("GET /api/v1/quiz-courses")
    class GetCourseList {

        @Test
        @DisplayName("코스 목록 조회 시 200 OK와 코스 목록을 반환한다")
        void returnCourseListWithStatus200() throws Exception {
            // given
            List<QuizCourseListItem> items = List.of(
                    new QuizCourseListItem(1L, "JAVA", "Java 마스터",
                            "Java 기초부터 고급까지", 5, "JAVA_MASTER", "Java 마스터"),
                    new QuizCourseListItem(2L, "PYTHON", "Python 기초",
                            "Python 입문자를 위한 코스", 4, "PYTHON_MASTER", "Python 마스터"),
                    new QuizCourseListItem(3L, "CS_BASIC", "CS 기초",
                            "컴퓨터 과학 기초 개념", 6, "CS_MASTER", "CS 마스터")
            );
            QuizCourseListResponse response = new QuizCourseListResponse(items);

            given(quizCourseService.getCourseList()).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/quiz-courses")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("Success"))
                    .andExpect(jsonPath("$.data.courses").isArray())
                    .andExpect(jsonPath("$.data.courses.length()").value(3))
                    .andExpect(jsonPath("$.data.courses[0].id").value(1))
                    .andExpect(jsonPath("$.data.courses[0].code").value("JAVA"))
                    .andExpect(jsonPath("$.data.courses[0].name").value("Java 마스터"))
                    .andExpect(jsonPath("$.data.courses[0].description").value("Java 기초부터 고급까지"))
                    .andExpect(jsonPath("$.data.courses[0].totalSections").value(5))
                    .andExpect(jsonPath("$.data.courses[0].badgeCode").value("JAVA_MASTER"))
                    .andExpect(jsonPath("$.data.courses[0].badgeName").value("Java 마스터"));
        }

        @Test
        @DisplayName("코스가 없으면 빈 배열을 반환한다")
        void returnEmptyListWhenNoCoursesExist() throws Exception {
            // given
            QuizCourseListResponse response = new QuizCourseListResponse(Collections.emptyList());
            given(quizCourseService.getCourseList()).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/quiz-courses")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.courses").isArray())
                    .andExpect(jsonPath("$.data.courses").isEmpty());
        }

        @Test
        @DisplayName("배지 코드가 없는 코스는 badgeName이 null이다")
        void returnNullBadgeNameWhenNoBadgeCode() throws Exception {
            // given
            List<QuizCourseListItem> items = List.of(
                    new QuizCourseListItem(1L, "INTRO", "입문 코스",
                            "입문 설명", 2, null, null)
            );
            QuizCourseListResponse response = new QuizCourseListResponse(items);

            given(quizCourseService.getCourseList()).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/quiz-courses")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.courses[0].badgeCode").doesNotExist())
                    .andExpect(jsonPath("$.data.courses[0].badgeName").doesNotExist());
        }

        @Test
        @DisplayName("인증 없이도 접근 가능하다")
        void accessibleWithoutAuthentication() throws Exception {
            // given
            QuizCourseListResponse response = new QuizCourseListResponse(Collections.emptyList());
            given(quizCourseService.getCourseList()).willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/quiz-courses")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }
}
