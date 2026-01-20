package com.ssafy.conference.repository;

import com.ssafy.conference.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, String> {
}
