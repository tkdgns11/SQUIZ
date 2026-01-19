package com.ssafy.domain.quiz.entity;

import com.ssafy.domain.user.entity.User;
import jakarta.persistence.*;
import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자별 코스 진행 상황 엔티티.
 *
 * 사용자가 특정 코스를 진행하는 동안의 상태를 추적한다.
 * 복합 키(user_id, course_id)를 사용한다.
 *
 * DDL 참조: docs/sql/ERD.sql - user_course_progress
 */
@Entity
@Table(name = "user_course_progress", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_course", columnNames = {"user_id", "course_id"})
})
@IdClass(UserCourseProgressId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserCourseProgress extends BaseEntity {

    /**
     * 사용자 ID (복합 키).
     */
    @Id
    @Column(name = "user_id")
    private Long userId;

    /**
     * 코스 ID (복합 키).
     */
    @Id
    @Column(name = "course_id")
    private Long courseId;

    /**
     * 사용자 엔티티.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * 코스 엔티티.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private QuizCourse course;

    /**
     * 현재 진행 중인 섹션 번호 (1부터 시작).
     */
    @Column(name = "current_section")
    private Integer currentSection = 1;

    /**
     * 완료한 섹션 수.
     */
    @Column(name = "completed_sections")
    private Integer completedSections = 0;

    /**
     * 코스 완료 여부.
     */
    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    /**
     * 코스 완료 일시.
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * 코스 시작 일시.
     */
    @CreatedDate
    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    /**
     * 마지막 업데이트 일시.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public UserCourseProgress(Long userId, Long courseId) {
        this.userId = userId;
        this.courseId = courseId;
        this.currentSection = 1;
        this.completedSections = 0;
        this.isCompleted = false;
    }

    /**
     * 섹션 통과 시 진행 상황을 업데이트한다.
     */
    public void passSectionAndAdvance(int passedSectionNumber, int totalSections) {
        if (passedSectionNumber == this.currentSection) {
            this.completedSections = passedSectionNumber;
            if (passedSectionNumber < totalSections) {
                this.currentSection = passedSectionNumber + 1;
            } else {
                this.isCompleted = true;
                this.completedAt = LocalDateTime.now();
            }
        }
    }
}
