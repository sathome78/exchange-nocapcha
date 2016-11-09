package me.exrates.model;

import me.exrates.model.enums.NotificationEvent;

import java.time.LocalDateTime;

/**
 * Created by OLEG on 09.11.2016.
 */
public class Notification {
    private Long id;
    private Integer receiverUserId;
    private String title;
    private String message;
    private LocalDateTime creationTime;
    private NotificationEvent cause;
    private Boolean isRead;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Integer receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public NotificationEvent getCause() {
        return cause;
    }

    public void setCause(NotificationEvent cause) {
        this.cause = cause;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
