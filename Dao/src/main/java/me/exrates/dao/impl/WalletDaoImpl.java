package me.exrates.dao.impl;

import me.exrates.dao.WalletDao;
import me.exrates.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
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
		double balance = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
		return balance;
	}
	
	public double getWalletRBalance(int walletId) {
		String sql = "SELECT reserved_balance FROM wallet WHERE id = :walletId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("walletId", String.valueOf(walletId));
		double balance = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
		return balance;
	}

	@Override
	public boolean setWalletABalance(int walletId,BigDecimal newBalance) {
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
	public boolean setWalletRBalance(int walletId,BigDecimal newBalance) {
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
		return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
	}
	
	
	public boolean createNewWallet(Wallet wallet) {
		String sql = "INSERT wallet(currency_id,users_id,active_balance) VALUES(:currId,:userId,:activeBalance)";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("currId", String.valueOf(wallet.getCurrId()));
		namedParameters.put("userId", String.valueOf(wallet.getUserId()));
		namedParameters.put("activeBalance", String.valueOf(wallet.getActiveBalance()));
		return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
	}

	@Override
	public List<Wallet> getAllWallets(int userId) {
		String sql = "select id, currency_id, user_id,active_balance, reserved_balance from wallet where user_id=:user_id";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("user_id", String.valueOf(userId));		
		List<Wallet> walletList;
		walletList = namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
                    Wallet wallet = new Wallet();
                    wallet.setId(rs.getInt("id"));
                    wallet.setCurrId(rs.getInt("currency_id"));
                    wallet.setUserId(rs.getInt("user_id"));
                    wallet.setActiveBalance(rs.getDouble("active_balance"));
                    wallet.setReservedBalance(rs.getDouble("reserved_balance"));
                    return wallet;
        });
		return walletList;
	}
}