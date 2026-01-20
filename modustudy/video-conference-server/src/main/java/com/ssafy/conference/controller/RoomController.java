package com.ssafy.conference.controller;

import com.ssafy.conference.config.SfuProperties;
import com.ssafy.conference.dto.IceServerResponse;
import com.ssafy.conference.dto.ParticipantDto;
import com.ssafy.conference.dto.RoomResponse;
import com.ssafy.conference.dto.SfuConfigResponse;
import com.ssafy.conference.model.Room;
import com.ssafy.conference.repository.RoomRepository;
import com.ssafy.conference.service.RoomService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RoomController {
  private final RoomRepository roomRepository;
  private final RoomService roomService;
  private final SfuProperties sfuProperties;

  public RoomController(RoomRepository roomRepository, RoomService roomService, SfuProperties sfuProperties) {
    this.roomRepository = roomRepository;
    this.roomService = roomService;
    this.sfuProperties = sfuProperties;
  }

  @GetMapping("/rooms/{roomId}")
  public ResponseEntity<RoomResponse> getRoom(@PathVariable String roomId) {
    Room room = roomRepository.findById(roomId).orElse(null);
    if (room == null) {
      return ResponseEntity.notFound().build();
    }
    List<ParticipantDto> participants = roomService.getActiveParticipants(roomId);
    return ResponseEntity.ok(new RoomResponse(room.getId(), room.getTitle(), participants));
  }

  @GetMapping("/sfu/config")
  public SfuConfigResponse getSfuConfig() {
    List<IceServerResponse> iceServers = sfuProperties.getIceServers().stream()
        .filter(server -> server.getUrls() != null && !server.getUrls().isBlank())
        .map(server -> new IceServerResponse(server.getUrls(), server.getUsername(), server.getCredential()))
        .collect(Collectors.toList());
    return new SfuConfigResponse(sfuProperties.getBaseUrl(), iceServers);
  }
}
