package me.exrates.dao;

import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationMessageEventEnum;

import java.util.Optional;

/**
 * Created by Maks on 29.09.2017.
 */
public interface NotificationUserSettingsDao {

    Optional<NotificationsUserSetting> getByUserAndEvent(int userId, NotificationMessageEventEnum event);

}
