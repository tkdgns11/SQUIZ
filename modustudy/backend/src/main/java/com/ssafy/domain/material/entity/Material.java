package com.ssafy.domain.material.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "material")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Material extends BaseEntity {
}
