package com.ssafy.domain.study.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_template")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StudyTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "is_system")
    @Builder.Default
    private boolean isSystem = false;

    @Column(name = "template_type", length = 50)
    private String templateType;

    @Column(length = 50)
    private String topic;

    @Column(length = 50)
    private String format;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type")
    private MeetingType meetingType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String textbook;

    @Column(length = 500)
    private String goal;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    @Column(columnDefinition = "TEXT")
    private String prerequisites;

    @Column(columnDefinition = "TEXT", name = "process_Detail")
    private String processDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_policy")
    private PenaltyPolicy penaltyPolicy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
