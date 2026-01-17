package com.ssafy.domain.quiz.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_course")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizCourse extends BaseEntity {
}
