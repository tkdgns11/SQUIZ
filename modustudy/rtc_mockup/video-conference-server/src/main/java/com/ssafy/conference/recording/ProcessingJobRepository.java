package com.ssafy.conference.recording;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessingJobRepository extends JpaRepository<ProcessingJob, Long> {
  Optional<ProcessingJob> findByRecordingIdAndJobType(Long recordingId, JobType jobType);
}
