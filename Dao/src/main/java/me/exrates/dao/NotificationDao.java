package me.exrates.dao;

import me.exrates.model.Notification;

import java.util.List;

/**
 * Created by OLEG on 09.11.2016.
 */
public interface NotificationDao {
    long createNotification(Notification notification);

    List<Notification> findAllByUser(Integer userId);

    boolean setRead(Long notificationId);

    boolean remove(Long notificationId);

    int setReadAllByUser(Integer userId);

    int removeAllByUser(Integer userId);
}
