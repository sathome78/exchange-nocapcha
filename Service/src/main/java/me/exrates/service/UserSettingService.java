package me.exrates.service;

public interface UserSettingService {
    int addCallbackURL(int userId, String callbackURL);

    String getCallbackURL(int userId);

    int updateCallbackURL(int userId, String callbackURL);

}
