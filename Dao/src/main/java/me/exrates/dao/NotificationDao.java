package me.exrates.dao;

import me.exrates.model.Notification;

/**
 * Created by OLEG on 09.11.2016.
 */
public interface NotificationDao {
    long createNotification(Notification notification);
}
