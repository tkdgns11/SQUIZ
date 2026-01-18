package com.ssafy.domain.user.entity;


import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Column(unique = true)
    private String userId;

    private String password;

    private String name;
    private String department;
    private String position;

    // ========== 소셜 로그인용 ==========

    @Column(unique = true, length = 100)
    private String email;

    @Column(unique = true, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;

    @Column(nullable = false)
    private Boolean isActive = true;

    private LocalDateTime lastLoginAt;

    @Column(nullable = false)
    private Boolean isOnline = false;

    private LocalDateTime lastSeenAt;

    @Column(nullable = false)
    private Boolean isSearchable = true;

    @Column(nullable = false)
    private Integer totalExp = 0;

    @Column(nullable = false)
    private Integer currentPoints = 0;

    @Column(nullable = false)
    private Integer currentLevel = 1;

    @Column(length = 50)
    private String levelName = "새싹";
}