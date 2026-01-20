package com.ssafy.domain.quiz.dto.response;

import com.ssafy.domain.quiz.entity.enums.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 시도 문제 응답 DTO.
 *
 * 시도에 할당된 개별 문제 정보.
 * order_index 순서로 정렬되어 반환된다.
 *
 * @param orderIndex 셔플된 문제 순서 (1부터 시작)
 * @param questionId 문제 ID (답안 제출 시 사용)
 * @param questionText 문제 내용
 * @param questionType 문제 유형
 * @param options 객관식 보기 (단답형은 빈 리스트)
 * @param userAnswer 사용자 답안 (임시 저장된 값, null이면 미답변)
 */
@Schema(description = "시도 문제 정보")
public record AttemptQuestionItem(
        @Schema(description = "문제 순서", example = "1")
        Integer orderIndex,

        @Schema(description = "문제 ID", example = "123")
        Long questionId,

        @Schema(description = "문제 내용", example = "자바에서 정수형 변수를 선언하는 키워드는?")
        String questionText,

        @Schema(description = "문제 유형", example = "MULTIPLE_CHOICE")
        QuestionType questionType,

        @Schema(description = "객관식 보기")
        List<OptionItem> options,

        @Schema(description = "사용자 답안 (임시 저장된 값)", example = "A")
        String userAnswer
) {
}
