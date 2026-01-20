package com.ssafy.domain.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileSetupRequest {
    private String nickname;
    private String password;
}