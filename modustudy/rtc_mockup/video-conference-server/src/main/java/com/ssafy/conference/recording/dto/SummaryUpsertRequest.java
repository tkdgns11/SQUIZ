package com.ssafy.conference.recording.dto;

public class SummaryUpsertRequest {
  private Long recordingId;
  private String summaryText;
  private String model;

  public Long getRecordingId() {
    return recordingId;
  }

  public void setRecordingId(Long recordingId) {
    this.recordingId = recordingId;
  }

  public String getSummaryText() {
    return summaryText;
  }

  public void setSummaryText(String summaryText) {
    this.summaryText = summaryText;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }
}
