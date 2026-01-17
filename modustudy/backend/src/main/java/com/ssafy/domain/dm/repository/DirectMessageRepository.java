package com.ssafy.domain.dm.repository;

import com.ssafy.domain.dm.entity.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
}
