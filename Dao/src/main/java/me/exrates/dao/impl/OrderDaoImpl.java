package me.exrates.dao.impl;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import me.exrates.dao.OrderDao;
import me.exrates.model.Order;
import org.springframework.stereotype.Repository;

@Repository
public class OrderDaoImpl implements OrderDao{

	//private static final Logger logger=Logger.getLogger(OrderDaoImpl.class); 
	@Autowired
	DataSource dataSource;

	public boolean createOrder(Order order) {

		String sql = "insert into order(wallet_id_sell,amount_sell,"
				+ "currency_buy,exchange_rate, operation_type)"
				+ " values(:walletSell,:amountSell,:currencyBuy,:exchangeRate, operationType)";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("walletSell", String.valueOf(order.getWalletIdSell()));
		namedParameters.put("amountSell", String.valueOf(order.getAmountSell()));
		namedParameters.put("currencyBuy", String.valueOf(order.getCurrencyBuy()));
		namedParameters.put("exchangeRate", String.valueOf(order.getExchangeRate()));
		namedParameters.put("operationType", String.valueOf(order.getOperationType()));
		int result = namedParameterJdbcTemplate.update(sql, namedParameters);
		return result > 0;
	}
}