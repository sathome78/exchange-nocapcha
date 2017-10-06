package me.exrates.service.notifications;

import java.math.BigDecimal;

/**
 * Created by Maks on 06.10.2017.
 */
public interface NotificatorsPriceService {

    BigDecimal getFeeForNotificatorAndRole(int code, int role);

}
