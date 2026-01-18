package com.ssafy.domain.meeting.repository;

import com.ssafy.domain.meeting.entity.MeetingParticipantSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingParticipantSummaryRepository extends JpaRepository<MeetingParticipantSummary, Long> {
    List<MeetingParticipantSummary> findByMeetingId(Long meetingId);

    Optional<MeetingParticipantSummary> findByMeetingIdAndUserId(Long meetingId, Long userId);
}
