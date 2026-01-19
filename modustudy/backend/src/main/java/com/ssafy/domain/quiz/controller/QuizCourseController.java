package com.ssafy.domain.quiz.controller;

import com.ssafy.common.auth.SsafyUserDetails;
import com.ssafy.common.response.ApiResponse;
import com.ssafy.domain.quiz.dto.response.QuizCourseDetailResponse;
import com.ssafy.domain.quiz.dto.response.QuizCourseListResponse;
import com.ssafy.domain.quiz.dto.response.SectionQuestionsResponse;
import com.ssafy.domain.quiz.service.QuizCourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 퀴즈 코스 공개 API 컨트롤러.
 *
 * API 문서: docs/api/API_21_퀴즈코스.md
 *
 * 호출 경로:
 * - Web/Mobile 코스 목록 화면(SC-190, M-080)에서 GET /api/v1/quiz-courses
 *
 */
@RestController
@RequestMapping("/api/v1/quiz-courses")
@RequiredArgsConstructor
public class QuizCourseController {

    private final QuizCourseService quizCourseService;

    /**
     * 코스 목록 조회.
     *
     * 인증 없이 접근 가능한 공개 목록이며, 활성화된 코스만 반환.
     *
     * @return 코스 목록 응답
     */
    @GetMapping
    public ApiResponse<QuizCourseListResponse> getCourseList() {
        return ApiResponse.success(quizCourseService.getCourseList());
    }

    /**
     * 코스 상세 조회.
     *
     * 인증 없이 접근 가능한 공개 상세이며, 활성화된 코스만 반환.
     *
     * @param courseId 코스 ID
     * @return 코스 상세 응답
     */
    @GetMapping("/{courseId}")
    public ApiResponse<QuizCourseDetailResponse> getCourseDetail(@PathVariable Long courseId) {
        return ApiResponse.success(quizCourseService.getCourseDetail(courseId));
    }
    /**
     * 섹션 문제 조회.
     *
     * 인증이 필요하며, 해금된 섹션만 문제를 조회할 수 있다.
     * - 섹션 1은 항상 해금
     * - 섹션 N은 섹션 N-1을 70% 이상 통과해야 해금
     *
     * @param courseId 코스 ID
     * @param sectionId 섹션 ID
     * @param userDetails 인증된 사용자 정보
     * @return 섹션 문제 응답
     */
    @Operation(summary = "섹션 문제 조회", description = "해금된 섹션의 문제 목록을 조회합니다. 인증 필요.")
    @GetMapping("/{courseId}/sections/{sectionId}")
    public ApiResponse<SectionQuestionsResponse> getSectionQuestions(
            @Parameter(description = "코스 ID") @PathVariable Long courseId,
            @Parameter(description = "섹션 ID") @PathVariable Long sectionId,
            @AuthenticationPrincipal SsafyUserDetails userDetails) {
        Long userId = userDetails.getUser().getId();
        return ApiResponse.success(quizCourseService.getSectionQuestions(courseId, sectionId, userId));
    }
}
