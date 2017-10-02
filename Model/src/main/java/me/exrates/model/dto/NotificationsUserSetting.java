package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationPayTypeEnum;

/**
 * Created by Maks on 29.09.2017.
 */
@Data
@Builder
public class NotificationsUserSetting {
    
    private int id;
    private int userId;
    private NotificationMessageEventEnum notificationMessageEventEnum;
    private int notificatorId;

    @Tolerate
    public NotificationsUserSetting() {
    }
}
