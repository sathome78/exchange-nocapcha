package me.exrates.dao.impl;

import me.exrates.dao.WalletDao;
import me.exrates.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

@Repository
public class WalletDaoImpl implements WalletDao {

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	public double getWalletABalance(int walletId) {
		String sql = "SELECT active_balance FROM WALLET WHERE id = :walletId";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("walletId", String.valueOf(walletId));
		return jdbcTemplate.queryForObject(sql, namedParameters, Double.class);
	}

	public double getWalletRBalance(int walletId) {
		String sql = "SELECT reserved_balance FROM WALLET WHERE id = :walletId";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("walletId", String.valueOf(walletId));
		return jdbcTemplate.queryForObject(sql, namedParameters, Double.class);
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
		return jdbcTemplate.update(sql,params) > 0;
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
		return jdbcTemplate.update(sql,params) > 0;
	}

	public int getWalletId(int userId, int currencyId) {
		String sql = "SELECT id FROM WALLET WHERE user_id = :userId AND currency_id = :currencyId";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("userId", String.valueOf(userId));
		namedParameters.put("currencyId", String.valueOf(currencyId));
		try {
			return jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}


	public int createNewWallet(Wallet wallet) {
		String sql = "INSERT INTO WALLET (currency_id,user_id,active_balance) VALUES(:currId,:userId,:activeBalance)";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource parameters = new MapSqlParameterSource()
				.addValue("currId", wallet.getCurrencyId())
				.addValue("userId", wallet.getUserId())
				.addValue("activeBalance", wallet.getActiveBalance());
		int result = jdbcTemplate.update(sql, parameters, keyHolder);
		int id = (int) keyHolder.getKey().longValue();
		if(result <= 0) {
			id = 0;
		}
		return id;
	}

	@Override
	public List<Wallet> findAllByUser(int userId) {
		final String sql = "SELECT WALLET.id,WALLET.currency_id,WALLET.user_id,WALLET.active_balance, WALLET.reserved_balance, CURRENCY.name as name FROM WALLET" +
				"  INNER JOIN CURRENCY On WALLET.currency_id = CURRENCY.id and WALLET.user_id = :userId";
		final Map<String, Integer> params = unmodifiableMap(of(
				new SimpleEntry<>("userId", userId))
				.collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
		return jdbcTemplate.query(sql,params,new BeanPropertyRowMapper<>(Wallet.class));
	}

	@Override
	public Wallet findByUserAndCurrency(int userId,int currencyId) {
		final String sql = "SELECT WALLET.id,WALLET.currency_id,WALLET.user_id,WALLET.active_balance, WALLET.reserved_balance, CURRENCY.name as name FROM WALLET INNER JOIN CURRENCY On" +
				"  WALLET.currency_id = CURRENCY.id WHERE user_id = :userId and currency_id = :currencyId";
		final Map<String, Integer> params = unmodifiableMap(of(
				new SimpleEntry<>("userId", userId),
				new SimpleEntry<>("currencyId",currencyId))
				.collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
		try {
			return jdbcTemplate.queryForObject(sql,params, new BeanPropertyRowMapper<>(Wallet.class));
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	@Override
	public Wallet createWallet(int userId, int currencyId) {
		final String sql = "INSERT INTO WALLET (currency_id,user_id) VALUES(:currId,:userId)";
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		final MapSqlParameterSource parameters = new MapSqlParameterSource()
				.addValue("currId", currencyId)
				.addValue("userId", userId);
		if (jdbcTemplate.update(sql, parameters, keyHolder)>0) {
			Wallet wallet = new Wallet();
			wallet.setActiveBalance(0);
			wallet.setReservedBalance(0);
			wallet.setId(keyHolder.getKey().intValue());
			wallet.setCurrencyId(currencyId);
			return wallet;
		}
		return null;
	}

	@Override
	public boolean update(Wallet wallet) {
		final String sql = "UPDATE WALLET SET active_balance = :activeBalance, reserved_balance = :reservedBalance WHERE id = :id";
		final Map<String,Object> params = unmodifiableMap(of(
				new SimpleEntry<>("id",wallet.getId()),
				new SimpleEntry<>("activeBalance", wallet.getActiveBalance()),
				new SimpleEntry<>("reservedBalance", wallet.getReservedBalance())
		).collect(toMap(SimpleEntry::getKey,SimpleEntry::getValue)));
		return jdbcTemplate.update(sql,params) == 1;
	}


	public int getUserIdFromWallet(int walletId) {
		final String sql = "SELECT user_id FROM WALLET WHERE id = :walletId";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("walletId", String.valueOf(walletId));
		try {
			return jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}
}