package me.exrates.dao;

public interface UserSettingsDao {
    int addCallBackUrl(int userId, String callbackURL);

    String getCallBackURLByUserId(int userId);

    int updateCallbackURL(final int userId, final String callbackURL);

}
