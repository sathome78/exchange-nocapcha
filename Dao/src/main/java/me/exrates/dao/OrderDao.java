package me.exrates.dao;

import java.util.List;

import me.exrates.model.Order;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;

public interface OrderDao {

	public int createOrder(Order order);
	
	public List<Order> getMyOrders(int userId);
	
	public List<Order> getAllOrders();
	
	public boolean deleteOrder(int orderId);
	
	public Order getOrderById(int orderid);
	
	public boolean setStatus(int orderId, OrderStatus status);
	
	public boolean updateOrder(Order order);
	
	
		
}
