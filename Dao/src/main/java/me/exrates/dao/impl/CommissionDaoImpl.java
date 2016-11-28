package me.exrates.dao.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommissionDaoImpl implements CommissionDao{

	@Autowired  
	NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public Commission getCommission(OperationType operationType) {
		final String sql = "SELECT * FROM COMMISSION WHERE operation_type = :operationType "
				+ "order by date desc limit 1";
		final HashMap<String,Integer> params = new HashMap<>();
		params.put("operationType",operationType.type);
		return jdbcTemplate.queryForObject(sql,params,(resultSet, i) -> {
			Commission commission = new Commission();
			commission.setDateOfChange(resultSet.getDate("date"));
			commission.setId(resultSet.getInt("id"));
			commission.setOperationType(OperationType.convert(resultSet.getInt("operation_type")));
			commission.setValue(resultSet.getBigDecimal("value"));
			return commission;
		});
	}

	@Override
	public BigDecimal getCommissionMerchant(String merchant, String currency) {
		final String sql = "SELECT merchant_commission FROM birzha.MERCHANT_CURRENCY " +
				"where merchant_id = (select id from MERCHANT where name = :merchant) \n" +
				"and currency_id = (select id from CURRENCY where name = :currency)";
		final HashMap<String, String> params = new HashMap<>();
		params.put("currency", currency);
		params.put("merchant", merchant);

		return BigDecimal.valueOf(jdbcTemplate.queryForObject(sql, params, Double.class));
	}

	@Override
	public List<Commission> getEditableCommissions() {
		final String sql = "SELECT id, operation_type, value, date " +
				"FROM COMMISSION WHERE editable = 1 " +
				"ORDER BY id";
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			Commission commission = new Commission();
			commission.setId(rs.getInt("id"));
			commission.setOperationType(OperationType.convert(rs.getInt("operation_type")));
			commission.setValue(rs.getBigDecimal("value"));
			commission.setDateOfChange(rs.getTimestamp("date"));
			return commission;
		});
	}

	@Override
	public void updateCommission(Integer id, BigDecimal value) {
		final String sql = "UPDATE COMMISSION SET value = :value, date = NOW() where id = :id";
		Map<String, Number> params = new HashMap<String, Number>() {{
			put("id", id);
			put("value", value);
		}};
		jdbcTemplate.update(sql, params);
	}

	@Override
	public void updateMerchantCurrencyCommission(Integer merchantId, Integer currencyId, BigDecimal value){
		final String sql = "UPDATE MERCHANT_CURRENCY SET merchant_commission = :value " +
				"where merchant_id = :merchant_id AND currency_id = :currency_id";
		Map<String, Number> params = new HashMap<String, Number>() {{
			put("merchant_id", merchantId);
			put("currency_id", currencyId);
			put("value", value);
		}};
		jdbcTemplate.update(sql, params);
	}


}
