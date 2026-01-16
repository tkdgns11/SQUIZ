package com.ssafy.conference.recording.dto;

public class RecordingStopResponse {
  private String status;

  public RecordingStopResponse() {
  }

  public RecordingStopResponse(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
