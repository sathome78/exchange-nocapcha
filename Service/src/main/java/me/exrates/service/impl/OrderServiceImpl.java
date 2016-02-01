package me.exrates.service.impl;

import java.util.List;
import java.util.Locale;

import me.exrates.dao.OrderDao;
import me.exrates.dao.UserDao;
import me.exrates.model.Order;

import me.exrates.service.CommissionService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service("orderService")
public class OrderServiceImpl implements OrderService {

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
	public boolean createOrder(Order order) {
		if(walletService.ifEnoughMoney(order.getWalletIdSell(),order.getAmountSell())) {
			return orderDao.createOrder(order);
		}
		else return false;
	}

	@Override
	public List<Order> getMyOrders(String email) {
		int userId = userService.getIdByEmail(email);
		List<Order> orderList = orderDao.getMyOrders(userId);
		for(Order order : orderList) {
			order.setStatusString(getStatusString(order.getStatus()));
			order.setCommission(commissionService.getCommissionByType(order.getOperationType()));
			order.setCurrencySell(walletService.getCurrencyId(order.getWalletIdSell()));
			order.setCurrencySellString(walletService.getCurrencyName(order.getCurrencySell()));
			order.setCurrencyBuyString(walletService.getCurrencyName(order.getCurrencyBuy()));
		}
		return orderList;
	}

	
	private String getStatusString(int status) {
		String statusString = null;
		switch (status) {
		case 1 : statusString= messageSource.getMessage("orderstatus.inprocess", null, ru); break;
		case 2 : statusString= messageSource.getMessage("orderstatus.opened", null, ru); break;
		case 3 : statusString= messageSource.getMessage("orderstatus.closed", null, ru); break;
		}
		return statusString;
	}

	@Override
	public boolean deleteOrder(int orderId) {
		return orderDao.deleteOrder(orderId);
	}

	
}
