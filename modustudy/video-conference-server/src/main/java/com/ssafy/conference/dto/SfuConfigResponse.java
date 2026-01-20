package com.ssafy.conference.dto;

import java.util.List;

public class SfuConfigResponse {
  private String baseUrl;
  private List<IceServerResponse> iceServers;

  public SfuConfigResponse() {}

  public SfuConfigResponse(String baseUrl, List<IceServerResponse> iceServers) {
    this.baseUrl = baseUrl;
    this.iceServers = iceServers;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public List<IceServerResponse> getIceServers() {
    return iceServers;
  }

  public void setIceServers(List<IceServerResponse> iceServers) {
    this.iceServers = iceServers;
  }
}
