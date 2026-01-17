package com.ssafy.domain.meeting.repository;

import com.ssafy.domain.meeting.entity.MeetingTranscript;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingTranscriptRepository extends JpaRepository<MeetingTranscript, Long> {
    Page<MeetingTranscript> findByMeetingIdOrderByTimestampSecondsAsc(Long meetingId, Pageable pageable);

    boolean existsByMeetingId(Long meetingId);
}
