package me.exrates.impl;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import me.exrates.daos.CommissionDao;

public class CommissionDaoImpl implements CommissionDao{

	@Autowired  
	DataSource dataSource;
	
	@Override
	public double getCommissionByType(String operationType) {
		String sql = "SELECT value FROM commission WHERE operation_type = :operationType";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("operationType", operationType);
		double value = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
		return value;
	}
	
}
