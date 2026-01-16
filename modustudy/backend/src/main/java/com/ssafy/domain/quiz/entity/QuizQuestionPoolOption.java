package com.ssafy.domain.quiz.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quiz_question_pool_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizQuestionPoolOption {

}
