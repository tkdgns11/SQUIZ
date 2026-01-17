package com.ssafy.domain.gamification.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "badge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Badge extends BaseEntity {
    /**
     * 배지 고유 코드.
     * 퀴즈 코스의 badgeCode와 매칭된다.
     */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /**
     * 배지 표시 이름.
     */
    @Column(nullable = false, length = 100)
    private String name;
}
