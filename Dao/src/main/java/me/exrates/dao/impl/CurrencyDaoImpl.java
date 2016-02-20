package me.exrates.dao.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

@Repository
public class CurrencyDaoImpl implements CurrencyDao{

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
		final Map<String,String> params = unmodifiableMap(of(
				new AbstractMap.SimpleEntry<>("name", name))
				.collect(toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
		return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Currency.class));
	}
}