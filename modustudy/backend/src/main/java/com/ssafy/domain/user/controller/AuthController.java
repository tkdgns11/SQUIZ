package com.ssafy.domain.user.controller;

import com.ssafy.common.response.ApiResponse;
import com.ssafy.domain.user.dto.request.OAuth2CallbackRequest;
import com.ssafy.domain.user.dto.request.TokenRefreshRequest;
import com.ssafy.domain.user.dto.response.AuthResponse;
import com.ssafy.domain.user.dto.response.TokenRefreshResponse;
import com.ssafy.domain.user.service.OAuth2Service;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuth2Service oAuth2Service;

    /**
     * 1. 소셜 로그인 URL 요청
     * GET /api/v1/auth/oauth/{provider}
     */
    @GetMapping("/oauth/{provider}")
    public ResponseEntity<ApiResponse<AuthUrlResponse>> getAuthorizationUrl(
            @PathVariable String provider) {

        String authUrl;

        switch (provider.toLowerCase()) {
            case "kakao":
                authUrl = oAuth2Service.getKakaoAuthUrl();
                break;
            case "naver":
                authUrl = oAuth2Service.getNaverAuthUrl();
                break;
            case "google":
                authUrl = oAuth2Service.getGoogleAuthUrl();
                break;
            default:
                return ResponseEntity
                        .badRequest()
                        .body(ApiResponse.error("INVALID_PROVIDER", "지원하지 않는 OAuth 제공자입니다."));
        }

        AuthUrlResponse response = new AuthUrlResponse(authUrl);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 2. 소셜 로그인 콜백 처리
     * POST /api/v1/auth/oauth/{provider}/callback
     */
    @PostMapping("/oauth/{provider}/callback")
    public ResponseEntity<ApiResponse<AuthResponse>> handleCallback(
            @PathVariable String provider,
            @RequestBody OAuth2CallbackRequest request,
            @RequestParam(required = false) String state) {  // 네이버용 state 파라미터

        AuthResponse response;

        switch (provider.toLowerCase()) {
            case "kakao":
                response = oAuth2Service.processKakaoCallback(request.getCode());
                break;
            case "naver":
                response = oAuth2Service.processNaverCallback(request.getCode(), state);
                break;
            case "google":
                response = oAuth2Service.processGoogleCallback(request.getCode());
                break;
            default:
                return ResponseEntity
                        .badRequest()
                        .body(ApiResponse.error("INVALID_PROVIDER", "지원하지 않는 OAuth 제공자입니다."));
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 3. Access Token 갱신
     * POST /api/v1/auth/token/refresh
     */
    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @RequestBody TokenRefreshRequest request) {

        String newAccessToken = oAuth2Service.refreshAccessToken(request.getRefreshToken());

        TokenRefreshResponse response = TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn(3600)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 내부 클래스: AuthUrlResponse
     */
    @Getter
    @AllArgsConstructor
    private static class AuthUrlResponse {
        private String authUrl;
    }

}