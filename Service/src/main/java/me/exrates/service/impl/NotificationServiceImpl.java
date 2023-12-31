package me.exrates.service.impl;

import me.exrates.dao.NotificationDao;
import me.exrates.model.Email;
import me.exrates.model.Notification;
import me.exrates.model.NotificationOption;
import me.exrates.model.User;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.service.NotificationService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Created by OLEG on 10.11.2016.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {


    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private UserService userService;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private MessageSource messageSource;

    // TODO manage notifications in admin page


    /*private long createNotification(Integer userId, String title, String message, NotificationEvent cause) {
        Notification notification = new Notification();
        notification.setReceiverUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setCause(cause);
        return notificationDao.createNotification(notification);
    }*/

    @Override
    public long createLocalizedNotification(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                                            Object[] messageArgs) {
        Locale locale = new Locale(userService.getPreferedLang(userId));
        return 0L /*createNotification(userId, messageSource.getMessage(titleCode, null, locale),
                messageSource.getMessage(messageCode, normalizeArgs(messageArgs), locale), cause)*/;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(String email, NotificationEvent cause, String titleCode, String messageCode,
                           Object[] messageArgs) {
        notifyUser(userService.getIdByEmail(email), cause, titleCode, messageCode, normalizeArgs(messageArgs));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(String email, NotificationEvent cause, String titleCode, String messageCode,
                           Object[] messageArgs, Locale locale) {
        notifyUser(userService.getIdByEmail(email), cause, titleCode, messageCode, normalizeArgs(messageArgs), locale);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                           Object[] messageArgs) {
        notifyUser(userId, cause, titleCode, messageCode, normalizeArgs(messageArgs), Locale.ENGLISH);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                           Object[] messageArgs, Locale locale) {
        String titleMessage = messageSource.getMessage(titleCode, null, locale);
        String message = messageSource.getMessage(messageCode, normalizeArgs(messageArgs), locale);
        notifyUser(userId, cause, titleMessage, message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(Integer userId, NotificationEvent cause, String titleMessage, String message) {
        User user = userService.getUserById(userId);
        Email email = new Email();
        email.setSubject(titleMessage);
        email.setMessage(message);
        email.setTo(user.getEmail());

        Properties properties = new Properties();
        properties.setProperty("public_id", user.getPublicId());
        email.setProperties(properties);

        sendMailService.sendMail(email);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Notification> findAllByUser(String email) {
        return notificationDao.findAllByUser(userService.getIdByEmail(email));
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
        return notificationDao.setReadAllByUser(userService.getIdByEmail(email));
    }

    @Override
    public int removeAllByUser(String email) {
        return notificationDao.removeAllByUser(userService.getIdByEmail(email));

    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationOption> getNotificationOptionsByUser(Integer userId) {
        return notificationDao.getNotificationOptionsByUser(userId);
    }

    private String[] normalizeArgs(Object... args) {
        return Arrays.toString(args).replaceAll("[\\[\\]]", "").split("\\s*,\\s*");
    }

    @Override
    public void updateNotificationOptionsForUser(int userId, List<NotificationOption> options) {
        throw new UnsupportedOperationException();
    }
}
