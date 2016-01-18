package me.exrates.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import me.exrates.beans.Currency;
import me.exrates.beans.User;
import me.exrates.beans.Wallet;
import me.exrates.daos.WalletDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


public class WalletDaoImpl implements WalletDao {

	//private static final Logger logger=Logger.getLogger(WalletDaoImpl.class); 
	@Autowired  
	DataSource dataSource;
	
	public double getWalletABalance(Wallet wallet) {
		String sql = "SELECT active_balance FROM wallet WHERE id = :walletId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("walletId", String.valueOf(wallet.getId()));
		double balance = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
		return balance;
	}
	
	public double getWalletRBalance(Wallet wallet) {
		String sql = "SELECT reserved_balance FROM wallet WHERE id = :walletId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("walletId", String.valueOf(wallet.getId()));
		double balance = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
		return balance;
	}

	public int getWalletId(int userId, int currencyId) {
		String sql = "SELECT id FROM wallet WHERE user_id = :userId AND currency_id = :currencyId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("userId", String.valueOf(userId));
		namedParameters.put("currencyId", String.valueOf(currencyId));
		int id = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
		return id;
	}
	
	
	public boolean createNewWallet(Wallet wallet) {
		String sql = "INSERT wallet(currency_id,users_id,active_balance) VALUES(:currId,:userId,:activeBalance)";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("currId", String.valueOf(wallet.getCurrId()));
		namedParameters.put("userId", String.valueOf(wallet.getUserId()));
		namedParameters.put("activeBalance", String.valueOf(wallet.getActiveBalance()));
		int result = namedParameterJdbcTemplate.update(sql, namedParameters);
		if(result > 0) {
			return true;
		}
		else return false;
	}

	@Override
	public List<Wallet> getAllWallets(int userId) {
		String sql = "select id, currency_id, user_id,active_balance, reserved_balance from wallet where user_id=:user_id";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("user_id", String.valueOf(userId));		
		List<Wallet> walletList = new ArrayList<Wallet>();  
		walletList = namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<Wallet>(){
			public Wallet mapRow(ResultSet rs, int row) throws SQLException {
						Wallet wallet = new Wallet();
						wallet.setId(rs.getInt("id"));
						wallet.setCurrId(rs.getInt("currency_id"));
						wallet.setUserId(rs.getInt("user_id"));
						wallet.setActiveBalance(rs.getDouble("active_balance"));
						wallet.setReservedBalance(rs.getDouble("reserved_balance"));
						return wallet;
			}
		});
		return walletList;
	}
	
	
}
