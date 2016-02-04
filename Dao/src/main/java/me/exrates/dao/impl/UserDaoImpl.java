package me.exrates.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import me.exrates.dao.UserDao;
import me.exrates.model.User;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao { 
		
		private static final Logger logger=Logger.getLogger(UserDaoImpl.class);
		
		@Autowired  
		DataSource dataSource;  

		public int getIdByEmail(String email) {
			String sql = "SELECT id FROM user WHERE email = :email";
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
			Map<String, String> namedParameters = new HashMap<String, String>();
			namedParameters.put("email", email);
			int id = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
			return id;
		}
		
		public boolean create(User user) {
			String sql = "insert into user(nickname,email,password) values(:nickname,:email,:password)";
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
			Map<String, String> namedParameters = new HashMap<String, String>();
			namedParameters.put("email", user.getEmail());
			namedParameters.put("nickname", user.getNickname());
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(user.getPassword());
			namedParameters.put("password", hashedPassword);
			int result = namedParameterJdbcTemplate.update(sql, namedParameters);
			
			if(result > 0) {
				
				return true;
			}
			else return false;

		}

		
		public List<String> getUserRoles(String email) {
			String sql = "select name from user_role where user_id=(select id from user where email = :email)";
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
			Map<String, String> namedParameters = new HashMap<String, String>();
			namedParameters.put("email", email);
			List<String> rolesList = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<String>(){
				public String mapRow(ResultSet rs, int row)	throws SQLException {
					String name = rs.getString("name");
					return name;
				}
			});
			return rolesList;
		}
		
		public boolean addUserRoles(String email, String role) {
			String sql = "insert into user_role(name, user_id) values(:name,:userid)";
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
			Map<String, String> namedParameters = new HashMap<String, String>();
			namedParameters.put("name", role);
			namedParameters.put("userid",String.valueOf(getIdByEmail(email)));
			int result = namedParameterJdbcTemplate.update(sql, namedParameters);
			if(result > 0) {
				return true;
			}
			else return false;

		}


		public List<User> getAllUsers() {
			String sql = "select email, password, status from user";
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);		
			List<User> userList = new ArrayList<User>();  
			userList = jdbcTemplate.query(sql, new RowMapper<User>(){
				public User mapRow(ResultSet rs, int row) throws SQLException {
						User user = new User();
						user.setEmail(rs.getString("email"));
						user.setPassword(rs.getString("password"));
						user.setStatus(rs.getString("status"));
						return user;

				}
			});
			return userList;
		}
		
		public String getBriefInfo(int login) {
			return null;
		}

		public boolean ifNicknameIsUnique(String nickname) {
			String sql = "SELECT id FROM user WHERE nickname = :nickname";
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
			Map<String, String> namedParameters = new HashMap<String, String>();
			namedParameters.put("nickname", nickname);
			List<Integer> idList = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<Integer>(){
				public Integer mapRow(ResultSet rs, int row)
						throws SQLException {
					if(rs.next()) {
						return rs.getInt("id");
					}
					else return 0;
				}
			});
			return idList.isEmpty();
		}

		public boolean ifPhoneIsUnique(int phone) {
			return false;
		}

		public boolean ifEmailIsUnique(String email) {
			String sql = "SELECT id FROM user WHERE email = :email";
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
			Map<String, String> namedParameters = new HashMap<String, String>();
			namedParameters.put("email", email);
			List<Integer> idList = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<Integer>(){
				public Integer mapRow(ResultSet rs, int row)
						throws SQLException {
					if(rs.next()) {
						return rs.getInt("id");
					}
					else return 0;
				}
			});
			return idList.isEmpty();
		}

		public String getPasswordByEmail(String email) {
			return null;
		}

		public String getIP(int userId) {
			String sql = "SELECT ipaddress FROM user WHERE id = :userId";
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
			Map<String, String> namedParameters = new HashMap<String, String>();
			namedParameters.put("userId", String.valueOf(userId));
			List<String> ip = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<String>(){
				public String mapRow(ResultSet rs, int row)
						throws SQLException {
					if(rs.next()) {
						return rs.getString("ipaddress");
					}
					else return null;
				}
			});
			return ip.get(0);
		}

		public boolean setIP(int id, String ip) {
			String sql = "UPDATE user SET ipaddress = :ipaddress WHERE id = :id";
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
			Map<String, String> namedParameters = new HashMap<String, String>();
			namedParameters.put("ipaddress",ip);
			namedParameters.put("id", String.valueOf(id));
			int result = namedParameterJdbcTemplate.update(sql, namedParameters);
			if(result > 0) {
				return true;
			}
			else return false;
		}

		public boolean addIPToLog(int userId, String ip) {
			String sql = "insert ip_log(ip,user_id) values(:ip,:userId)";
			NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
			Map<String, String> namedParameters = new HashMap<String, String>();
			namedParameters.put("ip",ip);
			namedParameters.put("userId", String.valueOf(userId));
			int result = namedParameterJdbcTemplate.update(sql, namedParameters);
			if(result > 0) {
				return true;
			}
			else return false;
		}

		public void update(User user) {
			// TODO Auto-generated method stub
			
		}

		public void delete(User user) {
			// TODO Auto-generated method stub
			
		} 
		
}