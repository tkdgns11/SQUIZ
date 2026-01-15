package com.ssafy.conference.dto;

import java.util.List;

public class RoomResponse {
  private String id;
  private String title;
  private List<ParticipantDto> participants;

  public RoomResponse() {}

  public RoomResponse(String id, String title, List<ParticipantDto> participants) {
    this.id = id;
    this.title = title;
    this.participants = participants;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<ParticipantDto> getParticipants() {
    return participants;
  }

  public void setParticipants(List<ParticipantDto> participants) {
    this.participants = participants;
  }
}
