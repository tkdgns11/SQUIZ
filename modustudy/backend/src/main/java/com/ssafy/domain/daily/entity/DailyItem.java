package com.ssafy.domain.daily.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyItem extends BaseEntity {

}
