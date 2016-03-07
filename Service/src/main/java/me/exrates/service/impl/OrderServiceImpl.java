package me.exrates.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.exrates.dao.OrderDao;
import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;
import me.exrates.model.Order;
import me.exrates.model.Wallet;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.service.CommissionService;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
	CompanyWalletService companyWalletService;
	
	@Autowired
	CommissionService commissionService;
	
	private static final Locale ru = new Locale("ru");
	
	private static final Logger logger = LogManager.getLogger(OrderServiceImpl.class);
	 
	@Autowired
	MessageSource messageSource;
	
	@Override
	@Transactional
	public int createOrder(Order order) {
		int orderId = 0;
		if(walletService.ifEnoughMoney(order.getWalletIdSell(),order.getAmountSell())) {
			BigDecimal commission = commissionService.findCommissionByType(OperationType.SELL).getValue().divide(BigDecimal.valueOf(100));
			BigDecimal commissionAmountSell = order.getAmountSell().multiply(commission);
			BigDecimal commissionAmountBuy = order.getAmountBuy().multiply(commission);
			order.setCommissionAmountBuy(commissionAmountBuy);
			order.setCommissionAmountSell(commissionAmountSell);
			if((orderId=orderDao.createOrder(order)) > 0) {
				walletService.setWalletRBalance(order.getWalletIdSell(), order.getAmountSell());
				walletService.setWalletABalance(order.getWalletIdSell(), order.getAmountSell().negate());
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
			order.setCommission(commissionService.findCommissionByType(order.getOperationType()).getValue());
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
			order.setCommission(commissionService.findCommissionByType(order.getOperationType()).getValue());
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
		order.setCurrencySellString(walletService.getCurrencyName(order.getCurrencySell()));
		order.setCurrencyBuyString(walletService.getCurrencyName(order.getCurrencyBuy()));
		return order;
	}
	
	@Transactional(propagation = Propagation.NESTED)
	public boolean setStatus(int orderId, OrderStatus status) {
		return orderDao.setStatus(orderId, status);
	}

	@Transactional(propagation = Propagation.NESTED)
	@Override
	public boolean updateOrder(Order order) {
		return orderDao.updateOrder(order);
	}
	
	@Transactional(rollbackFor={Throwable.class}, propagation=Propagation.NESTED)
	@Override
	public boolean acceptOrder(int userId, int orderId) {
		Boolean flag = false;
		Order order = this.getOrderById(orderId);
		int userWalletId = walletService.getWalletId(userId, order.getCurrencyBuy());
		try {
			if(walletService.ifEnoughMoney(userWalletId, order.getAmountBuy())) {
				
				//for seller
				BigDecimal amountForSeller = order.getAmountBuy().subtract(order.getCommissionAmountBuy());
				int wallet1ForBuyCurrency = walletService.getWalletId(walletService.getUserIdFromWallet(order.getWalletIdSell()), order.getCurrencyBuy());
				if(wallet1ForBuyCurrency == 0) {
					Wallet wallet = new Wallet();
					wallet.setCurrencyId(order.getCurrencyBuy());
					wallet.setActiveBalance(BigDecimal.valueOf(0));
					wallet.setUserId(walletService.getUserIdFromWallet(order.getWalletIdSell()));
					wallet1ForBuyCurrency = walletService.createNewWallet(wallet);
				}
				walletService.setWalletABalance(wallet1ForBuyCurrency, amountForSeller);
				walletService.setWalletRBalance(order.getWalletIdSell(), order.getAmountSell().negate());
				Currency currencySell = new Currency();
				currencySell.setId(order.getCurrencySell());
				CompanyWallet companyWalletSell = companyWalletService.findByCurrency(currencySell);
				companyWalletService.deposit(companyWalletSell, BigDecimal.valueOf(0), order.getCommissionAmountSell());

				//for buyer
				BigDecimal amountForBuyer = order.getAmountSell().subtract(order.getCommissionAmountSell());
				int wallet2ForBuyCurrency = walletService.getWalletId(userId, order.getCurrencySell());
				if(wallet2ForBuyCurrency == 0){
					Wallet wall = new Wallet();
					wall.setActiveBalance(BigDecimal.valueOf(0));
					wall.setUserId(userId);
					wall.setCurrencyId(order.getCurrencySell());
					wallet2ForBuyCurrency = walletService.createNewWallet(wall);
				}
				walletService.setWalletABalance(wallet2ForBuyCurrency, amountForBuyer);
				walletService.setWalletABalance(walletService.getWalletId(userId, order.getCurrencyBuy()), order.getAmountBuy().negate());

				Currency currencyBuy = new Currency();
				currencyBuy.setId(order.getCurrencyBuy());
				CompanyWallet companyWalletBuy = companyWalletService.findByCurrency(currencyBuy);
				companyWalletService.deposit(companyWalletBuy, BigDecimal.valueOf(0), order.getCommissionAmountBuy());

	            order.setWalletIdBuy(wallet2ForBuyCurrency);
	            order.setStatus(OrderStatus.CLOSED);
				order.setDateFinal(LocalDateTime.now());
				updateOrder(order);
				flag = true;
			}
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("Error while accepting order with id = "+order.getId()+" exception: "+e.getMessage());
		}
		return flag;
	}
	
	
	@Transactional(rollbackFor={Throwable.class})
	@Override
	public boolean cancellOrder(int orderId) {
		boolean flag = false;
		try{
			setStatus(orderId, OrderStatus.CANCELLED);
			Order order = getOrderById(orderId);
			walletService.setWalletABalance(order.getWalletIdSell(), order.getAmountSell());
			walletService.setWalletRBalance(order.getWalletIdSell(), order.getAmountSell().negate());
			flag = true;
		} catch (Throwable e) {
			logger.error("Error while cancelling order "+orderId+" , "+e.getMessage());
		}
		
		return flag;
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
