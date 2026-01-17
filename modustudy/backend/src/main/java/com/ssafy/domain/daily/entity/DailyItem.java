package com.ssafy.domain.daily.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "daily_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyItem extends BaseEntity {
}
