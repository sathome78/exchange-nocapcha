package me.exrates.service.events;

import me.exrates.model.ExOrder;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderEventTypeEnum;
import org.springframework.context.ApplicationEvent;

/**
 * Created by Maks on 30.08.2017.
 */
public class OrderEvent extends ApplicationEvent {


    private OrderEventTypeEnum eventTypeEnum;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public OrderEvent(ExOrder source) {
        super(source);
    }
}
