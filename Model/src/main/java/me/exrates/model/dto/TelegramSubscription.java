package me.exrates.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import me.exrates.model.enums.TelegramSubscriptionStateEnum;
import org.springframework.messaging.simp.stomp.StompSession;

/**
 * Created by Maks on 05.10.2017.
 */
@Data
@Builder
public class TelegramSubscription implements Subscription {


    private int id;
    private int userId;
    private String code;
    private TelegramSubscriptionStateEnum subscriptionState;
    private String userAccount;
    private Long chatId;
    private String rawText;

    @Tolerate
    public TelegramSubscription() {
    }
}
