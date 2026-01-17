package com.ssafy.domain.study.repository;

import com.ssafy.domain.study.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
}
