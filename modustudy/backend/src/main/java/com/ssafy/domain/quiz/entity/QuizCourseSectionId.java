package com.ssafy.domain.quiz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable; /**
 * 퀴즈 코스 섹션 복합키.
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class QuizCourseSectionId implements Serializable {

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "section_number")
    private Integer sectionNumber;
}
