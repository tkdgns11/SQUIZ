package com.ssafy.domain.user.dto;

import com.ssafy.domain.user.entity.SocialProvider;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class OAuth2UserInfo {
    private String providerId;
    private String email;
    private String name;
    private String nickname;
    private String profileImageUrl;
    private SocialProvider provider;

    public static OAuth2UserInfo fromKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2UserInfo.builder()
                .providerId(String.valueOf(attributes.get("id")))
                .email((String) kakaoAccount.get("email"))
                .name((String) kakaoAccount.get("name"))
                .nickname((String) profile.get("nickname"))
                .profileImageUrl((String) profile.get("profile_image_url"))
                .provider(SocialProvider.KAKAO)
                .build();
    }
}