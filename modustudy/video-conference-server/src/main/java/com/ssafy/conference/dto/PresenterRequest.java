package com.ssafy.conference.dto;

import jakarta.validation.constraints.NotBlank;

public class PresenterRequest {
  @NotBlank
  private String displayName;
  private String action;

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
