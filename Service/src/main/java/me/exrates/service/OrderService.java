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

    boolean deleteOrder(int orderId);

    ExOrder getOrderById(int orderId);

    boolean setStatus(int orderId, OrderStatus status);

    void acceptOrder(int userId, int orderId);

    boolean cancellOrder(int orderId);

    List<OrderListDto> getOrdersSell();

    List<OrderListDto> getOrdersBuy();

    boolean updateOrder(ExOrder exOrder);

}
