package com.ssafy.domain.news.repository;

import com.ssafy.domain.news.entity.NewsBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsBookmarkRepository extends JpaRepository<NewsBookmark, Long> {

}
