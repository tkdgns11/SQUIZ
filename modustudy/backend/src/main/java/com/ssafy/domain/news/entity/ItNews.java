package com.ssafy.domain.news.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "it_news")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItNews extends BaseEntity {

}
