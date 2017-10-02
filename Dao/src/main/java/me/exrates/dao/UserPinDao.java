package me.exrates.dao;

import me.exrates.model.enums.NotificationMessageEventEnum;

/**
 * Created by Maks on 02.10.2017.
 */
public interface UserPinDao {
    void createOrUpdatePinByUserEmail(String userEmail, String encode, NotificationMessageEventEnum event);
}
