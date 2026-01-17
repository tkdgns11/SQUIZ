package com.ssafy.conference.repository;

import com.ssafy.conference.model.Participant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
  List<Participant> findByRoomIdAndActiveTrue(String roomId);
  Optional<Participant> findBySessionId(String sessionId);
}
