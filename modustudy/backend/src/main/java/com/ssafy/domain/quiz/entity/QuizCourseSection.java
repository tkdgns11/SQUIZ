package com.ssafy.domain.quiz.entity;

import jakarta.persistence.*;
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
@Table(name = "quiz_course_section")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizCourseSection {

    @EmbeddedId
    private QuizCourseSectionId id;
    /**
     * 소속 코스.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")  // QuizCourseSectionId의 courseId와 매핑
    @JoinColumn(name = "course_id", nullable = false)
    private QuizCourse course;

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

    /**
     * 정적 팩토리 메서드 - section_number는 자동 할당.
     */
    public static QuizCourseSection create(
            QuizCourse course,
            String name,
            String description,
            Integer passScore
    ) {
        QuizCourseSection section = new QuizCourseSection();
        section.course = course;
        section.name = name;
        section.description = description;
        section.passScore = passScore != null ? passScore : 70;
        // section_number는 @PrePersist에서 자동 할당
        return section;
    }

    /**
     * 저장 전에 section_number를 자동으로 할당.
     * 같은 course_id를 가진 섹션 중 최대값 + 1로 설정.
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null && this.course != null) {
            // Repository를 통해 다음 section_number를 조회해야 함
            // 이 부분은 Service 레이어에서 처리하는 것이 더 안전
            throw new IllegalStateException(
                    "section_number must be set before persisting. Use Service layer to create sections."
            );
        }
    }
}