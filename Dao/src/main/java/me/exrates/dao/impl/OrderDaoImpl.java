package me.exrates.dao.impl;

import me.exrates.dao.OrderDao;
import me.exrates.jdbc.OrderRowMapper;
import me.exrates.model.Order;
import me.exrates.model.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderDaoImpl implements OrderDao{

	//private static final Logger logger=Logger.getLogger(OrderDaoImpl.class); 
	@Autowired  
	NamedParameterJdbcTemplate jdbcTemplate;

	public int createOrder(Order order) {
		
		String sql = "INSERT INTO ORDERS(wallet_id_sell,amount_sell,currency_buy,amount_buy, operation_type) values(:walletSell,:amountSell,:currencyBuy,:amountBuy, :operationType)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("walletSell", order.getWalletIdSell())
        .addValue("amountSell", order.getAmountSell())
        .addValue("currencyBuy", order.getCurrencyBuy())
        .addValue("amountBuy", order.getAmountBuy())
        .addValue("operationType", order.getOperationType().type);
		int result = jdbcTemplate.update(sql, parameters, keyHolder);
		int id = (int) keyHolder.getKey().longValue();
		if(result <= 0) {
			id = 0;
		}
		return id;
	}

	@Override
	public List<Order> getMyOrders(int userId) {
		String sql = "select * from ORDERS where "
				+ "(wallet_id_sell in (select id from WALLET where user_id=:user_id) ||"
				+"wallet_id_buy in (select id from WALLET where user_id=:user_id))"
				+ "&& (status=1 || status=2 || status=3)";
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("user_id", String.valueOf(userId));		
		return jdbcTemplate.query(sql, namedParameters, new OrderRowMapper());
	}
	
	@Override
	public List<Order> getAllOrders() {
		String sql = "select * from ORDERS where status = 2";
		return jdbcTemplate.query(sql, new OrderRowMapper());
	}

	@Override
	public boolean deleteOrder(int orderId) {
		String sql = "delete from ORDERS where id = :id";
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("id", String.valueOf(orderId));
		int result = jdbcTemplate.update(sql, namedParameters);
		if(result > 0) {
			return true;
		}
		else return false;
		
	}

	@Override
	public Order getOrderById(int orderId) {
		String sql = "select * from ORDERS where id = :id";
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("id", String.valueOf(orderId));
		List<Order> orderList;
		orderList = jdbcTemplate.query(sql, namedParameters, new OrderRowMapper());
		Order order = orderList.get(0);
		return order;
	}

	@Override
	public boolean setStatus(int orderId, OrderStatus status) {
		String sql = "update ORDERS set status=:status where id = :id";
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("status", String.valueOf(status.getStatus()));
		namedParameters.put("id", String.valueOf(orderId));
		int result = jdbcTemplate.update(sql, namedParameters);
		if(result > 0) {
			return true;
		}
		else return false;
		
	}

	@Override
	public boolean updateOrder(Order order) {
		String sql = "update ORDERS set wallet_id_buy=:walletBuy, status=:status, date_final=:dateFinal  where id = :id";
		Map<String, String> namedParameters = new HashMap<String, String>();
	    namedParameters.put("walletBuy", String.valueOf(order.getWalletIdBuy()));
	    namedParameters.put("status", String.valueOf(order.getStatus().getStatus()));
		namedParameters.put("dateFinal", String.valueOf(order.getDateFinal()));
		namedParameters.put("id", String.valueOf(order.getId()));
		System.out.println("ststus = "+order.getStatus().getStatus());
		int result = jdbcTemplate.update(sql, namedParameters);
		if(result > 0) {
			return true;
		}
		else return false;
	}

}


	

