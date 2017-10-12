package me.exrates.service.notifications;

import me.exrates.model.dto.NotificationPayEventEnum;
import me.exrates.model.dto.Notificator;

import java.math.BigDecimal;

/**
 * Created by Maks on 06.10.2017.
 */
public interface NotificatorsService {

    Notificator getById(int id);

    BigDecimal getMessagePrice(int notificatorId);

    BigDecimal getFeePrice(int notificatorId, int role, NotificationPayEventEnum payEventEnum);

    BigDecimal getSubscriptionPrice(int notificatorId);

    BigDecimal getLookUpPrice(int code);
}
