package me.exrates.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.exrates.model.Order;
import me.exrates.model.User;

import org.springframework.jdbc.core.RowMapper;

public class OrderRowMapper implements RowMapper<Order> {
	
 
	public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
		OrderExtractor orderExtractor = new OrderExtractor();  
		  return orderExtractor.extractData(rs);  
	}
	
}

