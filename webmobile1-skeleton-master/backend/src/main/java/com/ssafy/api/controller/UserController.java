package com.ssafy.api.controller;

import com.ssafy.api.request.UserRegisterPostReq;
import com.ssafy.api.response.UserRes;
import com.ssafy.api.service.UserService;
import com.ssafy.common.auth.SsafyUserDetails;
import com.ssafy.common.model.response.BaseResponseBody;
import com.ssafy.db.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "유저 API")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "회원 가입", description = "아이디와 패스워드를 통해 회원가입 합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponseBody> register(@RequestBody UserRegisterPostReq registerInfo) {
        userService.createUser(registerInfo);
        return ResponseEntity.ok(BaseResponseBody.of(200, "Success"));
    }

    @GetMapping("/me")
    @Operation(summary = "회원 본인 정보 조회", description = "로그인한 회원 본인의 정보를 응답합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<UserRes> getUserInfo(@Parameter(hidden = true) Authentication authentication) {
        SsafyUserDetails userDetails = (SsafyUserDetails) authentication.getPrincipal();
        String userId = userDetails.getUsername();
        User user = userService.getUserByUserId(userId);

        return ResponseEntity.ok(UserRes.of(user));
    }
}
