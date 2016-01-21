package me.exrates.service;

import me.exrates.dao.OrderDao;
import me.exrates.model.Order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("orderService")
public class OrderServiceImpl implements OrderService{

	@Autowired
	OrderDao orderDao;
	
	@Override
	public boolean createOrder(Order order) {
		return orderDao.createOrder(order);
	}

	
	

	
}
