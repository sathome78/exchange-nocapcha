package me.exrates.service.stomp;

import me.exrates.model.enums.OperationType;

/**
 * Created by Maks on 24.08.2017.
 */
public interface StompMessenger {

    void sendRefreshTradeOrdersMessage(Integer pairId, OperationType operationType);
}
