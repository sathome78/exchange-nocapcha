package me.exrates.service.impl;

import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.ExOrder;
import me.exrates.model.enums.OperationType;
import me.exrates.service.events.AcceptOrderEvent;
import me.exrates.service.events.CreateOrderEvent;
import me.exrates.service.events.OrderEvent;
import me.exrates.service.stopOrder.StopOrderService;
import me.exrates.service.vo.OrdersEventsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Maks on 28.08.2017.
 */
@Log4j2
@Component
public class OrdersEventHandleService  {


    private Map<Integer, OrdersEventsHandler> mapSell = new ConcurrentHashMap<>();
    private Map<Integer, OrdersEventsHandler> mapBuy = new ConcurrentHashMap<>();

    /*@Async*/
    private void onEvent(Integer pairId, OperationType operationType) {
        Map<Integer, OrdersEventsHandler> mapForWork;
        if (operationType.equals(OperationType.BUY)) {
            mapForWork = mapBuy;
        } else if (operationType.equals(OperationType.SELL)) {
            mapForWork = mapSell;
        } else {
            log.error("no such map");
            return;
        }
        OrdersEventsHandler handler = mapForWork
                .computeIfAbsent(pairId, k -> OrdersEventsHandler.init(pairId, operationType));
        handler.onOrderEvent();
    }

    @Async
    @TransactionalEventListener
    void handleOrderEventAsync(OrderEvent event) {
        ExOrder exOrder = (ExOrder) event.getSource();
        onEvent(exOrder.getCurrencyPairId(), exOrder.getOperationType());
    }

    @Async
    @TransactionalEventListener
    void handleOrderEventAsync(CreateOrderEvent event) {
        log.debug("new thr create {} ", Thread.currentThread().getName());
    }

    @Async
    @TransactionalEventListener
    void handleOrderEventAsync(AcceptOrderEvent event) {
        log.debug("new thr accept {} ", Thread.currentThread().getName());
    }


}
