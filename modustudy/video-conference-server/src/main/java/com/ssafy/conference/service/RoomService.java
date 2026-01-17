package com.ssafy.conference.service;

import com.ssafy.conference.dto.ParticipantDto;
import com.ssafy.conference.model.Participant;
import com.ssafy.conference.model.Room;
import com.ssafy.conference.repository.ParticipantRepository;
import com.ssafy.conference.repository.RoomRepository;
import com.ssafy.conference.dto.ChatMessage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoomService {
  private final RoomRepository roomRepository;
  private final ParticipantRepository participantRepository;
  private final Map<String, ArrayDeque<ChatMessage>> chatHistory = new ConcurrentHashMap<>();
  private final Map<String, PresenterInfo> presenters = new ConcurrentHashMap<>();
  private static final int CHAT_HISTORY_LIMIT = 200;

  public RoomService(RoomRepository roomRepository, ParticipantRepository participantRepository) {
    this.roomRepository = roomRepository;
    this.participantRepository = participantRepository;
  }

  @Transactional
  public Room getOrCreateRoom(String roomId, String title) {
    return roomRepository.findById(roomId).orElseGet(() -> {
      Room room = new Room();
      room.setId(roomId != null ? roomId : UUID.randomUUID().toString());
      room.setTitle(title != null ? title : "Room " + room.getId());
      return roomRepository.save(room);
    });
  }

  @Transactional
  public Participant joinRoom(String roomId, String displayName, String sessionId, String roomTitle) {
    Room room = getOrCreateRoom(roomId, roomTitle);
    Participant participant = new Participant();
    participant.setRoomId(room.getId());
    participant.setDisplayName(displayName);
    participant.setSessionId(sessionId);
    participant.setActive(true);
    return participantRepository.save(participant);
  }

  @Transactional(readOnly = true)
  public List<ParticipantDto> getActiveParticipants(String roomId) {
    return participantRepository.findByRoomIdAndActiveTrue(roomId)
        .stream()
        .map(p -> new ParticipantDto(p.getId(), p.getDisplayName(), p.isActive()))
        .collect(Collectors.toList());
  }

  @Transactional
  public Participant markLeftBySession(String sessionId) {
    return participantRepository.findBySessionId(sessionId).map(p -> {
      p.setActive(false);
      return participantRepository.save(p);
    }).orElse(null);
  }

  public PresenterInfo claimPresenter(String roomId, String sessionId, String displayName) {
    Long participantId = participantRepository.findBySessionId(sessionId)
        .map(Participant::getId)
        .orElse(null);
    PresenterInfo info = new PresenterInfo(sessionId, displayName, participantId);
    presenters.put(roomId, info);
    return info;
  }

  public PresenterInfo releasePresenter(String roomId, String sessionId) {
    PresenterInfo info = presenters.get(roomId);
    if (info != null && info.sessionId.equals(sessionId)) {
      presenters.remove(roomId);
      return null;
    }
    return info;
  }

  public PresenterUpdate clearPresenterBySession(String sessionId) {
    for (Map.Entry<String, PresenterInfo> entry : presenters.entrySet()) {
      PresenterInfo info = entry.getValue();
      if (info.sessionId.equals(sessionId)) {
        presenters.remove(entry.getKey());
        return new PresenterUpdate(entry.getKey(), info.displayName, info.participantId);
      }
    }
    return null;
  }

  public PresenterInfo getPresenter(String roomId) {
    return presenters.get(roomId);
  }

  public static class PresenterUpdate {
    private final String roomId;
    private final String displayName;
    private final Long participantId;

    public PresenterUpdate(String roomId, String displayName, Long participantId) {
      this.roomId = roomId;
      this.displayName = displayName;
      this.participantId = participantId;
    }

    public String getRoomId() {
      return roomId;
    }

    public String getDisplayName() {
      return displayName;
    }

    public Long getParticipantId() {
      return participantId;
    }
  }

  public static class PresenterInfo {
    private final String sessionId;
    private final String displayName;
    private final Long participantId;

    public PresenterInfo(String sessionId, String displayName, Long participantId) {
      this.sessionId = sessionId;
      this.displayName = displayName;
      this.participantId = participantId;
    }

    public String getSessionId() {
      return sessionId;
    }

    public String getDisplayName() {
      return displayName;
    }

    public Long getParticipantId() {
      return participantId;
    }
  }

  public void addChatMessage(String roomId, ChatMessage chatMessage) {
    ArrayDeque<ChatMessage> history = chatHistory.computeIfAbsent(roomId, key -> new ArrayDeque<>());
    synchronized (history) {
      history.addLast(chatMessage);
      while (history.size() > CHAT_HISTORY_LIMIT) {
        history.removeFirst();
      }
    }
  }

  public List<ChatMessage> getChatHistory(String roomId) {
    ArrayDeque<ChatMessage> history = chatHistory.get(roomId);
    if (history == null) {
      return List.of();
    }
    synchronized (history) {
      return new ArrayList<>(history);
    }
  }
}
