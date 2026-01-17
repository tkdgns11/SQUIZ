package com.ssafy.domain.meeting.repository;

import com.ssafy.domain.meeting.entity.MeetingSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingSummaryRepository extends JpaRepository<MeetingSummary, Long> {
    Optional<MeetingSummary> findByMeetingId(Long meetingId);

    boolean existsByMeetingId(Long meetingId);
}
