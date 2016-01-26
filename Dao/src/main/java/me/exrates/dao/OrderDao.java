package me.exrates.dao;

import java.util.List;

import me.exrates.model.Order;


public interface OrderDao {

	public boolean createOrder(Order order);
	
	public List<Order> getMyOrders(int userId);
	
	public boolean deleteOrder(int orderId);
	
}
