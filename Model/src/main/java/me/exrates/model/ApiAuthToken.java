package me.exrates.model;

import java.time.LocalDateTime;

/**
 * Created by OLEG on 04.11.2016.
 */
public class ApiAuthToken {
    private Long id;
    private String username;
    private String value;
    private LocalDateTime lastRequest;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getLastRequest() {
        return lastRequest;
    }

    public void seLastRequest(LocalDateTime lastRequest) {
        this.lastRequest = lastRequest;
    }

    @Override
    public String toString() {
        return "ApiAuthToken{" +
                "id=" + id +
                ", username=" + username +
                ", value='" + value + '\'' +
                ", lastRequest=" + lastRequest +
                '}';
    }
}
