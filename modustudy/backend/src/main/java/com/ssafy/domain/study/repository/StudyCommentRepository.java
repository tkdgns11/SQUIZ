package com.ssafy.domain.study.repository;

import com.ssafy.domain.study.entity.StudyComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCommentRepository extends JpaRepository<StudyComment, Long> {

}
