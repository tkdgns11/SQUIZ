package com.ssafy.domain.quiz.dto.response;

import com.ssafy.domain.quiz.entity.enums.AttemptStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 섹션 시도 응답 DTO.
 *
 * 시도 시작 또는 재개 시 반환되는 정보.
 *
 * @param attemptId 시도 ID
 * @param sectionNumber 섹션 번호
 * @param sectionName 섹션 이름
 * @param status 시도 상태
 * @param totalQuestions 총 문제 수
 * @param answeredCount 답변 완료 수
 * @param passScore 통과 점수 (%)
 * @param startedAt 시도 시작 시각
 * @param questions 문제 목록 (order_index 순서)
 */
@Schema(description = "섹션 시도 응답")
public record SectionAttemptResponse(
        @Schema(description = "시도 ID", example = "1")
        Long attemptId,

        @Schema(description = "섹션 번호", example = "1")
        Integer sectionNumber,

        @Schema(description = "섹션 이름", example = "기본 문법")
        String sectionName,

        @Schema(description = "시도 상태", example = "IN_PROGRESS")
        AttemptStatus status,

        @Schema(description = "총 문제 수", example = "30")
        Integer totalQuestions,

        @Schema(description = "답변 완료 수", example = "10")
        Integer answeredCount,

        @Schema(description = "통과 점수 (%)", example = "70")
        Integer passScore,

        @Schema(description = "시도 시작 시각")
        LocalDateTime startedAt,

        @Schema(description = "문제 목록 (order_index 순서)")
        List<AttemptQuestionItem> questions
) {
}
