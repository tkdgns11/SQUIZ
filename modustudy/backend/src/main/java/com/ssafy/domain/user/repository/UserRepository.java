package com.ssafy.domain.user.repository;

import com.ssafy.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    /**
     * 닉네임 중복 체크
     */
    boolean existsByNickname(String nickname);

    /**
     * 닉네임으로 사용자 검색 (검색 허용 + 본인 제외)
     */
    List<User> findByNicknameContainingAndIsSearchableTrueAndIdNot(String nickname, Long excludeId);
}