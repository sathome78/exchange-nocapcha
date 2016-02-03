package me.exrates.dao.impl;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import me.exrates.dao.CommissionDao;
<<<<<<< HEAD
import me.exrates.model.enums.OperationType;

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
		double value = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
		return value;
	}
	
}
=======
import org.springframework.stereotype.Repository;

@Repository
public class CommissionDaoImpl implements CommissionDao{

	@Autowired
	DataSource dataSource;

	@Override
	public double getCommissionByType(int operationType) {
		String sql = "SELECT value FROM commission WHERE operation_type = :operationType "
				+ "order by date desc limit 1";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("operationType", String.valueOf(operationType));
		return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
	}

}
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
