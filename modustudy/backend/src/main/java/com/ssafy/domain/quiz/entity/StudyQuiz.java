package com.ssafy.domain.quiz.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "study_quiz")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyQuiz extends BaseEntity {
}
