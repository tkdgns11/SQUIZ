package com.ssafy.conference.dto;

public class IceServerResponse {
  private String urls;
  private String username;
  private String credential;

  public IceServerResponse() {}

  public IceServerResponse(String urls, String username, String credential) {
    this.urls = urls;
    this.username = username;
    this.credential = credential;
  }

  public String getUrls() {
    return urls;
  }

  public void setUrls(String urls) {
    this.urls = urls;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getCredential() {
    return credential;
  }

  public void setCredential(String credential) {
    this.credential = credential;
  }
}
