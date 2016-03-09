package me.exrates.jdbc;


import me.exrates.model.User;
import me.exrates.model.enums.UserStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

  
public class UserExtractor implements ResultSetExtractor<User> {  
  
 public User extractData(ResultSet rs) throws SQLException, DataAccessException {  
    
    User user = new User();  
	user.setId(rs.getInt("id"));
	user.setNickname(rs.getString("nickname"));
	user.setEmail(rs.getString("email"));
	user.setPassword(rs.getString("password"));
	int status = rs.getInt("status");
	UserStatus[] statusenum = UserStatus.values();
	for(UserStatus s : statusenum) {
		if(s.getStatus() == status) {
			user.setStatus(s);
		}
	}
	return user;
 }  
  
}  
