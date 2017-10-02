package me.exrates.service.notifications;

import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.enums.NotificationMessageEventEnum;

/**
 * Created by Maks on 29.09.2017.
 */
public interface NotificationService {

    NotificationResultDto notifyUser(String userEmail, String message, NotificationMessageEventEnum event);
}
