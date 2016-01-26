package me.exrates.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import me.exrates.dao.OrderDao;
import me.exrates.jdbc.OrderRowMapper;
import me.exrates.model.Order;
import me.exrates.model.Wallet;


public class OrderDaoImpl implements OrderDao{

	//private static final Logger logger=Logger.getLogger(OrderDaoImpl.class); 
	@Autowired  
	DataSource dataSource;  

	public boolean createOrder(Order order) {
		
		String sql = "insert into orders(wallet_id_sell,amount_sell,currency_buy,amount_buy, operation_type) values(:walletSell,:amountSell,:currencyBuy,:amountBuy, :operationType)";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("walletSell", String.valueOf(order.getWalletIdSell()));
		namedParameters.put("amountSell", String.valueOf(order.getAmountSell()));
		namedParameters.put("currencyBuy", String.valueOf(order.getCurrencyBuy()));
		namedParameters.put("amountBuy", String.valueOf(order.getAmountBuy()));
		namedParameters.put("operationType", String.valueOf(order.getOperationType()));
		int result = namedParameterJdbcTemplate.update(sql, namedParameters);
		if(result > 0) {
			return true;
		}
		else return false;
		
	}

	@Override
	public List<Order> getMyOrders(int userId) {
		String sql = "select * from orders where wallet_id_sell in (select id from wallet where user_id=:user_id) && (status!=4 || status!=5)";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("user_id", String.valueOf(userId));		
		List<Order> orderList = new ArrayList<Order>();  
		orderList = namedParameterJdbcTemplate.query(sql, namedParameters, new OrderRowMapper());
		return orderList;
	}

	@Override
	public boolean deleteOrder(int orderId) {
		String sql = "delete from orders where id = :id";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("id", String.valueOf(orderId));
		int result = namedParameterJdbcTemplate.update(sql, namedParameters);
		if(result > 0) {
			return true;
		}
		else return false;
		
	}

	

}


	

