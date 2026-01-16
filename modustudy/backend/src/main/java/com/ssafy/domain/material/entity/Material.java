package com.ssafy.domain.material.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "material")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Material extends BaseEntity {

}
