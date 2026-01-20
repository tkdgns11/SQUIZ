package com.ssafy.domain.quiz.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 섹션 문제 조회 응답 DTO.
 *
 * API: GET /api/v1/quiz-courses/{courseId}/sections/{sectionNumber}
 *
 * @param sectionNumber 섹션 번호 (코스 내 순서)
 * @param sectionName 섹션 이름
 * @param totalQuestions 총 문제 수
 * @param passScore 통과 점수 (%)
 * @param questions 문제 목록
 */
@Schema(description = "섹션 문제 조회 응답")
public record SectionQuestionsResponse(
        @Schema(description = "섹션 번호", example = "1")
        Integer sectionNumber,

        @Schema(description = "섹션 이름", example = "기본 문법")
        String sectionName,

        @Schema(description = "총 문제 수", example = "10")
        Integer totalQuestions,

        @Schema(description = "통과 점수 (%)", example = "70")
        Integer passScore,

        @Schema(description = "문제 목록")
        List<QuestionItem> questions
) {
}
