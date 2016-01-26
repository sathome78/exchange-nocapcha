package me.exrates.service;

import java.util.List;

import me.exrates.model.Order;

public interface OrderService {

	public boolean createOrder(Order order);
	
	public List<Order> getMyOrders(String email);
	
	public boolean deleteOrder(int orderId);

}
