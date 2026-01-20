package com.ssafy.common.websocket;

public class MeetingRoomParticipantDto {
    // Minimal participant payload for WebSocket events.
    private Long id;
    private String displayName;
    private boolean active;

    public MeetingRoomParticipantDto() {
    }

    public MeetingRoomParticipantDto(Long id, String displayName, boolean active) {
        this.id = id;
        this.displayName = displayName;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
