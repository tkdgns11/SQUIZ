package com.ssafy.domain.quiz.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_course_progress")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCourseProgress extends BaseEntity {

}
