package me.exrates.dao;

import java.util.List;

import me.exrates.model.Order;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;

public interface OrderDao {

    int createOrder(Order order);

    List<Order> getMyOrders(int userId);

    List<Order> getAllOrders();

    boolean deleteOrder(int orderId);

    Order getOrderById(int orderid);

    boolean setStatus(int orderId, OrderStatus status);

    boolean updateOrder(Order order);

    List<OrderListDto> getOrdersSell();

    List<OrderListDto> getOrdersBuy();


}
