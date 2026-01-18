package com.ssafy.domain.user.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(name = "profile_image", length = 500)
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_online")
    @Builder.Default
    private Boolean isOnline = false;

    @Column(name = "last_seen_at")
    private java.time.LocalDateTime lastSeenAt;

    @Column(name = "is_searchable")
    @Builder.Default
    private Boolean isSearchable = true;

    @Column(name = "leader_rating")
    @Builder.Default
    private Float leaderRating = 0.0f;

    @Column(name = "leader_review_count")
    @Builder.Default
    private Integer leaderReviewCount = 0;

    @Column(name = "last_login_at")
    private java.time.LocalDateTime lastLoginAt;

    // Enum 정의
    public enum Role {
        USER, ADMIN
    }
}