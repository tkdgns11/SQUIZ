package com.ssafy.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import com.ssafy.domain.user.entity.User;

/**
 * 회원 본인 정보 조회 API ([GET] /api/v1/users/me) 요청에 대한 응답값 정의.
 */
@Getter
@Setter
@Schema(description = "유저 정보 응답")
public class UserRes {
    @Schema(description = "유저 ID")
    String userId;

    public static UserRes of(User user) {
        UserRes res = new UserRes();
        res.setUserId(user.getUserId());
        return res;
    }
}
