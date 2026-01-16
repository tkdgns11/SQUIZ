package com.ssafy.domain.daily.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "daily_report")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyReport extends BaseEntity {

}
