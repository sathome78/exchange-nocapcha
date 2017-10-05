package me.exrates.service.notifications;

import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Maks on 29.09.2017.
 */
public interface NotificationService {

    @Transactional
    NotificationResultDto notifyUser(String userEmail, String message, String subject, NotificationMessageEventEnum event);
}
