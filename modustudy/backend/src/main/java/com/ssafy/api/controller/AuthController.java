package com.ssafy.api.controller;

import com.ssafy.api.request.UserLoginPostReq;
import com.ssafy.api.response.UserLoginPostRes;
import com.ssafy.common.util.JwtTokenUtil;
import com.ssafy.domain.user.entity.User;  // ← 수정!
import com.ssafy.domain.user.service.UserService;  // ← 수정!
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디와 패스워드를 통해 로그인 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<UserLoginPostRes> login(@RequestBody UserLoginPostReq loginInfo) {
        String userId = loginInfo.getId();
        String password = loginInfo.getPassword();

        Optional<User> userOpt = userService.getUserByUserId(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body(UserLoginPostRes.of(404, "User not found", null));
        }

        User user = userOpt.get();

        if (passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.ok(UserLoginPostRes.of(200, "Success", jwtTokenUtil.getToken(userId)));
        }

        return ResponseEntity.status(401).body(UserLoginPostRes.of(401, "Invalid Password", null));
    }
}