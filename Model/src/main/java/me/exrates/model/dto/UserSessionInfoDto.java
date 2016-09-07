package me.exrates.model.dto;

import me.exrates.model.enums.UserRole;

import java.util.List;

/**
 * Created by OLEG on 06.09.2016.
 */
public class UserSessionInfoDto {

    private int userId;
    private List<String> sessionIds;
    private String userNickname;
    private String userEmail;
    private UserRole userRole;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<String> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds(List<String> sessionIds) {
        this.sessionIds = sessionIds;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "UserSessionInfoDto{" +
                "userId=" + userId +
                ", sessionIds=" + sessionIds +
                ", userNickname='" + userNickname + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userRole=" + userRole +
                '}';
    }
}
