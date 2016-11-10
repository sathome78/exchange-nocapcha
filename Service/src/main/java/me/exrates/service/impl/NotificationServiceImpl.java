package me.exrates.service.impl;

import me.exrates.dao.NotificationDao;
import me.exrates.dao.UserDao;
import me.exrates.model.Notification;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by OLEG on 10.11.2016.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private UserDao userDao;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createNotification(String receiverEmail, String title, String message, NotificationEvent cause) {
        Notification notification = new Notification();
        notification.setReceiverUserId(userDao.getIdByEmail(receiverEmail));
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setCause(cause);
        return notificationDao.createNotification(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findAllByUser(String email) {
        return notificationDao.findAllByUser(userDao.getIdByEmail(email));
    }

    @Override
    public boolean setRead(List<Long> notificationIds) {
        return notificationDao.setRead(notificationIds);
    }

    @Override
    public int deleteMessages(List<Long> notificationIds) {
        return notificationDao.deleteMessages(notificationIds);
    }



}
