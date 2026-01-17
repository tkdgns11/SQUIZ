package com.ssafy.domain.daily.repository;

import com.ssafy.domain.daily.entity.DailyItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyItemRepository extends JpaRepository<DailyItem, Long> {

}
