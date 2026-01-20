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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OAuth2Service {

    private final UserRepository userRepository;
    private final UserSocialAccountRepository socialAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final RestTemplate restTemplate;

    // ==================== 카카오 ====================

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
                "&scope=profile_image,account_email";
    }

    /**
     * 카카오 로그인 콜백 처리
     */
    public AuthResponse processKakaoCallback(String code) {
        log.info("카카오 로그인 콜백 처리 시작");

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
            user = existingSocial.get().getUser();
            user.setLastLoginAt(LocalDateTime.now());
            user.setIsOnline(true);
            isNewUser = false;
            log.info("기존 회원 로그인: userId={}", user.getId());
        } else {
            user = createNewUserForKakao(userInfo);
            isNewUser = true;
            log.info("신규 회원 가입: userId={}", user.getId());
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
                .user(UserDTO.from(user, SocialProvider.KAKAO))
                .loginProvider("KAKAO")
                .build();
    }

    /**
     * Authorization Code로 Access Token 받기 (카카오)
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
     * Access Token으로 사용자 정보 받기 (카카오)
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
     * 신규 회원 생성 (카카오)
     */
    private User createNewUserForKakao(OAuth2UserInfo userInfo) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .nickname(null)
                .profileImage(userInfo.getProfileImageUrl())
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

    // ==================== 네이버 ====================

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri:http://localhost:8080/login/oauth2/code/naver}")
    private String naverRedirectUri;

    /**
     * 네이버 로그인 URL 생성
     */
    public String getNaverAuthUrl() {
        return "https://nid.naver.com/oauth2.0/authorize?" +
                "client_id=" + naverClientId +
                "&redirect_uri=" + naverRedirectUri +
                "&response_type=code" +
                "&state=random_state_string";
    }

    /**
     * 네이버 로그인 콜백 처리
     */
    public AuthResponse processNaverCallback(String code, String state) {
        log.info("네이버 로그인 콜백 처리 시작");

        // 1. Authorization Code로 Access Token 받기
        String naverAccessToken = getNaverAccessToken(code, state);

        // 2. Access Token으로 사용자 정보 받기
        OAuth2UserInfo userInfo = getNaverUserInfo(naverAccessToken);

        // 3. 기존 회원 확인 또는 신규 회원 생성
        Optional<UserSocialAccount> existingSocial = socialAccountRepository
                .findByProviderAndProviderUserId(SocialProvider.NAVER, userInfo.getProviderId());

        User user;
        boolean isNewUser;

        if (existingSocial.isPresent()) {
            user = existingSocial.get().getUser();
            user.setLastLoginAt(LocalDateTime.now());
            user.setIsOnline(true);
            isNewUser = false;
            log.info("기존 회원 로그인: userId={}", user.getId());
        } else {
            user = createNewUserForNaver(userInfo);
            isNewUser = true;
            log.info("신규 회원 가입: userId={}", user.getId());
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
                .user(UserDTO.from(user, SocialProvider.NAVER))
                .loginProvider("NAVER")
                .build();
    }

    /**
     * Authorization Code로 Access Token 받기 (네이버)
     */
    private String getNaverAccessToken(String code, String state) {
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", naverClientId);
        params.add("client_secret", naverClientSecret);
        params.add("code", code);
        params.add("state", state);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        Map<String, Object> body = response.getBody();

        if (body.containsKey("error")) {
            throw new RuntimeException("네이버 토큰 발급 실패: " + body.get("error_description"));
        }

        return (String) body.get("access_token");
    }

    /**
     * Access Token으로 사용자 정보 받기 (네이버)
     */
    private OAuth2UserInfo getNaverUserInfo(String accessToken) {
        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                request,
                Map.class
        );

        return OAuth2UserInfo.fromNaver(response.getBody());
    }

    /**
     * 신규 회원 생성 (네이버)
     */
    private User createNewUserForNaver(OAuth2UserInfo userInfo) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .nickname(null)
                .profileImage(userInfo.getProfileImageUrl())
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

        UserSocialAccount socialAccount = UserSocialAccount.builder()
                .user(savedUser)
                .provider(SocialProvider.NAVER)
                .providerUserId(userInfo.getProviderId())
                .email(userInfo.getEmail())
                .isPrimary(true)
                .linkedAt(LocalDateTime.now())
                .build();

        socialAccountRepository.save(socialAccount);

        return savedUser;
    }

    // ==================== 구글 ====================

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri:http://localhost:8080/login/oauth2/code/google}")
    private String googleRedirectUri;

    /**
     * 구글 로그인 URL 생성
     */
    public String getGoogleAuthUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + googleClientId +
                "&redirect_uri=" + googleRedirectUri +
                "&response_type=code" +
                "&scope=openid%20profile%20email" +
                "&access_type=offline" +
                "&prompt=consent";
    }

    /**
     * 구글 로그인 콜백 처리
     */
    public AuthResponse processGoogleCallback(String code) {
        log.info("구글 로그인 콜백 처리 시작");

        // 1. Authorization Code로 Access Token 받기
        String googleAccessToken = getGoogleAccessToken(code);

        // 2. Access Token으로 사용자 정보 받기
        OAuth2UserInfo userInfo = getGoogleUserInfo(googleAccessToken);

        // 3. 기존 회원 확인 또는 신규 회원 생성
        Optional<UserSocialAccount> existingSocial = socialAccountRepository
                .findByProviderAndProviderUserId(SocialProvider.GOOGLE, userInfo.getProviderId());

        User user;
        boolean isNewUser;

        if (existingSocial.isPresent()) {
            user = existingSocial.get().getUser();
            user.setLastLoginAt(LocalDateTime.now());
            user.setIsOnline(true);
            isNewUser = false;
            log.info("기존 회원 로그인: userId={}", user.getId());
        } else {
            user = createNewUserForGoogle(userInfo);
            isNewUser = true;
            log.info("신규 회원 가입: userId={}", user.getId());
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
                .user(UserDTO.from(user, SocialProvider.GOOGLE))
                .loginProvider("GOOGLE")
                .build();
    }

    /**
     * Authorization Code로 Access Token 받기 (구글)
     */
    private String getGoogleAccessToken(String code) {
        String tokenUrl = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, request, Map.class);

        Map<String, Object> body = response.getBody();

        if (body.containsKey("error")) {
            throw new RuntimeException("구글 토큰 발급 실패: " + body.get("error_description"));
        }

        return (String) body.get("access_token");
    }

    /**
     * Access Token으로 사용자 정보 받기 (구글)
     */
    private OAuth2UserInfo getGoogleUserInfo(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                request,
                Map.class
        );

        return OAuth2UserInfo.fromGoogle(response.getBody());
    }

    /**
     * 신규 회원 생성 (구글)
     */
    private User createNewUserForGoogle(OAuth2UserInfo userInfo) {
        User user = User.builder()
                .email(userInfo.getEmail())
                .name(userInfo.getName())
                .nickname(null)
                .profileImage(userInfo.getProfileImageUrl())
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

        UserSocialAccount socialAccount = UserSocialAccount.builder()
                .user(savedUser)
                .provider(SocialProvider.GOOGLE)
                .providerUserId(userInfo.getProviderId())
                .email(userInfo.getEmail())
                .isPrimary(true)
                .linkedAt(LocalDateTime.now())
                .build();

        socialAccountRepository.save(socialAccount);

        return savedUser;
    }

    // ==================== 공통 ====================

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

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setIsOnline(false);
        user.setLastSeenAt(LocalDateTime.now());

        userRepository.save(user);
    }
}