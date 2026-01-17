package com.ssafy.domain.quiz.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_answer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizAnswer extends BaseEntity {
}
