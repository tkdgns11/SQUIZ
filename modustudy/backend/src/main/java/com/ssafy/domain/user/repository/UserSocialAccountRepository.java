package com.ssafy.domain.user.repository;

import com.ssafy.domain.user.entity.UserSocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSocialAccountRepository extends JpaRepository<UserSocialAccount, Long> {

}
