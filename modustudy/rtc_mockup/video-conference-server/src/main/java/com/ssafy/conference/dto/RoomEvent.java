package com.ssafy.conference.dto;

import java.time.Instant;
import java.util.List;

public class RoomEvent {
  public enum Type { JOIN, LEAVE, CHAT, CHAT_HISTORY, PRESENTER, PRESENCE }

  private Type type;
  private String roomId;
  private Instant createdAt = Instant.now();
  private ParticipantDto participant;
  private List<ParticipantDto> participants;
  private ChatMessage chat;
  private List<ChatMessage> chatHistory;
  private String presenterName;
  private Long presenterId;

  public RoomEvent() {}

  public RoomEvent(Type type, String roomId) {
    this.type = type;
    this.roomId = roomId;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public String getRoomId() {
    return roomId;
  }

  public void setRoomId(String roomId) {
    this.roomId = roomId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public ParticipantDto getParticipant() {
    return participant;
  }

  public void setParticipant(ParticipantDto participant) {
    this.participant = participant;
  }

  public List<ParticipantDto> getParticipants() {
    return participants;
  }

  public void setParticipants(List<ParticipantDto> participants) {
    this.participants = participants;
  }

  public ChatMessage getChat() {
    return chat;
  }

  public void setChat(ChatMessage chat) {
    this.chat = chat;
  }

  public List<ChatMessage> getChatHistory() {
    return chatHistory;
  }

  public void setChatHistory(List<ChatMessage> chatHistory) {
    this.chatHistory = chatHistory;
  }

  public String getPresenterName() {
    return presenterName;
  }

  public void setPresenterName(String presenterName) {
    this.presenterName = presenterName;
  }

  public Long getPresenterId() {
    return presenterId;
  }

  public void setPresenterId(Long presenterId) {
    this.presenterId = presenterId;
  }
}
