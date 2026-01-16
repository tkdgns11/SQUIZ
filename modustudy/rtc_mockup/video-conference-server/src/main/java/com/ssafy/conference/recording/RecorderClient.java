package com.ssafy.conference.recording;

public interface RecorderClient {
  void start(Recording recording);
  void stop(Recording recording);
}
