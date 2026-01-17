package com.ssafy.domain.notification.repository;

import com.ssafy.domain.notification.entity.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

}
