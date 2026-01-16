package com.ssafy.domain.meeting.repository;

import com.ssafy.domain.meeting.entity.MeetingSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingSummaryRepository extends JpaRepository<MeetingSummary, Long> {

}
