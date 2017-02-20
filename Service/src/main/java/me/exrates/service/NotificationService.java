package me.exrates.service;

import me.exrates.model.Notification;
import me.exrates.model.NotificationOption;
import me.exrates.model.dto.onlineTableDto.NotificationDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.vo.CacheData;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by OLEG on 10.11.2016.
 */
public interface NotificationService {


    long createLocalizedNotification(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                                     Object[] messageArgs);

    long createLocalizedNotification(String userEmail, NotificationEvent cause, String titleCode, String messageCode,
                                     Object[] messageArgs);

    @Transactional(rollbackFor = Exception.class)
    void notifyUser(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                    Object[] messageArgs);

    @Transactional(rollbackFor = Exception.class)
    void notifyUser(String email, NotificationEvent cause, String titleCode, String messageCode,
                    Object[] messageArgs);

    @Transactional(readOnly = true)
    List<Notification> findAllByUser(String email);

    @Transactional(readOnly = true)
    List<NotificationDto> findByUser(String email, CacheData cacheData, Integer offset, Integer limit);

    boolean setRead(Long notificationId);

    boolean remove(Long notificationId);

    @Transactional(readOnly = true)
    int setReadAllByUser(String email);

    @Transactional(readOnly = true)
    int removeAllByUser(String email);

    @Transactional(readOnly = true)
    List<NotificationOption> getNotificationOptionsByUser(Integer userId);

    void updateUserNotifications(List<NotificationOption> options);
}
