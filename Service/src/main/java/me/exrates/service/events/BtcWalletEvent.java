package me.exrates.service.events;

import me.exrates.model.dto.merchants.btcTransactionFacade.BtcTransactionDto;

public class BtcWalletEvent extends BtcCoreEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public BtcWalletEvent(BtcTransactionDto source) {
        super(source);
    }
}
