package com.ssafy.domain.quiz.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 퀴즈 대회 답안
 */
@Entity
@Table(name = "quiz_answer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizAnswer {

    @Id
    @Column(name = "quiz_answer_id")
    private Integer id;

}
