package me.exrates.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.exrates.dao.OrderDao;
import me.exrates.model.Order;
import me.exrates.model.Wallet;
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
	@Transactional
	public int createOrder(Order order) {
		int orderId = 0;
		if(walletService.ifEnoughMoney(order.getWalletIdSell(),order.getAmountSell())) {
			if((orderId=orderDao.createOrder(order)) > 0) {
				walletService.setWalletRBalance(order.getWalletIdSell(), order.getAmountSell());
				walletService.setWalletABalance(order.getWalletIdSell(), -order.getAmountSell());
				setStatus(orderId, OrderStatus.OPENED);
			}
		}
		return orderId;
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
		return orderDao.updateOrder(order);
	}
	
	@Transactional
	@Override
	public boolean acceptOrder(int userId, int orderId) {
		Order order = this.getOrderById(orderId);
		int userWalletId = walletService.getWalletId(userId, order.getCurrencyBuy());
		if(walletService.ifEnoughMoney(userWalletId, order.getAmountBuy())) {
			Double commission = commissionService.getCommissionByType(OperationType.SELL);
			System.out.println("commission = "+commission);
			//for seller
			Double amountForSeller = order.getAmountBuy() - order.getAmountBuy()*commission/100;
			System.out.println("amount that receive seller for ABalance= "+amountForSeller);
			int wallet1ForBuyCurrency = walletService.getWalletId(walletService.getUserIdFromWallet(order.getWalletIdSell()), order.getCurrencyBuy());
			if(wallet1ForBuyCurrency == 0) {
				Wallet wallet = new Wallet();
				wallet.setCurrencyId(order.getCurrencyBuy());
				wallet.setActiveBalance(0);
				wallet.setUserId(walletService.getUserIdFromWallet(order.getWalletIdSell()));
				wallet1ForBuyCurrency = walletService.createNewWallet(wallet);
			}
			walletService.setWalletABalance(wallet1ForBuyCurrency, amountForSeller);
			System.out.println("minus on rbalance = "+order.getAmountSell());
			walletService.setWalletRBalance(order.getWalletIdSell(), -order.getAmountSell());
			//should be companyAccount transaction for commission when I minus reserved balance
			//for buyer
			Double amountForBuyer = order.getAmountSell() - order.getAmountSell()*commission/100;
			System.out.println("amount that receive buyer to abalance = "+amountForBuyer);
			int wallet2ForBuyCurrency = walletService.getWalletId(userId, order.getCurrencySell());
			if(wallet2ForBuyCurrency == 0){
				Wallet wall = new Wallet();
				wall.setActiveBalance(0);
				wall.setUserId(userId);
				wall.setCurrencyId(order.getCurrencySell());
				wallet2ForBuyCurrency = walletService.createNewWallet(wall);
			}
			walletService.setWalletABalance(wallet2ForBuyCurrency, amountForBuyer);
			System.out.println("minus on abalance for buyer = "+order.getAmountBuy());
			walletService.setWalletABalance(walletService.getWalletId(userId, order.getCurrencyBuy()), -order.getAmountBuy());
			//should be companyAccount transaction for commission when I minus abalance
            order.setWalletIdBuy(wallet2ForBuyCurrency);
            order.setStatus(OrderStatus.CLOSED);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String currentTime = format.format(date);
			order.setDateFinal(currentTime);
			updateOrder(order);
			return true;
		}
		else return false;
	}
	
	
	@Transactional
	@Override
	public boolean cancellOrder(int orderId) {
		if(setStatus(orderId, OrderStatus.CANCELLED));
		Order order = getOrderById(orderId);
		walletService.setWalletABalance(order.getWalletIdSell(), order.getAmountSell());
		walletService.setWalletRBalance(order.getWalletIdSell(), -order.getAmountSell());
		return true;
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

}
