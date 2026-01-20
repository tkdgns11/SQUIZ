package com.ssafy.common.websocket;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Controller;

@Controller
public class MeetingRoomWebSocketController {
    // Handles room join/chat/presenter events over STOMP.
    private final MeetingRoomStateService roomStateService;
    private final SimpMessagingTemplate messagingTemplate;

    public MeetingRoomWebSocketController(MeetingRoomStateService roomStateService,
                                          SimpMessagingTemplate messagingTemplate) {
        this.roomStateService = roomStateService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/rooms/{roomId}/join")
    public void joinRoom(@DestinationVariable String roomId,
                         @Valid MeetingRoomJoinRequest request,
                         SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        // Track participants in-memory; a database-backed model can be added later.
        MeetingRoomStateService.Participant participant =
                roomStateService.joinRoom(roomId, request.getDisplayName(), sessionId);
        List<MeetingRoomParticipantDto> participants = roomStateService.getActiveParticipants(roomId);

        MeetingRoomEvent event = new MeetingRoomEvent(MeetingRoomEvent.Type.JOIN, roomId);
        event.setParticipant(participant.toDto());
        event.setParticipants(participants);
        MeetingRoomStateService.PresenterInfo presenter = roomStateService.getPresenter(roomId);
        if (presenter != null) {
            event.setPresenterName(presenter.getDisplayName());
            event.setPresenterId(presenter.getParticipantId());
        }
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/events", event);

        List<MeetingRoomChatMessage> history = roomStateService.getChatHistory(roomId);
        if (!history.isEmpty()) {
            MeetingRoomEvent historyEvent = new MeetingRoomEvent(MeetingRoomEvent.Type.CHAT_HISTORY, roomId);
            historyEvent.setChatHistory(history);
            messagingTemplate.convertAndSendToUser(
                    sessionId,
                    "/queue/rooms/" + roomId + "/history",
                    historyEvent,
                    createHeaders(sessionId)
            );
        }
    }

    @MessageMapping("/rooms/{roomId}/chat")
    public void sendChat(@DestinationVariable String roomId, @Valid MeetingRoomChatMessage chatMessage) {
        roomStateService.addChatMessage(roomId, chatMessage);
        MeetingRoomEvent event = new MeetingRoomEvent(MeetingRoomEvent.Type.CHAT, roomId);
        event.setChat(chatMessage);
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/events", event);
    }

    @MessageMapping("/rooms/{roomId}/presenter")
    public void setPresenter(@DestinationVariable String roomId,
                             @Valid MeetingRoomPresenterRequest request,
                             SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        String action = request.getAction() == null ? "claim" : request.getAction();
        MeetingRoomEvent event = new MeetingRoomEvent(MeetingRoomEvent.Type.PRESENTER, roomId);
        if ("release".equalsIgnoreCase(action)) {
            MeetingRoomStateService.PresenterInfo info = roomStateService.releasePresenter(roomId, sessionId);
            if (info != null) {
                event.setPresenterName(info.getDisplayName());
                event.setPresenterId(info.getParticipantId());
            }
        } else {
            Long participantId = roomStateService.getParticipantIdBySession(sessionId);
            MeetingRoomStateService.PresenterInfo info =
                    roomStateService.claimPresenter(roomId, sessionId, request.getDisplayName(), participantId);
            event.setPresenterName(info.getDisplayName());
            event.setPresenterId(info.getParticipantId());
        }
        messagingTemplate.convertAndSend("/topic/rooms/" + roomId + "/events", event);
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        accessor.setSessionId(sessionId);
        accessor.setLeaveMutable(true);
        return accessor.getMessageHeaders();
    }
}
