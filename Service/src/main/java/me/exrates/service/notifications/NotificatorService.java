package me.exrates.service.notifications;

import me.exrates.model.enums.NotificationPayTypeEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.service.exception.MessageUndeliweredException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by Maks on 29.09.2017.
 */
public interface NotificatorService {


    String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException;

    BigDecimal getMessageCost();

    NotificationPayTypeEnum getPayType();

    NotificationTypeEnum getNotificationType();

    /*default BigDecimal getTotalMessageCost(UserRole userRole) {
        return getMessageCost().add(getCommissionCost(userRole));
    }*/
}
