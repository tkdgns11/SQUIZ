package com.ssafy.domain.user.dto.response;

import com.ssafy.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String nickname;
    private String profileImage;
    private String loginProvider;

    public static UserDTO from(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(null)
                .loginProvider(null)
                .build();
    }
}