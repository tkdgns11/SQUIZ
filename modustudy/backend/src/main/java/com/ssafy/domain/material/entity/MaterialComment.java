package com.ssafy.domain.material.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "material_comment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MaterialComment extends BaseEntity {
}
