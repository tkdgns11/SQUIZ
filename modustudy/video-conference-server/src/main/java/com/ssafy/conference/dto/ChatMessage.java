package com.ssafy.conference.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

public class ChatMessage {
  @NotBlank
  private String sender;
  @NotBlank
  private String text;
  private Instant sentAt = Instant.now();

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Instant getSentAt() {
    return sentAt;
  }

  public void setSentAt(Instant sentAt) {
    this.sentAt = sentAt;
  }
}
