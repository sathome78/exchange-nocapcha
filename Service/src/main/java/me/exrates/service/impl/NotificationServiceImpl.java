package me.exrates.service.impl;

import me.exrates.dao.NotificationDao;
import me.exrates.model.Email;
import me.exrates.model.Notification;
import me.exrates.model.NotificationOption;
import me.exrates.model.User;
import me.exrates.model.dto.onlineTableDto.NotificationDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.vo.CacheData;
import me.exrates.service.NotificationService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.util.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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


    private long createNotification(Integer userId, String title, String message, NotificationEvent cause) {
        Notification notification = new Notification();
        notification.setReceiverUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setCause(cause);
        return notificationDao.createNotification(notification);
    }

    @Override
    public long createLocalizedNotification(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                                            Object[] messageArgs) {
        Locale locale = new Locale(userService.getPreferedLang(userId));
        return createNotification(userId, messageSource.getMessage(titleCode, null, locale),
                messageSource.getMessage(messageCode, messageArgs, locale), cause);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                           Object[] messageArgs) {
        User user = userService.getUserById(userId);
        NotificationOption option = notificationDao.findUserOptionForEvent(userId, cause);
        if (option.isSendNotification()) {
            createLocalizedNotification(userId, cause, titleCode, messageCode, messageArgs);
        }
        //TODO Temporary disabled
        /*if (option.isSendEmail()) {
            Locale locale = new Locale(userService.getPreferedLang(userId));
            Email email = new Email();
            email.setMessage(messageSource.getMessage(messageCode, messageArgs, locale));
            email.setSubject(messageSource.getMessage(titleCode, null, locale));
            email.setTo(user.getEmail());
            sendMailService.sendMail(email);
        }*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(String email, NotificationEvent cause, String titleCode, String messageCode,
                           Object[] messageArgs) {
        notifyUser(userService.getIdByEmail(email), cause, titleCode, messageCode, messageArgs);
    }




    @Override
    @Transactional(readOnly = true)
    public List<Notification> findAllByUser(String email) {
        return notificationDao.findAllByUser(userService.getIdByEmail(email));
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> findByUser(String email, CacheData cacheData, Integer offset, Integer limit) {
        List<NotificationDto> result = notificationDao.findByUser(userService.getIdByEmail(email), offset, limit);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<NotificationDto>() {{
                add(new NotificationDto(false));
            }};
        }
        return result;
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

    @Override
    public void updateUserNotifications(List<NotificationOption> options) {
        notificationDao.updateNotificationOptions(options);
    }



}
