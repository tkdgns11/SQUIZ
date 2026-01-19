package com.ssafy.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenRefreshResponse {
    private String accessToken;
    private Integer expiresIn;
}