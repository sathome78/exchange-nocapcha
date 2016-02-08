package me.exrates.dao.impl;

import static me.exrates.jdbc.TokenRowMapper.tokenRowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import me.exrates.dao.WalletDao;
import me.exrates.model.Wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class WalletDaoImpl implements WalletDao {

	//private static final Logger logger=Logger.getLogger(WalletDaoImpl.class); 
	@Autowired  
	DataSource dataSource;
	
	public double getWalletABalance(int walletId) {
		String sql = "SELECT active_balance FROM wallet WHERE id = :walletId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("walletId", String.valueOf(walletId));
		return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
	}
	
	public double getWalletRBalance(int walletId) {
		String sql = "SELECT reserved_balance FROM wallet WHERE id = :walletId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("walletId", String.valueOf(walletId));
		return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
	}

	@Override
	public boolean setWalletABalance(int walletId, double newBalance) {
		final String sql = "UPDATE WALLET SET active_balance =:newBalance WHERE id =:walletId";
		final Map<String,String> params = new HashMap<String,String>() {
			{
				put("newBalance",String.valueOf(newBalance));
				put("walletId",String.valueOf(walletId));
			}
		};
		return new NamedParameterJdbcTemplate(dataSource).update(sql,params) > 0;
	}

	@Override
	public boolean setWalletRBalance(int walletId,double newBalance) {
		final String sql = "UPDATE WALLET SET reserved_balance =:newBalance WHERE id =:walletId";
		final Map<String,String> params = new HashMap<String,String>() {
			{
				put("newBalance",String.valueOf(newBalance));
				put("walletId",String.valueOf(walletId));
			}
		};
		return new NamedParameterJdbcTemplate(dataSource).update(sql,params) > 0;
	}

	public int getWalletId(int userId, int currencyId) {
		String sql = "SELECT id FROM wallet WHERE user_id = :userId AND currency_id = :currencyId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("userId", String.valueOf(userId));
		namedParameters.put("currencyId", String.valueOf(currencyId));
		try {
            int id = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
            return id;
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
	}
	
	
	public int createNewWallet(Wallet wallet) {
		String sql = "INSERT wallet(currency_id,user_id,active_balance) VALUES(:currId,:userId,:activeBalance)";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource parameters = new MapSqlParameterSource()
        .addValue("currId", wallet.getCurrId())
        .addValue("userId", wallet.getUserId())
        .addValue("activeBalance", wallet.getActiveBalance());
		int result = namedParameterJdbcTemplate.update(sql, parameters, keyHolder);
		int id = (int) keyHolder.getKey().longValue();
		if(result <= 0) {
			id = 0;
		}
		return id;
	}

	@Override
	public List<Wallet> getAllWallets(int userId) {
		String sql = "SELECT WALLET.id,WALLET.currency_id,WALLET.user_id,WALLET.active_balance,WALLET.reserved_balance, CURRENCY.name as wallet_name FROM WALLET" +
				"  INNER JOIN CURRENCY On WALLET.currency_id = CURRENCY.id and WALLET.user_id = :userId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
		final Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("userId", String.valueOf(userId));
		return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
			Wallet wallet = new Wallet();
			wallet.setId(rs.getInt("id"));
			wallet.setCurrId(rs.getInt("currency_id"));
			wallet.setUserId(rs.getInt("user_id"));
			wallet.setActiveBalance(rs.getDouble("active_balance"));
			wallet.setReservedBalance(rs.getDouble("reserved_balance"));
			wallet.setName(rs.getString("wallet_name"));
			return wallet;
        });
	}
	
	public int getUserIdFromWallet(int walletId) {
		String sql = "SELECT user_id FROM wallet WHERE id = :walletId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("walletId", String.valueOf(walletId));
		int userId = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
		return userId;
	}
}
