package com.ssafy.domain.quiz.entity;

import com.ssafy.common.entity.BaseEntity;
import com.ssafy.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자별 섹션 시도 기록 엔티티.
 *
 * 사용자가 섹션을 풀 때마다 기록되며, 점수와 통과 여부를 저장한다.
 * 한 섹션에 대해 여러 번 시도할 수 있다.
 *
 * DDL 참조: docs/sql/ERD.sql - user_section_attempt
 */
@Entity
@Table(name = "user_section_attempt")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSectionAttempt extends BaseEntity {

    /**
     * 시도한 사용자.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 시도한 섹션.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private QuizCourseSection section;

    /**
     * 획득 점수 (%).
     */
    @Column
    private Integer score = 0;

    /**
     * 맞힌 문제 수.
     */
    @Column(name = "correct_count")
    private Integer correctCount = 0;

    /**
     * 총 문제 수.
     */
    @Column(name = "total_questions")
    private Integer totalQuestions = 0;

    /**
     * 통과 여부.
     */
    @Column(name = "is_passed")
    private Boolean isPassed = false;

    @Builder
    public UserSectionAttempt(User user, QuizCourseSection section, Integer score,
                               Integer correctCount, Integer totalQuestions, Boolean isPassed) {
        this.user = user;
        this.section = section;
        this.score = score;
        this.correctCount = correctCount;
        this.totalQuestions = totalQuestions;
        this.isPassed = isPassed;
    }
}
