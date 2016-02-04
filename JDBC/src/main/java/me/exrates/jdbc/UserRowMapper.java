package me.exrates.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import me.exrates.model.User;

import org.springframework.jdbc.core.RowMapper;

public class UserRowMapper implements RowMapper<User> {
	
 
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserExtractor userExtractor = new UserExtractor();  
		  return userExtractor.extractData(rs);  
	}
	
}

