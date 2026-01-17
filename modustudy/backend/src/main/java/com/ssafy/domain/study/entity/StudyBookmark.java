package com.ssafy.domain.study.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "study_bookmark")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyBookmark extends BaseEntity {
}
