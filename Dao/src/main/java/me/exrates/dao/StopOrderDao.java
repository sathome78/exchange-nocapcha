package me.exrates.dao;

import me.exrates.model.StopOrder;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.enums.OrderStatus;

import java.util.List;

/**
 * Created by maks on 20.04.2017.
 */
public interface StopOrderDao {
    boolean setStatus(int orderId, OrderStatus status);

    Integer create(StopOrder order);

    boolean setStatusAndChildOrderId(int orderId, int childOrderId, OrderStatus status);

    List<StopOrder> getOrdersBypairId(List<Integer> pairIds, OrderStatus opened);

    OrderCreateDto getOrderById(Integer orderId, boolean forUpdate);
}
