package com.ssafy.conference.dto;

import jakarta.validation.constraints.NotBlank;

public class JoinRoomRequest {
  @NotBlank
  private String displayName;
  private String roomTitle;

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getRoomTitle() {
    return roomTitle;
  }

  public void setRoomTitle(String roomTitle) {
    this.roomTitle = roomTitle;
  }
}
