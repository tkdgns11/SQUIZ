package com.ssafy.domain.daily.repository;

import com.ssafy.domain.daily.entity.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {
}
