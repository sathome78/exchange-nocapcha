package me.exrates.dao.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CurrencyDaoImpl implements CurrencyDao {

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	public List<Currency> getCurrList() {
		String sql = "SELECT id, name FROM CURRENCY";
		List<Currency> currList;
		currList = jdbcTemplate.query(sql, (rs, row) -> {
			Currency currency = new Currency();
			currency.setId(rs.getInt("id"));
			currency.setName(rs.getString("name"));
			return currency;

		});
		return currList;
	}

	@Override
	public int getCurrencyId(int walletId) {
		String sql = "SELECT currency_id FROM WALLET WHERE id = :walletId ";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("walletId", String.valueOf(walletId));
		return jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
	}

	@Override
	public String getCurrencyName(int currencyId) {
		String sql = "SELECT name FROM CURRENCY WHERE  id = :id ";
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("id", String.valueOf(currencyId));
		return jdbcTemplate.queryForObject(sql, namedParameters, String.class);
	}

	@Override
	public Currency findByName(String name) {
		final String sql = "SELECT * FROM CURRENCY WHERE name = :name";
		final Map<String,String> params = new HashMap<String,String>(){
			{
				put("name", name);
			}
		};
		return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Currency.class));
	}

	@Override
	public Currency findById(int id) {
		final String sql = "SELECT * FROM CURRENCY WHERE id = :id";
		final Map<String,Integer> params = new HashMap<String,Integer>(){
			{
				put("id", id);
			}
		};
		return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Currency.class));
	}

	@Override
	public List<CurrencyPair> getAllCurrencyPairs() {
		String sql = "SELECT currency1_id, currency2_id, name, \n" +
				"(select name from currency where id = currency1_id) as currency1_name,\n" +
				"(select name from currency where id = currency2_id) as currency2_name\n" +
				" FROM CURRENCY_PAIR ";

		List<CurrencyPair> currencyPairList = jdbcTemplate.query(sql, (rs, row) -> {
			CurrencyPair currencyPair = new CurrencyPair();
			Currency currency1 = new Currency();
			currency1.setId(rs.getInt("currency1_id"));
			currency1.setName(rs.getString("currency1_name"));
			currencyPair.setCurrency1(currency1);

			Currency currency2 = new Currency();
			currency2.setId(rs.getInt("currency2_id"));
			currency2.setName(rs.getString("currency2_name"));
			currencyPair.setCurrency2(currency2);

			currencyPair.setName(rs.getString("name"));

			return currencyPair;

		});

		return currencyPairList;
	}

	@Override
	public CurrencyPair getCurrencyPairById(int currency1Id, int currency2Id) {
		String sql = "SELECT currency1_id, currency2_id, name, \n" +
				"(select name from currency where id = currency1_id) as currency1_name,\n" +
				"(select name from currency where id = currency2_id) as currency2_name\n" +
				" FROM CURRENCY_PAIR WHERE currency1_id = :currency1Id AND currency2_id = :currency2Id";

		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("currency1Id", String.valueOf(currency1Id));
		namedParameters.put("currency2Id", String.valueOf(currency2Id));
		return jdbcTemplate.queryForObject(sql, namedParameters,(resultSet, i) -> {
			CurrencyPair currencyPair = new CurrencyPair();
			Currency currency1 = new Currency();
			currency1.setId(resultSet.getInt("currency1_id"));
			currency1.setName(resultSet.getString("currency1_name"));
			currencyPair.setCurrency1(currency1);

			Currency currency2 = new Currency();
			currency2.setId(resultSet.getInt("currency2_id"));
			currency2.setName(resultSet.getString("currency2_name"));
			currencyPair.setCurrency2(currency2);

			currencyPair.setName(resultSet.getString("name"));

			return currencyPair;
		});
	}
}