package com.ssafy.domain.quiz.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wrong_answer_note")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WrongAnswerNote extends BaseEntity {

}
