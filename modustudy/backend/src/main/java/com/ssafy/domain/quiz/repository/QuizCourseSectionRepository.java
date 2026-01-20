package com.ssafy.domain.quiz.repository;

import com.ssafy.domain.quiz.entity.QuizCourseSection;
import com.ssafy.domain.quiz.entity.QuizCourseSectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 퀴즈 코스 섹션 레포지토리.
 *
 * 호출자: {@link com.ssafy.domain.quiz.service.QuizCourseService}
 */
public interface QuizCourseSectionRepository extends JpaRepository<QuizCourseSection, QuizCourseSectionId> {

    /**
     * 코스 ID와 섹션 번호로 섹션을 조회한다.
     * 코스와 문제 목록도 함께 fetch join으로 조회한다 (N+1 방지).
     *
     * @param quizCourseId 코스 ID
     * @param sectionNumber 섹션 번호
     * @return 섹션 (코스, 문제 포함)
     */
    @Query("SELECT s FROM QuizCourseSection s " +
           "JOIN FETCH s.course " +
           "LEFT JOIN FETCH s.questions " +
           "WHERE s.id.quizCourseId = :quizCourseId AND s.id.sectionNumber = :sectionNumber")
    Optional<QuizCourseSection> findByIdWithCourseAndQuestions(
            @Param("quizCourseId") Long quizCourseId,
            @Param("sectionNumber") Integer sectionNumber);

    /**
     * 코스 ID와 섹션 번호로 섹션을 조회한다.
     * 코스 정보만 fetch join으로 조회한다.
     *
     * @param quizCourseId 코스 ID
     * @param sectionNumber 섹션 번호
     * @return 섹션 (코스 포함)
     */
    @Query("SELECT s FROM QuizCourseSection s " +
           "JOIN FETCH s.course " +
           "WHERE s.id.quizCourseId = :quizCourseId AND s.id.sectionNumber = :sectionNumber")
    Optional<QuizCourseSection> findByIdWithCourse(
            @Param("quizCourseId") Long quizCourseId,
            @Param("sectionNumber") Integer sectionNumber);

    /**
     * 코스 ID와 섹션 번호로 섹션을 조회한다.
     *
     * @param quizCourseId 코스 ID
     * @param sectionNumber 섹션 번호
     * @return 섹션
     */
    @Query("SELECT s FROM QuizCourseSection s " +
           "WHERE s.id.quizCourseId = :quizCourseId AND s.id.sectionNumber = :sectionNumber")
    Optional<QuizCourseSection> findByQuizCourseIdAndSectionNumber(
            @Param("quizCourseId") Long quizCourseId,
            @Param("sectionNumber") Integer sectionNumber);

    /**
     * 특정 코스의 최대 section_number 조회.
     */
    @Query("SELECT MAX(s.id.sectionNumber) FROM QuizCourseSection s WHERE s.id.quizCourseId = :quizCourseId")
    Optional<Integer> findMaxSectionNumberByQuizCourseId(@Param("quizCourseId") Long quizCourseId);

    /**
     * 특정 코스의 모든 섹션 조회.
     */
    @Query("SELECT s FROM QuizCourseSection s WHERE s.id.quizCourseId = :quizCourseId ORDER BY s.id.sectionNumber")
    List<QuizCourseSection> findAllByQuizCourseIdOrderBySectionNumber(@Param("quizCourseId") Long quizCourseId);
}
