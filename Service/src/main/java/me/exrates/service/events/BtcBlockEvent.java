package me.exrates.service.events;

import me.exrates.model.dto.merchants.btc.BtcBlockDto;

public class BtcBlockEvent extends BtcCoreEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public BtcBlockEvent(BtcBlockDto source) {
        super(source);
    }
}
