package com.ssafy.domain.study.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "study_template")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyTemplate extends BaseEntity {

}
