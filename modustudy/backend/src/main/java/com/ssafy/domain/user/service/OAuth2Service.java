package com.ssafy.domain.user.service;

import com.ssafy.common.util.JwtTokenUtil;
import com.ssafy.domain.user.dto.OAuth2UserInfo;
import com.ssafy.domain.user.dto.response.AuthResponse;
import com.ssafy.domain.user.dto.response.UserDTO;
import com.ssafy.domain.user.entity.Role;
import com.ssafy.domain.user.entity.*;
import com.ssafy.domain.user.repository.RefreshTokenRepository;
import com.ssafy.domain.user.repository.UserRepository;
import com.ssafy.domain.user.repository.UserSocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2Service {

    private final UserRepository userRepository;
    private final UserSocialAccountRepository socialAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final RestTemplate restTemplate;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String kakaoClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri:http://localhost:8080/login/oauth2/code/kakao}")
    private String kakaoRedirectUri;

    /**
     * 카카오 로그인 URL 생성
     */
    public String getKakaoAuthUrl() {
        return "https://kauth.kakao.com/oauth/authorize?" +
                "client_id=" + kakaoClientId +
                "&redirect_uri=" + kakaoRedirectUri +
                "&response_type=code" +
                "&scope=profile_nickname,profile_image,account_email";
    }

    /**
     * 카카오 로그인 콜백 처리
     */
    public AuthResponse processKakaoCallback(String code) {
        // 1. Authorization Code로 Access Token 받기
        String kakaoAccessToken = getKakaoAccessToken(code);

        // 2. Access Token으로 사용자 정보 받기
        OAuth2UserInfo userInfo = getKakaoUserInfo(kakaoAccessToken);

        // 3. 기존 회원 확인 또는 신규 회원 생성
        Optional<UserSocialAccount> existingSocial = socialAccountRepository
                .findByProviderAndProviderUserId(SocialProvider.KAKAO, userInfo.getProviderId());

        User user;
        boolean isNewUser;

        if (existingSocial.isPresent()) {
            // 기존 회원 로그인
            user = existingSocial.get().getUser();
            user.setLastLoginAt(LocalDateTime.now());
            user.setIsOnline(true);
            isNewUser = false;
        } else {
            // 신규 회원가입
            user = createNewUser(userInfo);
            isNewUser = true;
        }

        userRepository.save(user);

        // 4. JWT 토큰 발급
        String accessToken = jwtTokenUtil.createAccessToken(String.valueOf(user.getId()));
        String refreshToken = jwtTokenUtil.createRefreshToken(String.valueOf(user.getId()));

        // 5. Refresh Token 저장
        saveRefreshToken(user, refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(3600)
                .isNewUser(isNewUser)
                .user(UserDTO.from(user))
                .build();
    }

    /**
     * Authorization Code로 Access Token 받기
     */
    private String getKakaoAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("client_secret", kakaoClientSecret);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        return (String) response.getBody().get("access_token");
    }

    /**
     * Access Token으로 사용자 정보 받기
     */
    private OAuth2UserInfo getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                request,
                Map.class
        );

        return OAuth2UserInfo.fromKakao(response.getBody());
    }

    /**
     * 신규 회원 생성
     */
    private User createNewUser(OAuth2UserInfo userInfo) {
        // User 생성
        User user = User.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .nickname(null)
                .role(Role.USER)
                .isActive(true)
                .lastLoginAt(LocalDateTime.now())
                .isOnline(true)
                .isSearchable(true)
                .totalExp(0)
                .currentPoints(0)
                .currentLevel(1)
                .levelName("새싹")
                .build();

        User savedUser = userRepository.save(user);

        // SocialAccount 생성
        UserSocialAccount socialAccount = UserSocialAccount.builder()
                .user(savedUser)
                .provider(SocialProvider.KAKAO)
                .providerUserId(userInfo.getProviderId())
                .email(userInfo.getEmail())
                .isPrimary(true)
                .linkedAt(LocalDateTime.now())
                .build();

        socialAccountRepository.save(socialAccount);

        return savedUser;
    }

    /**
     * Refresh Token 저장
     */
    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .isRevoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    /**
     * Access Token 갱신
     */
    public String refreshAccessToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (token.getIsRevoked() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Refresh token expired");
        }

        return jwtTokenUtil.createAccessToken(String.valueOf(token.getUser().getId()));
    }
}