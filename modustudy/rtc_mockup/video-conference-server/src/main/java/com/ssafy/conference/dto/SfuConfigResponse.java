package com.ssafy.conference.dto;

public class SfuConfigResponse {
  private String baseUrl;

  public SfuConfigResponse() {}

  public SfuConfigResponse(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }
}
