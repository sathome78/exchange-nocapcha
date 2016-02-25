package me.exrates.dao.impl;

import me.exrates.dao.UserDao;
import me.exrates.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

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
		String sql = "insert into USER(nickname,email,password) values(:nickname,:email,:password)";
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("email", user.getEmail());
		namedParameters.put("nickname", user.getNickname());
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String hashedPassword = passwordEncoder.encode(user.getPassword());
		namedParameters.put("password", hashedPassword);
		return jdbcTemplate.update(sql, namedParameters) > 0;
	}

	public List<String> getUserRoles(String email) {
		String sql = "select name from USER_ROLE where user_id=(select id from USER where email = :email)";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("email", email);
		return jdbcTemplate.query(sql, namedParameters, (rs, row) -> {
			return rs.getString("name");
		});
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
		final Map<String,String> params = unmodifiableMap(of(
				new SimpleEntry<>("email", email)
		).collect(toMap(SimpleEntry::getKey,SimpleEntry::getValue)));
		return jdbcTemplate.queryForObject(sql,params, new BeanPropertyRowMapper<>(User.class));
	}

	public List<User> getAllUsers() {
		String sql = "select email, password, status from USER";
		return jdbcTemplate.query(sql, (rs, row) -> {
            User user = new User();
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setStatus(rs.getString("status"));
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

	public void update(User user) {
		// TODO Auto-generated method stub
	}

	public void delete(User user) {
		// TODO Auto-generated method stub
	}
}