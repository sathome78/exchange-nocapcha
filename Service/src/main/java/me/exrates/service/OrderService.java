package me.exrates.service;

import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.dto.OrderWideListDto;
import me.exrates.model.enums.OrderStatus;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface OrderService {

    int createOrder(int userId, OrderCreateDto order);

    Map<String, List<OrderWideListDto>> getMyOrders(String email, CurrencyPair currencyPair, Locale locale);

    Map<String, List<OrderWideListDto>> getAllOpenedOrders(Locale locale);

    ExOrder getOrderById(int orderId);

    boolean setStatus(int orderId, OrderStatus status);

    void acceptOrder(int userId, int orderId);

    boolean cancellOrder(ExOrder exOrder);

    List<OrderListDto> getOrdersSell(CurrencyPair currencyPair);

    List<OrderListDto> getOrdersBuy(CurrencyPair currencyPair);

    boolean updateOrder(ExOrder exOrder);

}
