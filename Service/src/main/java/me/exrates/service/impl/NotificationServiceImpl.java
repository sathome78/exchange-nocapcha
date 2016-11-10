package me.exrates.service.impl;

import me.exrates.dao.NotificationDao;
import me.exrates.dao.UserDao;
import me.exrates.model.Notification;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
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
        return createNotification(userDao.getIdByEmail(receiverEmail), title, message, cause);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createNotification(Integer userId, String title, String message, NotificationEvent cause) {
        Notification notification = new Notification();
        notification.setReceiverUserId(userId);
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
    public boolean setRead(Long notificationId) {
        return notificationDao.setRead(notificationId);
    }

    @Override
    public boolean remove(Long notificationId) {
        return notificationDao.remove(notificationId);
    }

    @Override
    public int setReadAllByUser(String email) {
        return notificationDao.setReadAllByUser(userDao.getIdByEmail(email));
    }

    @Override
    public int removeAllByUser(String email) {
        return notificationDao.removeAllByUser(userDao.getIdByEmail(email));

    }



}
