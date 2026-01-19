package com.ssafy.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "메시지 응답")
public class MessageResponse {

    @Schema(description = "응답 메시지", example = "로그아웃되었습니다.")
    private String message;
}