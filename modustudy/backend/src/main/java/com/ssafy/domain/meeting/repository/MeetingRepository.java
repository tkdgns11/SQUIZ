package com.ssafy.domain.meeting.repository;

import com.ssafy.domain.meeting.entity.Meeting;
import com.ssafy.domain.meeting.entity.MeetingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Page<Meeting> findByStudyId(Long studyId, Pageable pageable);

    Optional<Meeting> findByIdAndStudyId(Long meetingId, Long studyId);

    boolean existsByStudyIdAndStatus(Long studyId, MeetingStatus status);
}
