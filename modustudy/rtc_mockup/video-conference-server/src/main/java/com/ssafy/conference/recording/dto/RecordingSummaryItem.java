package com.ssafy.conference.recording.dto;

import java.time.Instant;

public class RecordingSummaryItem {
  private Long recordingId;
  private String status;
  private Integer durationSec;
  private Instant startedAt;
  private Instant endedAt;

  public RecordingSummaryItem() {
  }

  public RecordingSummaryItem(Long recordingId, String status, Integer durationSec, Instant startedAt, Instant endedAt) {
    this.recordingId = recordingId;
    this.status = status;
    this.durationSec = durationSec;
    this.startedAt = startedAt;
    this.endedAt = endedAt;
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

  public Integer getDurationSec() {
    return durationSec;
  }

  public void setDurationSec(Integer durationSec) {
    this.durationSec = durationSec;
  }

  public Instant getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(Instant startedAt) {
    this.startedAt = startedAt;
  }

  public Instant getEndedAt() {
    return endedAt;
  }

  public void setEndedAt(Instant endedAt) {
    this.endedAt = endedAt;
  }
}
