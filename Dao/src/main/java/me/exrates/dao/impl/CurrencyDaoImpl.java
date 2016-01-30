package me.exrates.dao.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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
}