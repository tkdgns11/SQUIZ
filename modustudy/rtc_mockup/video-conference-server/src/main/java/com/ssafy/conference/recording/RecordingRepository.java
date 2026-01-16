package com.ssafy.conference.recording;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordingRepository extends JpaRepository<Recording, Long> {
  List<Recording> findByRoomIdOrderByCreatedAtDesc(String roomId);
}
