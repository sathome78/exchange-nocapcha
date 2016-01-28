package me.exrates.dao;

import me.exrates.model.Order;


public interface OrderDao {
	boolean createOrder(Order order);
}