package me.exrates.service;

import java.util.List;
<<<<<<< HEAD
import java.util.Map;

import me.exrates.model.Order;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;

public interface OrderService {

	public int createOrder(Order order);
	
	public Map<String, List<Order>> getMyOrders(String email);
	
	public Map<String, List<Order>> getAllOrders();
	
	public boolean deleteOrder(int orderId);
	
	public Order getOrderById(int orderId);
	
	public boolean setStatus(int orderId, OrderStatus status);
	
	public boolean updateOrder(Order order);
=======

import me.exrates.model.Order;

public interface OrderService {

	public boolean createOrder(Order order);
	
	public List<Order> getMyOrders(String email);
	
	public boolean deleteOrder(int orderId);
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1

}
