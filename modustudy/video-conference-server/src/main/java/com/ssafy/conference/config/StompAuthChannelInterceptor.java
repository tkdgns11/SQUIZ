package com.ssafy.conference.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (accessor == null || accessor.getCommand() != StompCommand.CONNECT) {
      return message;
    }
    String token = resolveToken(accessor);
    if (token == null) {
      return message;
    }
    Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
    if (sessionAttributes == null) {
      sessionAttributes = new HashMap<>();
      accessor.setSessionAttributes(sessionAttributes);
    }
    sessionAttributes.put("authToken", token);
    return message;
  }

  private String resolveToken(StompHeaderAccessor accessor) {
    String token = firstHeader(accessor, "Authorization");
    if (token == null) {
      token = firstHeader(accessor, "authorization");
    }
    if (token == null) {
      token = firstHeader(accessor, "token");
    }
    if (token == null || token.isBlank()) {
      return null;
    }
    if (token.toLowerCase().startsWith("bearer ")) {
      return token.substring(7).trim();
    }
    return token.trim();
  }

  private String firstHeader(StompHeaderAccessor accessor, String name) {
    List<String> values = accessor.getNativeHeader(name);
    if (values == null || values.isEmpty()) {
      return null;
    }
    return values.get(0);
  }
}
