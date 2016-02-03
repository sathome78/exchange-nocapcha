package me.exrates.dao.impl;

<<<<<<< HEAD
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import me.exrates.dao.WalletDao;
import me.exrates.model.Currency;
import me.exrates.model.Order;
import me.exrates.model.User;
import me.exrates.model.Wallet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


=======
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

@Repository
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
public class WalletDaoImpl implements WalletDao {

	//private static final Logger logger=Logger.getLogger(WalletDaoImpl.class); 
	@Autowired  
	DataSource dataSource;
	
	public double getWalletABalance(int walletId) {
		String sql = "SELECT active_balance FROM wallet WHERE id = :walletId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
<<<<<<< HEAD
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("walletId", String.valueOf(walletId));
		double balance = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
		return balance;
=======
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("walletId", String.valueOf(walletId));
		return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
	}
	
	public double getWalletRBalance(int walletId) {
		String sql = "SELECT reserved_balance FROM wallet WHERE id = :walletId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
<<<<<<< HEAD
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("walletId", String.valueOf(walletId));
		double balance = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
		return balance;
=======
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
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
	}

	public int getWalletId(int userId, int currencyId) {
		String sql = "SELECT id FROM wallet WHERE user_id = :userId AND currency_id = :currencyId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
<<<<<<< HEAD
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("userId", String.valueOf(userId));
		namedParameters.put("currencyId", String.valueOf(currencyId));
		int id = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
		return id;
=======
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("userId", String.valueOf(userId));
		namedParameters.put("currencyId", String.valueOf(currencyId));
		return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
	}
	
	
	public boolean createNewWallet(Wallet wallet) {
		String sql = "INSERT wallet(currency_id,users_id,active_balance) VALUES(:currId,:userId,:activeBalance)";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
<<<<<<< HEAD
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("currId", String.valueOf(wallet.getCurrId()));
		namedParameters.put("userId", String.valueOf(wallet.getUserId()));
		namedParameters.put("activeBalance", String.valueOf(wallet.getActiveBalance()));
		int result = namedParameterJdbcTemplate.update(sql, namedParameters);
		if(result > 0) {
			return true;
		}
		else return false;
=======
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("currId", String.valueOf(wallet.getCurrId()));
		namedParameters.put("userId", String.valueOf(wallet.getUserId()));
		namedParameters.put("activeBalance", String.valueOf(wallet.getActiveBalance()));
		return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
	}

	@Override
	public List<Wallet> getAllWallets(int userId) {
<<<<<<< HEAD
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
=======
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
            wallet.setName("wallet_name");
			return wallet;
        });
	}
}
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
