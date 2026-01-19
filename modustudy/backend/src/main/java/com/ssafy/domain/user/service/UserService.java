package com.ssafy.domain.user.service;

import com.ssafy.domain.user.entity.User;

import java.util.Optional;

public interface UserService {

    /**
     * userId로 사용자 조회
     */
    Optional<User> getUserByUserId(String userId);

    /**
     * id로 사용자 조회
     */
    Optional<User> getUserById(Long id);

    /**
     * email로 사용자 조회
     */
    Optional<User> getUserByEmail(String email);
}