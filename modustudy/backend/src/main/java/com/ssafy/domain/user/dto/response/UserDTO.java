package com.ssafy.domain.user.dto.response;

import com.ssafy.domain.user.entity.User;
import com.ssafy.domain.user.entity.UserSocialAccount;
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
        // Primary 소셜 계정 찾기
        String provider = null;
        if (user.getSocialAccounts() != null && !user.getSocialAccounts().isEmpty()) {
            provider = user.getSocialAccounts().stream()
                    .filter(UserSocialAccount::getIsPrimary)
                    .findFirst()
                    .map(account -> account.getProvider().name())
                    .orElse(null);
        }

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImage(user.getProfileImage())
                .loginProvider(provider)
                .build();
    }
}