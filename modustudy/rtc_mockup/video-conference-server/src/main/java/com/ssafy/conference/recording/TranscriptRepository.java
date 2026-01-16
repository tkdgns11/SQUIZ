package com.ssafy.conference.recording;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TranscriptRepository extends JpaRepository<Transcript, Long> {
  Optional<Transcript> findByRecordingId(Long recordingId);
}
