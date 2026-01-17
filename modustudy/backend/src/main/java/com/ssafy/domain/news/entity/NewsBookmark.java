package com.ssafy.domain.news.entity;

import com.ssafy.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "news_bookmark")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsBookmark extends BaseEntity {

}
