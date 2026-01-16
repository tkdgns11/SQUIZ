package com.ssafy.conference.recording;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SummaryRepository extends JpaRepository<Summary, Long> {
  Optional<Summary> findByRecordingId(Long recordingId);
}
