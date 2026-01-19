package com.ssafy.domain.quiz.repository;

import com.ssafy.domain.quiz.entity.QuizCourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * 퀴즈 코스 섹션 레포지토리.
 *
 * 호출자: {@link com.ssafy.domain.quiz.service.QuizCourseService}
 */
public interface QuizCourseSectionRepository extends JpaRepository<QuizCourseSection, Long> {

    /**
     * 섹션 ID와 코스 ID로 섹션을 조회한다.
     * 문제 목록도 함께 fetch join으로 조회한다.
     *
     * @param sectionId 섹션 ID
     * @param courseId 코스 ID
     * @return 섹션 (문제 포함)
     */
    @Query("SELECT s FROM QuizCourseSection s " +
           "LEFT JOIN FETCH s.questions " +
           "WHERE s.id = :sectionId AND s.course.id = :courseId")
    Optional<QuizCourseSection> findByIdAndCourseIdWithQuestions(
            @Param("sectionId") Long sectionId,
            @Param("courseId") Long courseId);

    /**
     * 코스 ID와 섹션 번호로 섹션을 조회한다.
     *
     * @param courseId 코스 ID
     * @param sectionNumber 섹션 번호
     * @return 섹션
     */
    @Query("SELECT s FROM QuizCourseSection s " +
           "WHERE s.course.id = :courseId AND s.sectionNumber = :sectionNumber")
    Optional<QuizCourseSection> findByCourseIdAndSectionNumber(
            @Param("courseId") Long courseId,
            @Param("sectionNumber") Integer sectionNumber);
}
