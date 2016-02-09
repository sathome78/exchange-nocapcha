package me.exrates.dao.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CurrencyDaoImpl implements CurrencyDao{

	//private static final Logger logger=Logger.getLogger(CurrencyDaoImpl.class);
	@Autowired
	DataSource dataSource;

	public List<Currency> getCurrList() {
		String sql = "SELECT id, name FROM currency";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
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
		String sql = "SELECT currency_id FROM wallet WHERE id = :walletId ";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("walletId", String.valueOf(walletId));
		int value = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
		return value;
	}

	@Override
	public String getCurrencyName(int currencyId) {
		String sql = "SELECT name FROM currency WHERE  id = :id ";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("id", String.valueOf(currencyId));
		String value = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, String.class);
		return value;
	}

}