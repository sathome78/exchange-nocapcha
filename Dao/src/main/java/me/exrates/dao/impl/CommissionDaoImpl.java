package me.exrates.dao.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CommissionDaoImpl implements CommissionDao{

	@Autowired  
	DataSource dataSource;

	@Override
	public Commission getCommission(OperationType operationType) {
		final String sql = "SELECT * FROM COMMISSION WHERE operation_type = :operationType "
				+ "order by date desc limit 1";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		final HashMap<String,Integer> params = new HashMap<>();
		params.put("operationType",operationType.type);
		return namedParameterJdbcTemplate.queryForObject(sql,params,(resultSet, i) -> {
			Commission commission = new Commission();
			commission.setDateOfChange(resultSet.getDate("date"));
			commission.setId(resultSet.getInt("id"));
			commission.setOperationType(OperationType.convert(resultSet.getInt("operation_type")));
			commission.setValue(resultSet.getDouble("value"));
			return commission;
		});
	}

	@Override
	public double getCommissionByType(OperationType type) {
		String sql = "SELECT value FROM COMMISSION WHERE operation_type = :operationType "
				+ "order by date desc limit 1";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("operationType", String.valueOf(type.type));
		return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
	}
	
}
