package me.exrates.model.dto;

/**
 * Created by OLEG on 30.08.2016.
 */
public class SessionInfoDto {
    private String sessionId;
    private String username;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "SessionInfoDto{" +
                "sessionId='" + sessionId + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
