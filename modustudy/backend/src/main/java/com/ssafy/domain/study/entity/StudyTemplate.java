package com.ssafy.domain.study.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "study_template")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyTemplate extends BaseEntity {
}
