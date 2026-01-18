package com.ssafy.domain.user.service;

import com.ssafy.domain.user.dto.response.AuthResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
// @Disabled  // 주석처리하고 실행!
class OAuth2ServiceTest {

    @Autowired
    private OAuth2Service oAuth2Service;

    @Test
    void 카카오_인증코드로_로그인_테스트() {
        // ===== 여기에 복사한 code 붙여넣기 =====
        String code = "0vXpX89kOeuwE60-XOv6wIRXnv5tcKuOb2uhDr-BvQUEaGAma0-PbgAAAAQKFxTuAAABm9BgZ9mo9NUiJo7xnA";

        try {
            AuthResponse response = oAuth2Service.processKakaoCallback(code);

            System.out.println("========================================");
            System.out.println("✅ 카카오 로그인 테스트 성공!");
            System.out.println("========================================");
            System.out.println("📦 전체 응답: " + response);
            System.out.println("========================================");

            // 개별 필드가 있으면 출력
            if (response.getAccessToken() != null) {
                System.out.println("🔑 Access Token 있음!");
            }
            if (response.getRefreshToken() != null) {
                System.out.println("🔄 Refresh Token 있음!");
            }
            if (response.getUser() != null) {
                System.out.println("👤 사용자 정보: " + response.getUser());
            }

            System.out.println("========================================");

        } catch (Exception e) {
            System.out.println("========================================");
            System.out.println("❌ 테스트 실패!");
            System.out.println("========================================");
            System.out.println("에러: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
