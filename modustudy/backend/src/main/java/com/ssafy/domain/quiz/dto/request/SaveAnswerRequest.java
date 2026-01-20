package com.ssafy.domain.quiz.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 답안 임시 저장 요청 DTO.
 *
 * 진행 중인 시도에 대해 부분 답안을 저장할 때 사용한다.
 *
 * @param answers 저장할 답안 목록
 */
@Schema(description = "답안 임시 저장 요청")
public record SaveAnswerRequest(
        @Schema(description = "저장할 답안 목록")
        @NotNull
        List<AnswerItem> answers
) {
    /**
     * 개별 답안 항목.
     *
     * @param questionId 문제 ID
     * @param answer 사용자 답안
     */
    @Schema(description = "답안 항목")
    public record AnswerItem(
            @Schema(description = "문제 ID", example = "123")
            @NotNull
            Long questionId,

            @Schema(description = "사용자 답안", example = "A")
            String answer
    ) {
    }
}
