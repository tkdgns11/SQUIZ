package com.ssafy.domain.quiz.repository;

import com.ssafy.domain.quiz.entity.UserSectionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 섹션 시도 기록 레포지토리.
 *
 * 호출자: {@link com.ssafy.domain.quiz.service.QuizCourseService}
 */
public interface UserSectionAttemptRepository extends JpaRepository<UserSectionAttempt, Long> {

    /**
     * 사용자 ID와 섹션 ID로 시도 기록 목록을 조회한다.
     * 최신 시도 순으로 정렬한다.
     *
     * @param userId 사용자 ID
     * @param sectionId 섹션 ID
     * @return 시도 기록 목록
     */
    @Query("SELECT a FROM UserSectionAttempt a " +
           "WHERE a.user.id = :userId AND a.section.id = :sectionId " +
           "ORDER BY a.createdAt DESC")
    List<UserSectionAttempt> findByUserIdAndSectionIdOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("sectionId") Long sectionId);

    /**
     * 사용자 ID와 섹션 ID로 최고 점수 시도 기록을 조회한다.
     *
     * @param userId 사용자 ID
     * @param sectionId 섹션 ID
     * @return 최고 점수 시도 기록
     */
    @Query("SELECT a FROM UserSectionAttempt a " +
           "WHERE a.user.id = :userId AND a.section.id = :sectionId " +
           "ORDER BY a.score DESC, a.createdAt DESC " +
           "LIMIT 1")
    Optional<UserSectionAttempt> findBestAttemptByUserIdAndSectionId(
            @Param("userId") Long userId,
            @Param("sectionId") Long sectionId);

    /**
     * 사용자 ID와 섹션 ID로 시도 횟수를 조회한다.
     *
     * @param userId 사용자 ID
     * @param sectionId 섹션 ID
     * @return 시도 횟수
     */
    @Query("SELECT COUNT(a) FROM UserSectionAttempt a " +
           "WHERE a.user.id = :userId AND a.section.id = :sectionId")
    int countByUserIdAndSectionId(
            @Param("userId") Long userId,
            @Param("sectionId") Long sectionId);

    /**
     * 사용자 ID와 섹션 ID로 통과 여부를 확인한다.
     *
     * @param userId 사용자 ID
     * @param sectionId 섹션 ID
     * @return 통과 여부
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM UserSectionAttempt a " +
           "WHERE a.user.id = :userId AND a.section.id = :sectionId AND a.isPassed = true")
    boolean existsPassedAttemptByUserIdAndSectionId(
            @Param("userId") Long userId,
            @Param("sectionId") Long sectionId);

    /**
     * 사용자 ID와 코스에 속한 섹션들의 시도 기록을 조회한다.
     *
     * @param userId 사용자 ID
     * @param courseId 코스 ID
     * @return 시도 기록 목록
     */
    @Query("SELECT a FROM UserSectionAttempt a " +
           "JOIN a.section s " +
           "WHERE a.user.id = :userId AND s.course.id = :courseId")
    List<UserSectionAttempt> findByUserIdAndCourseId(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId);
}
