package me.exrates.dao.impl;

import me.exrates.dao.UserDao;
import me.exrates.model.RegistrationToken;
import me.exrates.model.User;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDaoImpl implements UserDao { 
		
	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	public int getIdByEmail(String email) {
		String sql = "SELECT id FROM USER WHERE email = :email";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("email", email);
		return jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
	}
		
		public boolean create(User user) {
			String sql = "insert into USER(nickname,email,password,phone,status,roleid ) " +
					"values(:nickname,:email,:password,:phone,:status,:roleid)";
			Map<String, String> namedParameters = new HashMap<String, String>();
			namedParameters.put("email", user.getEmail());
			namedParameters.put("nickname", user.getNickname());
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(user.getPassword());
			namedParameters.put("password", hashedPassword);
			String phone = user.getPhone();
			if(user.getPhone().equals("")){
				phone = null;
			}
			namedParameters.put("phone", phone);
			namedParameters.put("status", String.valueOf(user.getStatus().getStatus()));
			namedParameters.put("roleid", String.valueOf(user.getRole().getRole()));
		return jdbcTemplate.update(sql, namedParameters) > 0;
		}

		public List<UserRole> getAllRoles() {
			String sql = "select name from USER_ROLE";
			return jdbcTemplate.query(sql, (rs, row) -> {
					UserRole role = UserRole.valueOf(rs.getString("name"));;
					return role;
			});
	}

	public UserRole getUserRoles(String email) {
		String sql = "select USER_ROLE.name as role_name from USER " +
				"inner join USER_ROLE on USER.roleid = USER_ROLE.id where USER.email = :email";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("email", email);
		return jdbcTemplate.query(sql, namedParameters, (rs, row) -> {
			UserRole role = UserRole.valueOf(rs.getString("role_name"));
			return role;
		}).get(0);
	}
		
	public boolean addUserRoles(String email, String role) {
		String sql = "insert into USER_ROLE(name, user_id) values(:name,:userid)";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("name", role);
		namedParameters.put("userid",String.valueOf(getIdByEmail(email)));
		return jdbcTemplate.update(sql, namedParameters) > 0;
	}

	@Override
	public User findByEmail(String email) {
		final String sql = "SELECT * FROM USER WHERE email = :email";
		final Map<String,String> params =  new HashMap<String, String>(){
			{
				put("email", email);
			}
		};
		return jdbcTemplate.queryForObject(sql,params, (resultSet, i) -> {
			final User user = new User();
			user.setId(resultSet.getInt("id"));
			user.setNickname(resultSet.getString("nickname"));
			user.setEmail(resultSet.getString("email"));
			user.setPassword(resultSet.getString("password"));
			user.setRegdate(resultSet.getDate("regdate"));
			user.setPhone(resultSet.getString("phone"));

			return user;
		});
	}


		public List<User> getAllUsers() {
			String sql = "select email, password, status, nickname, id from USER";
			return jdbcTemplate.query(sql, (rs, row) -> {
						User user = new User();
						user.setEmail(rs.getString("email"));
						user.setPassword(rs.getString("password"));
						user.setStatus(UserStatus.values()[rs.getInt("status")-1]);
						user.setNickname(rs.getString("nickname"));
						user.setId(rs.getInt("id"));
						return user;
			});
		}

		public User getUserById(int id) {
			String sql = "select USER.id, nickname, email, password, regdate, phone, status, USER_ROLE.name as role_name from USER " +
					"inner join USER_ROLE on USER.roleid = USER_ROLE.id where USER.id = :id";
			Map<String, String> namedParameters = new HashMap<String, String>();
			namedParameters.put("id", String.valueOf(id));

			return jdbcTemplate.queryForObject(sql,namedParameters, (resultSet, i) -> {
				final User user = new User();
				user.setId(resultSet.getInt("id"));
				user.setNickname(resultSet.getString("nickname"));
				user.setEmail(resultSet.getString("email"));
				user.setPassword(resultSet.getString("password"));
				user.setRegdate(resultSet.getDate("regdate"));
				user.setPhone(resultSet.getString("phone"));
				user.setStatus(UserStatus.values()[resultSet.getInt("status")-1]);
				user.setRole(UserRole.valueOf(resultSet.getString("role_name")));

				return user;
			});
		}

		public List<User> getUsersByRoles(List<UserRole> listRoles) {
		String sql = "select USER.id, nickname, email, password, regdate, status, phone, USER_ROLE.name as role_name" +
				" from USER inner join USER_ROLE on USER.roleid = USER_ROLE.id where USER_ROLE.name IN (:roles)";
		Map<String, List> namedParameters = new HashMap<String, List>();
		List<String> stringList = new ArrayList<>();
			for (UserRole userRole : listRoles){
				stringList.add(userRole.name());
			}
		namedParameters.put("roles", stringList);

		return jdbcTemplate.query(sql, namedParameters, (resultSet, row) -> {
			final User user = new User();
			user.setId(resultSet.getInt("id"));
			user.setNickname(resultSet.getString("nickname"));
			user.setEmail(resultSet.getString("email"));
			user.setPassword(resultSet.getString("password"));
			user.setRegdate(resultSet.getDate("regdate"));
			user.setPhone(resultSet.getString("phone"));
			user.setStatus(UserStatus.values()[resultSet.getInt("status")-1]);
			user.setRole(UserRole.valueOf(resultSet.getString("role_name")));

			return user;
		});
	}

	public String getBriefInfo(int login) {
			return null;
		}

	public boolean ifNicknameIsUnique(String nickname) {
		String sql = "SELECT id FROM USER WHERE nickname = :nickname";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("nickname", nickname);
		return jdbcTemplate.query(sql, namedParameters, (rs, row) -> {
			if (rs.next()) {
				return rs.getInt("id");
			} else return 0;
		}).isEmpty();
	}

	public boolean ifPhoneIsUnique(int phone) {
			return false;
		}

	public boolean ifEmailIsUnique(String email) {
		String sql = "SELECT id FROM USER WHERE email = :email";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("email", email);
		return jdbcTemplate.query(sql, namedParameters, (rs, row) -> {
                if(rs.next()) {
                    return rs.getInt("id");
                }
				else return 0;
		}).isEmpty();
	}

	public String getPasswordByEmail(String email) {
			return null;
		}

	public String getIP(int userId) {
		String sql = "SELECT ipaddress FROM USER WHERE id = :userId";
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("userId", String.valueOf(userId));
		return jdbcTemplate.query(sql, namedParameters, (rs, row) -> {
                if(rs.next()) {
                    return rs.getString("ipaddress");
                }
				return null;
		}).get(0);
	}

	public boolean setIP(int id, String ip) {
		String sql = "UPDATE USER SET ipaddress = :ipaddress WHERE id = :id";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("ipaddress",ip);
		namedParameters.put("id", String.valueOf(id));
		return jdbcTemplate.update(sql, namedParameters) > 0;
	}

	public boolean addIPToLog(int userId, String ip) {
		String sql = "insert INTO IP_Log (ip,user_id) values(:ip,:userId)";
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("ip",ip);
		namedParameters.put("userId", String.valueOf(userId));
		return jdbcTemplate.update(sql, namedParameters) > 0;
	}

		public boolean update(User user) {
			String sql = "UPDATE USER SET nickname = :nickname, email = :email, password = :password," +
					"phone = :phone, status = :status, roleid = :roleid WHERE USER.id = :id; ";

			Map<String, String> namedParameters = new HashMap<String, String>();
			namedParameters.put("id", String.valueOf(user.getId()));
			namedParameters.put("nickname", user.getNickname());
			namedParameters.put("email", user.getEmail());
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword;
			if (user.getPassword() == ""){
				hashedPassword = getUserById(user.getId()).getPassword();
			}else {
				hashedPassword = passwordEncoder.encode(user.getPassword());
			}
			namedParameters.put("password", hashedPassword);
			String phone = String.valueOf(user.getPhone());
			if(user.getPhone().isEmpty()){
				phone = null;
			}
			namedParameters.put("phone", phone);
			namedParameters.put("status", String.valueOf(user.getStatus().getStatus()));
			namedParameters.put("roleid", String.valueOf(user.getRole().getRole()));

			return jdbcTemplate.update(sql, namedParameters) > 0;
		}

	public boolean createRegistrationToken(RegistrationToken token) {
		String sql = "insert into REGISTRATION_TOKEN(value,user_id) values(:value,:user_id)";
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("value", token.getValue());
		namedParameters.put("user_id", String.valueOf(token.getUserId()));
		return jdbcTemplate.update(sql, namedParameters) > 0;
	}
	
	public RegistrationToken verifyToken(String token) {
		String sql = "SELECT * FROM REGISTRATION_TOKEN WHERE VALUE= :value";
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("value", token);
		return jdbcTemplate.query(sql, namedParameters, (rs, row) -> {
                    RegistrationToken rt = new RegistrationToken();
                    rt.setId(rs.getInt("id"));
                    rt.setUserId(rs.getInt("user_id"));
                    rt.setValue(token);
                    rt.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                    rt.setExpired(rs.getBoolean("expired"));
                	return rt;
 
		}).get(0);
	}
	
	public boolean deleteRegistrationToken(RegistrationToken token) {
		String sql = "delete from REGISTRATION_TOKEN where id = :id";
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("id", String.valueOf(token.getId()));
		return jdbcTemplate.update(sql, namedParameters) > 0;
	}
	
	public boolean updateUserStatus(User user) {
		String sql = "update USER set status=:status where id=:id";
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("status", String.valueOf(user.getStatus().getStatus()));
		namedParameters.put("id", String.valueOf(user.getId()));
		return jdbcTemplate.update(sql, namedParameters) > 0;
	}


}