package me.exrates.service;

import me.exrates.model.ExOrder;
import me.exrates.model.StopOrder;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.StopOrderSummaryDto;
import me.exrates.model.enums.OrderStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.NavigableSet;

/**
 * Created by maks on 20.04.2017.
 */
public interface StopOrderService {

    Integer createOrder(ExOrder exOrder);

    void proceedStopOrders(int pairId, NavigableSet<StopOrderSummaryDto> orders);

    @Transactional
    void proceedStopOrders(int stopOrderId);

    List<StopOrder> getActiveStopOrdersByCurrencyPairsId(List<Integer> pairIds);

    @Transactional
    boolean cancelOrder(ExOrder exOrder, Locale locale);

    @Transactional
    boolean setStatus(int orderId, OrderStatus status);

    OrderCreateDto getOrderById(Integer orderId, boolean forUpdate);

    void onLimitOrderAccept(ExOrder exOrder);

    @Async
    void onStopOrderCreate(ExOrder exOrder);
}
