package com.ssafy.domain.study.entity;

import com.ssafy.domain.study.converter.JsonConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Entity
@Table(name = "study")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Study {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외래 키 (추후 추가)
    @Column(name = "leader_id", nullable = false)
    private Long leaderId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String topic;

    @Column(length = 50)
    private String format;

    @Enumerated(EnumType.STRING)
    @Column(name = "study_type", nullable = false)
    private StudyType studyType;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_type")
    @Builder.Default
    private MeetingType meetingType = MeetingType.ONLINE;

    // 외래 키 (추후 추가)
    @Column(name = "region_id")
    private Long regionId;

    @Column(name = "location_detail", length = 200)
    private String locationDetail;

    @Column(name = "schedule_summary", length = 100)
    private String scheduleSummary;

    @Column(name = "schedule_days", length = 50)
    private String scheduleDays;

    @Column(name = "schedule_time")
    private LocalTime scheduleTime;

    @Column(name = "max_members")
    @Builder.Default
    private Integer maxMembers = 10;

    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_policy")
    @Builder.Default
    private PenaltyPolicy penaltyPolicy = PenaltyPolicy.NORMAL;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "total_sessions")
    private Integer totalSessions;

    @Column(name = "recruit_start_date")
    private LocalDate recruitStartDate;

    @Column(name = "recruit_end_date")
    private LocalDate recruitEndDate;

    @Column(name = "extension_count")
    @Builder.Default
    private Integer extensionCount = 0;

    @Column(length = 500)
    private String textbook;

    @Column(length = 500)
    private String goal;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Difficulty difficulty = Difficulty.INTERMEDIATE;

    @Column(columnDefinition = "TEXT")
    private String prerequisites;

    @Column(name = "process_detail", columnDefinition = "TEXT")
    private String processDetail;

    @Column(name = "target_org_type", length = 50)
    private String targetOrgType;

    @Convert(converter = JsonConverter.class)
    @Column(name = "target_org_criteria", columnDefinition = "JSON")
    private Map<String, Object> targetOrgCriteria;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 비즈니스 로직
    public void updateStatus(Status newStatus) {
        this.status = newStatus;
    }

    public void extendRecruitment(LocalDate newEndDate) {
        if (this.extensionCount >= 1) {
            throw new IllegalStateException("모집 기간은 최대 1회만 연장 가능합니다.");
        }
        this.recruitEndDate = newEndDate;
        this.extensionCount++;
    }
}