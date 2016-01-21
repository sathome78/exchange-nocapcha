package me.exrates.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class CurrencyDaoImpl implements CurrencyDao{

	//private static final Logger logger=Logger.getLogger(CurrencyDaoImpl.class); 
	@Autowired  
	DataSource dataSource; 
	
	public List<Currency> getCurrList() {
		String sql = "SELECT id, name FROM currency";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);		
		List<Currency> currList = new ArrayList<Currency>();  
		currList = jdbcTemplate.query(sql, new RowMapper<Currency>(){
			public Currency mapRow(ResultSet rs, int row) throws SQLException {
					Currency currency = new Currency();
					currency.setId(rs.getInt("id"));
					currency.setName(rs.getString("name"));
					return currency;

			}
		});
	
		return currList;
	}

}
