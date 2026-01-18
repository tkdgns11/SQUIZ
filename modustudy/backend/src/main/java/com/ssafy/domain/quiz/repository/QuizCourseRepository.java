package com.ssafy.domain.quiz.repository;

import com.ssafy.domain.quiz.entity.QuizCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 퀴즈 코스 조회 전용 레포지토리.
 *
 * 호출자: {@link com.ssafy.domain.quiz.service.QuizCourseService}
 *
 */
public interface QuizCourseRepository extends JpaRepository<QuizCourse, Long> {
    /**
     * 활성 코스를 정렬 기준에 맞춰 조회한다.
     *
     * @return 활성 코스 목록 (sortOrder ASC, id ASC)
     */
    List<QuizCourse> findAllByIsActiveTrueOrderBySortOrderAscIdAsc();

    /**
     * 코스 ID로 코스와 섹션 목록을 함께 조회한다.
     * N+1 문제 방지를 위해 fetch join 사용.
     *
     * @param courseId 코스 ID
     * @return 코스 (섹션 포함)
     */
    @Query("SELECT c FROM QuizCourse c LEFT JOIN FETCH c.sections WHERE c.id = :courseId")
    Optional<QuizCourse> findByIdWithSections(@Param("courseId") Long courseId);
}
