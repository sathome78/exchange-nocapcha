package me.exrates.user.dao;

  
import java.sql.ResultSet;  
import java.sql.SQLException;  

import me.exrates.beans.User;

import org.springframework.dao.DataAccessException;  
import org.springframework.jdbc.core.ResultSetExtractor;  

  
public class UserExtractor implements ResultSetExtractor<User> {  
  
 public User extractData(ResultSet rs) throws SQLException, DataAccessException {  
    
    User user = new User();  
	user.setId(rs.getInt("id"));
	user.setNickname(rs.getString("nickname"));
	user.setEmail(rs.getString("email"));
	user.setPassword(rs.getString("password"));
	user.setStatus(rs.getString("status"));
	return user;
 }  
  
}  
