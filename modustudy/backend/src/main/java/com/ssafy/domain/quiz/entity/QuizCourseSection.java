package com.ssafy.domain.quiz.entity;

import com.ssafy.common.entity.BaseEntity;
<<<<<<< HEAD
import jakarta.persistence.*;
=======
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
>>>>>>> origin/study
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀴즈 코스 섹션 엔티티.
 *
 * 코스 내 학습 단계를 나타내며, 각 섹션은 여러 문제를 포함한다.
 * 사용자는 이전 섹션을 통과해야 다음 섹션을 해금할 수 있다.
 *
 * DDL 참조: docs/sql/ERD.sql - quiz_course_section
 */
@Entity
@Table(name = "quiz_course_section", uniqueConstraints = {
        @UniqueConstraint(name = "uk_course_section", columnNames = {"course_id", "section_number"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizCourseSection extends BaseEntity {

    /**
     * 소속 코스.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private QuizCourse course;

    /**
     * 섹션 순서 번호 (1부터 시작).
     */
    @Column(name = "section_number", nullable = false)
    private Integer sectionNumber;

    /**
     * 섹션 이름 (예: 기본 문법, 객체지향).
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 섹션 설명.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 섹션 내 총 문제 수.
     */
    @Column(name = "total_questions")
    private Integer totalQuestions = 0;

    /**
     * 통과 점수 (%, 기본값 70).
     */
    @Column(name = "pass_score")
    private Integer passScore = 70;

    /**
     * 섹션에 속한 문제 목록.
     */
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionNumber ASC")
    private List<QuizCourseQuestion> questions = new ArrayList<>();
}
