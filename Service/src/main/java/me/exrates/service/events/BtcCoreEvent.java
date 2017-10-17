package me.exrates.service.events;

import org.springframework.context.ApplicationEvent;

public class BtcCoreEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public BtcCoreEvent(Object source) {
        super(source);
    }
}
