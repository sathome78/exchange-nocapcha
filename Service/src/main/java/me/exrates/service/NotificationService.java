package me.exrates.service;

import me.exrates.model.Notification;
import me.exrates.model.enums.NotificationEvent;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by OLEG on 10.11.2016.
 */
public interface NotificationService {
    @Transactional(rollbackFor = Exception.class)
    long createNotification(String receiverEmail, String title, String message, NotificationEvent cause);

    @Transactional(readOnly = true)
    List<Notification> findAllByUser(String email);

    boolean setRead(List<Long> notificationIds);

    int deleteMessages(List<Long> notificationIds);
}
