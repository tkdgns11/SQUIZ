package com.ssafy.domain.attendance.repository;

import com.ssafy.domain.attendance.entity.SessionMemo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionMemoRepository extends JpaRepository<SessionMemo, Long> {

}
