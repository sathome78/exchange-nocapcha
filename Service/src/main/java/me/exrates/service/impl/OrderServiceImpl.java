package me.exrates.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.exrates.dao.OrderDao;
import me.exrates.dao.UserDao;
import me.exrates.model.Order;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.service.CommissionService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService{

	@Autowired
	OrderDao orderDao;
	
	@Autowired
	UserService userService;
	
	@Autowired
	WalletService walletService;
	
	@Autowired
	CommissionService commissionService;
	
	private static final Locale ru = new Locale("ru");
	 
	@Autowired
	MessageSource messageSource;
	
	@Override
	public int createOrder(Order order) {
		if(walletService.ifEnoughMoney(order.getWalletIdSell(),order.getAmountSell())) {
			return orderDao.createOrder(order);
		}
		else return 0;
	}

	@Transactional(readOnly = true)
	@Override
	public Map<String, List<Order>> getMyOrders(String email) {
		int userId = userService.getIdByEmail(email);
		List<Order> orderList = orderDao.getMyOrders(userId);
		List<Order> sellOrderList = new ArrayList<Order>();
		List<Order> buyOrderList = new ArrayList<Order>();
		for(Order order : orderList) {
			order.setStatusString(getStatusString(order.getStatus()));
			order.setCommission(commissionService.getCommissionByType(order.getOperationType()));
			order.setCurrencySell(walletService.getCurrencyId(order.getWalletIdSell()));
			order.setCurrencySellString(walletService.getCurrencyName(order.getCurrencySell()));
			order.setCurrencyBuyString(walletService.getCurrencyName(order.getCurrencyBuy()));
			if (order.getOperationType().equals(OperationType.SELL)) {
				sellOrderList.add(order);
			}
			else if (order.getOperationType().equals(OperationType.BUY)) {
				buyOrderList.add(order);
			}
		}
		Map<String, List<Order>> orderMap = new HashMap<String, List<Order>>();
		orderMap.put("sell", sellOrderList);
		orderMap.put("buy", buyOrderList);
		return orderMap;
	}
	
	@Transactional(readOnly = true)
	@Override
	public  Map<String, List<Order>>  getAllOrders(){
		List<Order> orderList = orderDao.getAllOrders();
		List<Order> sellOrderList = new ArrayList<Order>();
		List<Order> buyOrderList = new ArrayList<Order>();
		for(Order order : orderList) {
			order.setStatusString(getStatusString(order.getStatus()));
			order.setCommission(commissionService.getCommissionByType(order.getOperationType()));
			order.setCurrencySell(walletService.getCurrencyId(order.getWalletIdSell()));
			order.setCurrencySellString(walletService.getCurrencyName(order.getCurrencySell()));
			order.setCurrencyBuyString(walletService.getCurrencyName(order.getCurrencyBuy()));
			if (order.getOperationType().getType() == OperationType.SELL.getType()) {
				sellOrderList.add(order);
			}
			else if (order.getOperationType().getType() == OperationType.BUY.getType()) {
				buyOrderList.add(order);
			}
		}
		Map<String, List<Order>> orderMap = new HashMap<String, List<Order>>();
		orderMap.put("sell", sellOrderList);
		orderMap.put("buy", buyOrderList);
		return orderMap;
	}
	
	private String getStatusString(OrderStatus status) {
		String statusString = null;
		switch (status) {
		case INPROCESS : statusString= messageSource.getMessage("orderstatus.inprocess", null, ru); break;
		case OPENED : statusString= messageSource.getMessage("orderstatus.opened", null, ru); break;
		case CLOSED : statusString= messageSource.getMessage("orderstatus.closed", null, ru); break;
		}
		return statusString;
	}

	@Transactional
	@Override
	public boolean deleteOrder(int orderId) {
		return orderDao.deleteOrder(orderId);
	}

	@Transactional(readOnly = true)
	public Order getOrderById(int orderId) {
		Order order = orderDao.getOrderById(orderId);
		order.setCurrencySell(walletService.getCurrencyId(order.getWalletIdSell()));
		return order;
	}
	
	@Transactional
	public boolean setStatus(int orderId, OrderStatus status) {
		return orderDao.setStatus(orderId, status);
	}

	@Transactional
	@Override
	public boolean updateOrder(Order order) {
		if(walletService.ifEnoughMoney(order.getWalletIdSell(),order.getAmountSell())) {
			return orderDao.updateOrder(order);
		}
		
		else return false;
	}
	
	
}
