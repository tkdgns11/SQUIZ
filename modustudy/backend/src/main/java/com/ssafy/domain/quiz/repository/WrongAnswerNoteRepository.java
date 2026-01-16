package com.ssafy.domain.quiz.repository;

import com.ssafy.domain.quiz.entity.WrongAnswerNote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WrongAnswerNoteRepository extends JpaRepository<WrongAnswerNote, Long> {

}
