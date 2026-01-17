package com.ssafy.domain.meeting.repository;

import com.ssafy.domain.meeting.entity.MeetingTranscript;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingTranscriptRepository extends JpaRepository<MeetingTranscript, Long> {

}
