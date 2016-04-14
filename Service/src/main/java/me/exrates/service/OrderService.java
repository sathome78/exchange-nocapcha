package me.exrates.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.exrates.model.Order;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.enums.OrderStatus;

public interface OrderService {

	public int createOrder(Order order);
	
	public Map<String, List<Order>> getMyOrders(String email, Locale locale);
	
	public Map<String, List<Order>> getAllOrders(Locale locale);
	
	public boolean deleteOrder(int orderId);
	
	public Order getOrderById(int orderId);
	
	public boolean setStatus(int orderId, OrderStatus status);
	
	public boolean updateOrder(Order order);
	
	public boolean acceptOrder(int userId, int orderId);
	
	public boolean cancellOrder(int orderId);

	List<OrderListDto>  getOrdersSell();

	List<OrderListDto>  getOrdersBuy();

}
