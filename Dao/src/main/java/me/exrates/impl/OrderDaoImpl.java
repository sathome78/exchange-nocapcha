package me.exrates.impl;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import me.exrates.beans.Order;
import me.exrates.daos.OrderDao;


public class OrderDaoImpl implements OrderDao{

	//private static final Logger logger=Logger.getLogger(OrderDaoImpl.class); 
	@Autowired  
	DataSource dataSource;  

	public boolean createOrder(Order order) {
		
		String sql = "insert into order(wallet_id_from,amount_from,currency_to,exchange_rate)"
				+ " values(:walletFrom,:amountFrom,:currencyTo,:exchangeRate)";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("walletFrom", String.valueOf(order.getWalletIdFrom()));
		namedParameters.put("amountFrom", String.valueOf(order.getAmountFrom()));
		namedParameters.put("currencyTo", String.valueOf(order.getCurrencyTo()));
		namedParameters.put("exchangeRate", String.valueOf(order.getExchangeRate()));
		int result = namedParameterJdbcTemplate.update(sql, namedParameters);
		if(result > 0) {
			return true;
		}
		else return false;
		
	}

	
	
}
