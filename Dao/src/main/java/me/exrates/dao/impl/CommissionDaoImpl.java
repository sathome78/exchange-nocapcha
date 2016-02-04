package me.exrates.dao.impl;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import me.exrates.dao.CommissionDao;
import me.exrates.model.enums.OperationType;

@Repository
public class CommissionDaoImpl implements CommissionDao{

	@Autowired  
	DataSource dataSource;
	
	@Override
	public double getCommissionByType(OperationType type) {
		String sql = "SELECT value FROM commission WHERE operation_type = :operationType "
				+ "order by date desc limit 1";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("operationType", String.valueOf(type.type));
		return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
	}
	
}
