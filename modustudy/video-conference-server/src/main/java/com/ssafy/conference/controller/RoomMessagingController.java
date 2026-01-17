package com.ssafy.conference.controller;

import com.ssafy.conference.dto.ChatMessage;
import com.ssafy.conference.dto.JoinRoomRequest;
import com.ssafy.conference.dto.ParticipantDto;
import com.ssafy.conference.dto.PresenterRequest;
import com.ssafy.conference.dto.RoomEvent;
import com.ssafy.conference.model.Participant;
import com.ssafy.conference.service.RoomService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Controller;

@Controller
public class RoomMessagingController {
  private final RoomService roomService;
  private final SimpMessagingTemplate messagingTemplate;

  public RoomMessagingController(RoomService roomService, SimpMessagingTemplate messagingTemplate) {
    this.roomService = roomService;
    this.messagingTemplate = messagingTemplate;
  }

  @MessageMapping("/rooms/{roomId}/join")
  public void joinRoom(@DestinationVariable String roomId,
                       @Valid JoinRoomRequest request,
                       SimpMessageHeaderAccessor headerAccessor,
                       Principal principal) {
    String sessionId = headerAccessor.getSessionId();
    System.out.println("[JOIN] room=" + roomId + " name=" + request.getDisplayName() + " session=" + sessionId);
    Participant participant = roomService.joinRoom(roomId, request.getDisplayName(), sessionId, request.getRoomTitle());
    List<ParticipantDto> participants = roomService.getActiveParticipants(roomId);

    RoomEvent event = new RoomEvent(RoomEvent.Type.JOIN, roomId);
    event.setParticipant(new ParticipantDto(participant.getId(), participant.getDisplayName(), participant.isActive()));
    event.setParticipants(participants);
    RoomService.PresenterInfo presenter = roomService.getPresenter(roomId);
    if (presenter != null) {
      event.setPresenterName(presenter.getDisplayName());
      event.setPresenterId(presenter.getParticipantId());
    }
    messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/events", event);

    List<ChatMessage> chatHistory = roomService.getChatHistory(roomId);
    if (!chatHistory.isEmpty()) {
      RoomEvent historyEvent = new RoomEvent(RoomEvent.Type.CHAT_HISTORY, roomId);
      historyEvent.setChatHistory(chatHistory);
      messagingTemplate.convertAndSendToUser(
          sessionId,
          "/queue/rooms/" + roomId + "/history",
          historyEvent,
          createHeaders(sessionId)
      );
    }
  }

  @MessageMapping("/rooms/{roomId}/chat")
  public void sendChat(@DestinationVariable String roomId, @Valid ChatMessage chatMessage) {
    System.out.println("[CHAT] room=" + roomId + " sender=" + chatMessage.getSender());
    roomService.addChatMessage(roomId, chatMessage);
    RoomEvent event = new RoomEvent(RoomEvent.Type.CHAT, roomId);
    event.setChat(chatMessage);
    messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/events", event);
  }

  @MessageMapping("/rooms/{roomId}/presenter")
  public void setPresenter(@DestinationVariable String roomId,
                           @Valid PresenterRequest request,
                           SimpMessageHeaderAccessor headerAccessor) {
    String sessionId = headerAccessor.getSessionId();
    String action = request.getAction() == null ? "claim" : request.getAction();
    RoomEvent event = new RoomEvent(RoomEvent.Type.PRESENTER, roomId);
    if ("release".equalsIgnoreCase(action)) {
      RoomService.PresenterInfo info = roomService.releasePresenter(roomId, sessionId);
      if (info != null) {
        event.setPresenterName(info.getDisplayName());
        event.setPresenterId(info.getParticipantId());
      }
    } else {
      RoomService.PresenterInfo info = roomService.claimPresenter(roomId, sessionId, request.getDisplayName());
      event.setPresenterName(info.getDisplayName());
      event.setPresenterId(info.getParticipantId());
    }
    System.out.println("[PRESENTER] room=" + roomId + " presenter=" + event.getPresenterName() + " action=" + action);
    messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/events", event);
  }

  private MessageHeaders createHeaders(String sessionId) {
    SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
    accessor.setSessionId(sessionId);
    accessor.setLeaveMutable(true);
    return accessor.getMessageHeaders();
  }
}
