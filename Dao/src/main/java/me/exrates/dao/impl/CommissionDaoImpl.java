package me.exrates.dao.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CommissionDaoImpl implements CommissionDao {

	private static final RowMapper<Commission> commissionRowMapper = (resultSet, i) -> {
		Commission commission = new Commission();
		commission.setDateOfChange(resultSet.getDate("date"));
		commission.setId(resultSet.getInt("id"));
		commission.setOperationType(OperationType.convert(resultSet.getInt("operation_type")));
		commission.setValue(resultSet.getBigDecimal("value"));
		return commission;
	};

	@Autowired  
	NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public Commission getCommission(OperationType operationType, UserRole userRole) {
		final String sql = "SELECT COMMISSION.id, COMMISSION.operation_type, COMMISSION.date, COMMISSION.editable, " +
				"  IFNULL(COMMISSION_USER_ROLE.value, COMMISSION.default_value) AS value " +
				"FROM commission " +
				"LEFT JOIN COMMISSION_USER_ROLE ON COMMISSION_USER_ROLE.commission_id = COMMISSION.id " +
				"WHERE COMMISSION.operation_type = :operation_type AND (COMMISSION_USER_ROLE.user_role_id = :role_id " +
				"                                                       OR COMMISSION_USER_ROLE.user_role_id IS NULL);";
		final HashMap<String,Integer> params = new HashMap<>();
		params.put("operation_type",operationType.type);
		params.put("role_id", userRole.getRole());
		return jdbcTemplate.queryForObject(sql,params, commissionRowMapper);
	}

	@Override
	public Commission getDefaultCommission(OperationType operationType) {
		final String sql = "SELECT id, operation_type, date, editable, default_value AS value " +
				"FROM COMMISSION " +
				"WHERE operation_type = :operation_type;";
		final HashMap<String,Integer> params = new HashMap<>();
		params.put("operation_type",operationType.type);
		return jdbcTemplate.queryForObject(sql,params,commissionRowMapper);
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
		return jdbcTemplate.query(sql, commissionRowMapper);
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
