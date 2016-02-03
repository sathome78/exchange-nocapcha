package me.exrates.dao;

import java.util.List;

import me.exrates.model.Order;
<<<<<<< HEAD
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
=======
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1


public interface OrderDao {

<<<<<<< HEAD
	public int createOrder(Order order);
	
	public List<Order> getMyOrders(int userId);
	
	public List<Order> getAllOrders();
	
	public boolean deleteOrder(int orderId);
	
	public Order getOrderById(int orderid);
	
	public boolean setStatus(int orderId, OrderStatus status);
	
	public boolean updateOrder(Order order);
	
=======
	public boolean createOrder(Order order);
	
	public List<Order> getMyOrders(int userId);
	
	public boolean deleteOrder(int orderId);
	
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
}
