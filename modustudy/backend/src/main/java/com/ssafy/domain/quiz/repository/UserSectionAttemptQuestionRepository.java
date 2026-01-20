package com.ssafy.domain.quiz.repository;

import com.ssafy.domain.quiz.entity.UserSectionAttemptQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 섹션 시도 문제 레포지토리.
 */
public interface UserSectionAttemptQuestionRepository extends JpaRepository<UserSectionAttemptQuestion, Long> {

    /**
     * 시도 ID로 문제 목록을 order_index 순서로 조회한다.
     * 문제 정보도 함께 fetch join으로 조회한다.
     *
     * @param attemptId 시도 ID
     * @return 문제 목록 (순서대로)
     */
    @Query("SELECT aq FROM UserSectionAttemptQuestion aq " +
           "JOIN FETCH aq.question q " +
           "WHERE aq.attempt.id = :attemptId " +
           "ORDER BY aq.orderIndex ASC")
    List<UserSectionAttemptQuestion> findByAttemptIdWithQuestionOrderByOrderIndex(
            @Param("attemptId") Long attemptId);

    /**
     * 시도 ID와 문제 ID로 특정 문제를 조회한다.
     *
     * @param attemptId 시도 ID
     * @param questionId 문제 ID
     * @return 시도 문제
     */
    @Query("SELECT aq FROM UserSectionAttemptQuestion aq " +
           "WHERE aq.attempt.id = :attemptId AND aq.question.id = :questionId")
    Optional<UserSectionAttemptQuestion> findByAttemptIdAndQuestionId(
            @Param("attemptId") Long attemptId,
            @Param("questionId") Long questionId);

    /**
     * 시도 ID와 order_index로 특정 문제를 조회한다.
     *
     * @param attemptId 시도 ID
     * @param orderIndex 문제 순서
     * @return 시도 문제
     */
    @Query("SELECT aq FROM UserSectionAttemptQuestion aq " +
           "JOIN FETCH aq.question " +
           "WHERE aq.attempt.id = :attemptId AND aq.orderIndex = :orderIndex")
    Optional<UserSectionAttemptQuestion> findByAttemptIdAndOrderIndex(
            @Param("attemptId") Long attemptId,
            @Param("orderIndex") Integer orderIndex);

    /**
     * 시도의 정답 개수를 조회한다.
     *
     * @param attemptId 시도 ID
     * @return 정답 개수
     */
    @Query("SELECT COUNT(aq) FROM UserSectionAttemptQuestion aq " +
           "WHERE aq.attempt.id = :attemptId AND aq.isCorrect = true")
    int countCorrectByAttemptId(@Param("attemptId") Long attemptId);

    /**
     * 시도의 답변 완료 개수를 조회한다.
     *
     * @param attemptId 시도 ID
     * @return 답변 완료 개수
     */
    @Query("SELECT COUNT(aq) FROM UserSectionAttemptQuestion aq " +
           "WHERE aq.attempt.id = :attemptId AND aq.userAnswer IS NOT NULL")
    int countAnsweredByAttemptId(@Param("attemptId") Long attemptId);

    /**
     * 시도의 모든 문제에 대해 일괄 채점을 수행한다.
     * 사용자 답안과 정답을 비교하여 is_correct를 업데이트한다.
     *
     * @param attemptId 시도 ID
     * @return 업데이트된 행 수
     */
    @Modifying
    @Query("UPDATE UserSectionAttemptQuestion aq " +
           "SET aq.isCorrect = CASE " +
           "  WHEN aq.userAnswer IS NULL THEN false " +
           "  WHEN LOWER(TRIM(aq.userAnswer)) = LOWER(TRIM(aq.question.correctAnswer)) THEN true " +
           "  ELSE false " +
           "END " +
           "WHERE aq.attempt.id = :attemptId")
    int gradeAllByAttemptId(@Param("attemptId") Long attemptId);
}
