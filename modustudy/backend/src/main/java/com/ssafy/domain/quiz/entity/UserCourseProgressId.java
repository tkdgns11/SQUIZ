package com.ssafy.domain.quiz.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * UserCourseProgress 복합 키 클래스.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserCourseProgressId implements Serializable {

    private Long userId;
    private Long courseId;
}
