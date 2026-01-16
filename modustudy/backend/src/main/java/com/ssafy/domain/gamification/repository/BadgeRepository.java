package com.ssafy.domain.gamification.repository;

import com.ssafy.domain.gamification.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

}
