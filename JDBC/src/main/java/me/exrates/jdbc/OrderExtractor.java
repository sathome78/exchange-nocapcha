package me.exrates.jdbc;

  
import java.sql.ResultSet;  
import java.sql.SQLException;  

import me.exrates.model.Order;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;

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
	int operationType = rs.getInt("operation_type");
	OperationType[] typeenum = OperationType.values();
	for(OperationType t : typeenum) {
		if(t.type == operationType) {
			order.setOperationType(t);
		}
	}
	int status = rs.getInt("status");
	OrderStatus[] statusenum = OrderStatus.values();
	for(OrderStatus s : statusenum) {
		if(s.status == status) {
			order.setStatus(s);
		}
	}

	order.setDateCreation(rs.getDate("date_creation"));
	order.setDateFinal(rs.getDate("date_final"));
	return order;
 }  
  
}  
