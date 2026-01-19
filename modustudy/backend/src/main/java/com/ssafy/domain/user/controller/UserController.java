package com.ssafy.domain.user.controller;

import com.ssafy.common.auth.SsafyUserDetails;
import com.ssafy.common.response.ApiResponse;
import com.ssafy.domain.user.dto.request.ProfileSetupRequest;
import com.ssafy.domain.user.dto.request.UserUpdateRequest;
import com.ssafy.domain.user.dto.response.UserDTO;
import com.ssafy.domain.user.entity.User;
import com.ssafy.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 API Controller
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 내 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getMyInfo(Authentication authentication) {
        SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        User user = userService.getUserWithSocialAccounts(userId);

        return ResponseEntity.ok(
                ApiResponse.success(UserDTO.from(user))
        );
    }

    /**
     * 추가 정보 입력 (최초 로그인)
     * POST /api/v1/users/me/profile
     */
    @PostMapping("/me/profile")
    public ResponseEntity<ApiResponse<UserDTO>> setupProfile(
            Authentication authentication,
            @RequestBody ProfileSetupRequest request) {

        SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        User updatedUser = userService.setupProfile(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success(UserDTO.from(updatedUser))
        );
    }

    /**
     * 내 정보 수정
     * PUT /api/v1/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> updateMyInfo(
            Authentication authentication,
            @RequestBody UserUpdateRequest request) {

        SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        User updatedUser = userService.updateUserInfo(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success(UserDTO.from(updatedUser))
        );
    }
}