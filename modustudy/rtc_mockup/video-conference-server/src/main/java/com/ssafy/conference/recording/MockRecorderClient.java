package com.ssafy.conference.recording;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MockRecorderClient implements RecorderClient {
  private static final Logger logger = LoggerFactory.getLogger(MockRecorderClient.class);

  private final String recorderBaseUrl;

  public MockRecorderClient(@Value("${app.recorder.base-url:http://localhost:9100}") String recorderBaseUrl) {
    this.recorderBaseUrl = recorderBaseUrl;
  }

  @Override
  public void start(Recording recording) {
    logger.info("Mock recorder start: recordingId={}, roomId={}, baseUrl={}",
        recording.getId(), recording.getRoomId(), recorderBaseUrl);
  }

  @Override
  public void stop(Recording recording) {
    logger.info("Mock recorder stop: recordingId={}, roomId={}, baseUrl={}",
        recording.getId(), recording.getRoomId(), recorderBaseUrl);
  }
}
