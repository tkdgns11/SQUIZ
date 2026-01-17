package com.ssafy.domain.quiz.repository;

import com.ssafy.domain.quiz.entity.QuizCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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
}
