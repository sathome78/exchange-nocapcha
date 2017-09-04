package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.ExOrder;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.events.AcceptOrderEvent;
import me.exrates.service.events.CreateOrderEvent;
import me.exrates.service.events.OrderEvent;
import me.exrates.service.stomp.StompMessenger;
import me.exrates.service.vo.OrdersEventsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

/**
 * Created by Maks on 28.08.2017.
 */
@Log4j2
@Component
public class OrdersEventHandleService  {

    @Autowired
    private StompMessenger stompMessenger;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;


    private Map<Integer, OrdersEventsHandler> mapSell = new ConcurrentHashMap<>();
    private Map<Integer, OrdersEventsHandler> mapBuy = new ConcurrentHashMap<>();

    private static final Semaphore SEMAPHORE = new Semaphore(1, true);

    /*@Async*/
    private void onOrdersEvent(Integer pairId, OperationType operationType) {
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
        onOrdersEvent(exOrder.getCurrencyPairId(), exOrder.getOperationType());
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
        /*sendMyTrades((ExOrder)event.getSource());*/
        try {
            sendAllTrades((ExOrder)event.getSource());
        } catch (InterruptedException e) {
            log.error("erro sending all Trades {}", e);
        }
    }

    private void sendAllTrades(ExOrder exOrder) throws InterruptedException {
        if (SEMAPHORE.tryAcquire()) {
            Thread.sleep(1400);
            SEMAPHORE.release();
            String message = orderService.getTradesForRefresh(exOrder.getCurrencyPairId(), null, RefreshObjectsEnum.ALL_TRADES);
            stompMessenger.sendAllTradesToUser(exOrder.getCurrencyPairId(), message);
        }
    }
/*
    private void sendMyTrades(ExOrder exOrder) throws InterruptedException {
        Thread.sleep(1200);
        *//*get orders*//*
        String message = "";
        stompMessenger.sendMyTradesToUser(userService.getEmailById());
    }*/


}
