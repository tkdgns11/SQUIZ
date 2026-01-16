package com.ssafy.domain.retrospect.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "retrospective_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RetrospectiveItem extends BaseEntity {

}
