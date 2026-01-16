package com.ssafy.conference.recording.dto;

public class TranscriptUpsertRequest {
  private Long recordingId;
  private String fullText;
  private String language;
  private String sttProvider;

  public Long getRecordingId() {
    return recordingId;
  }

  public void setRecordingId(Long recordingId) {
    this.recordingId = recordingId;
  }

  public String getFullText() {
    return fullText;
  }

  public void setFullText(String fullText) {
    this.fullText = fullText;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getSttProvider() {
    return sttProvider;
  }

  public void setSttProvider(String sttProvider) {
    this.sttProvider = sttProvider;
  }
}
