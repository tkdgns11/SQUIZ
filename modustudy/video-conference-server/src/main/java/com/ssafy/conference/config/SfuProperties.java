package com.ssafy.conference.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app.sfu")
public class SfuProperties {
  private String baseUrl;
  private List<IceServer> iceServers = new ArrayList<>();

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public List<IceServer> getIceServers() {
    return iceServers;
  }

  public void setIceServers(List<IceServer> iceServers) {
    this.iceServers = iceServers;
  }

  public static class IceServer {
    private String urls;
    private String username;
    private String credential;

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
}
