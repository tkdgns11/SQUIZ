package com.ssafy.domain.quiz.controller;

import com.ssafy.common.response.ApiResponse;
import com.ssafy.domain.quiz.dto.response.QuizCourseListResponse;
import com.ssafy.domain.quiz.service.QuizCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
}
