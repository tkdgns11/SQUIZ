package com.ssafy.conference.config;

import com.ssafy.conference.dto.ParticipantDto;
import com.ssafy.conference.dto.RoomEvent;
import com.ssafy.conference.service.RoomService;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketDisconnectListener {
  private final RoomService roomService;
  private final SimpMessagingTemplate messagingTemplate;

  public WebSocketDisconnectListener(RoomService roomService, SimpMessagingTemplate messagingTemplate) {
    this.roomService = roomService;
    this.messagingTemplate = messagingTemplate;
  }

  @EventListener
  public void handleSessionDisconnect(SessionDisconnectEvent event) {
    String sessionId = event.getSessionId();
    var participant = roomService.markLeftBySession(sessionId);
    if (participant == null) {
      return;
    }

    String roomId = participant.getRoomId();
    RoomService.PresenterUpdate presenterUpdate = roomService.clearPresenterBySession(sessionId);
    List<ParticipantDto> participants = roomService.getActiveParticipants(roomId);
    RoomEvent leaveEvent = new RoomEvent(RoomEvent.Type.LEAVE, roomId);
    leaveEvent.setParticipant(new ParticipantDto(participant.getId(), participant.getDisplayName(), participant.isActive()));
    leaveEvent.setParticipants(participants);
    messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/events", leaveEvent);

    if (presenterUpdate != null && roomId.equals(presenterUpdate.getRoomId())) {
      RoomEvent presenterEvent = new RoomEvent(RoomEvent.Type.PRESENTER, roomId);
      presenterEvent.setPresenterName(null);
      presenterEvent.setPresenterId(null);
      messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/events", presenterEvent);
    }
  }
}
