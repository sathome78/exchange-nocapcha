package me.exrates.dao;

import me.exrates.model.dto.NotificationPayEventEnum;

import java.math.BigDecimal;

/**
 * Created by Maks on 09.10.2017.
 */
public interface NotificatorPriceDao {
    BigDecimal getMessagePrice(int notificatorId, int roleId, NotificationPayEventEnum payEventEnum);
}
