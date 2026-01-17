package com.ssafy.conference.recording.dto;

public class RecordingStartResponse {
  private Long recordingId;
  private String status;

  public RecordingStartResponse() {
  }

  public RecordingStartResponse(Long recordingId, String status) {
    this.recordingId = recordingId;
    this.status = status;
  }

  public Long getRecordingId() {
    return recordingId;
  }

  public void setRecordingId(Long recordingId) {
    this.recordingId = recordingId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
