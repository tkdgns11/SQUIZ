package com.ssafy.domain.quiz.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wrong_answer_note")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WrongAnswerNote extends BaseEntity {
}
