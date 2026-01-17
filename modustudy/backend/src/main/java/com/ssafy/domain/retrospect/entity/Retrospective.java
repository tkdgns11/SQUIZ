package com.ssafy.domain.retrospect.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "retrospective")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Retrospective extends BaseEntity {
}
