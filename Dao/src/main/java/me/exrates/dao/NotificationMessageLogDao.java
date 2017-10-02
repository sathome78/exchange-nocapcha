package me.exrates.dao;

import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;

import java.math.BigDecimal;

/**
 * Created by Maks on 29.09.2017.
 */
public interface NotificationMessageLogDao {
    void saveLogNotification(String userEmail, BigDecimal payAmount, NotificationMessageEventEnum event, NotificationTypeEnum notificationTypeEnum);
}
