package me.exrates.jdbc;

  
import java.sql.ResultSet;  
import java.sql.SQLException;  

import me.exrates.model.Order;

import org.springframework.dao.DataAccessException;  
import org.springframework.jdbc.core.ResultSetExtractor;  

  
public class OrderExtractor implements ResultSetExtractor<Order> {  
  
 public Order extractData(ResultSet rs) throws SQLException, DataAccessException {  
    
    Order order = new Order();  
	order.setId(rs.getInt("id"));
	order.setWalletIdSell(rs.getInt("wallet_id_sell"));
	order.setCurrencyBuy(rs.getInt("currency_buy"));
	order.setAmountSell(rs.getDouble("amount_sell"));
	order.setAmountBuy(rs.getDouble("amount_buy"));
	order.setWalletIdBuy(rs.getInt("wallet_id_buy"));
	order.setOperationType(rs.getInt("operation_type"));
	order.setStatus(rs.getInt("status"));
	order.setDateCreation(rs.getDate("date_creation"));
	order.setDateFinal(rs.getDate("date_final"));
	return order;
 }  
  
}  
