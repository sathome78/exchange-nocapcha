package me.exrates.dao.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CurrencyDaoImpl implements CurrencyDao {

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
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("id", String.valueOf(currencyId));
		return jdbcTemplate.queryForObject(sql, namedParameters, String.class);
	}

	@Override
	public Currency findByName(String name) {
		final String sql = "SELECT * FROM CURRENCY WHERE name = :name";
		final Map<String,String> params = new HashMap<String,String>(){
			{
				put("name", name);
			}
		};
		return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Currency.class));
	}

	@Override
	public Currency findById(int id) {
		final String sql = "SELECT * FROM CURRENCY WHERE id = :id";
		final Map<String,Integer> params = new HashMap<String,Integer>(){
			{
				put("id", id);
			}
		};
		return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Currency.class));
	}

	@Override
	public List<Currency> findAllCurrencies() {
		final String sql = "SELECT * FROM CURRENCY";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Currency.class));
	}

	@Override
    public boolean updateMinWithdraw(int currencyId, BigDecimal minAmount) {
        String sql = "UPDATE CURRENCY SET min_withdraw_sum = :min_withdraw_sum WHERE id = :id";
        final Map<String,Number> params = new HashMap<String,Number>(){
            {
                put("id", currencyId);
                put("min_withdraw_sum", minAmount);
            }
        };

        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
	public List<CurrencyLimit> retrieveCurrencyLimitsForRoles(List<Integer> roleIds, OperationType operationType) {
		String sql = "SELECT DISTINCT CURRENCY_LIMIT.currency_id, CURRENCY.name, " +
				"CURRENCY_LIMIT.min_sum, CURRENCY_LIMIT.max_sum " +
				"FROM CURRENCY_LIMIT " +
				"JOIN CURRENCY ON CURRENCY_LIMIT.currency_id = CURRENCY.id " +
				"WHERE user_role_id IN(:role_ids) AND CURRENCY_LIMIT.operation_type_id = :operation_type_id";
		Map<String, Object> params = new HashMap<String, Object>() {{
			put("role_ids", roleIds);
			put("operation_type_id", operationType.getType());
		}};

		return jdbcTemplate.query(sql, params, (rs, row) -> {
			CurrencyLimit currencyLimit = new CurrencyLimit();
			Currency currency = new Currency();
			currency.setId(rs.getInt("currency_id"));
			currency.setName(rs.getString("name"));
			currencyLimit.setCurrency(currency);
			currencyLimit.setMinSum(rs.getBigDecimal("min_sum"));
			currencyLimit.setMaxSum(rs.getBigDecimal("max_sum"));
			return currencyLimit;
		});
	}

	@Override
	public BigDecimal retrieveMinLimitForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId) {
		String sql = "SELECT min_sum FROM CURRENCY_LIMIT " +
				"WHERE user_role_id = :role_id AND operation_type_id = :operation_type_id AND currency_id = :currency_id";
		Map<String, Integer> params = new HashMap<String, Integer>() {{
			put("role_id", userRole.getRole());
			put("operation_type_id", operationType.getType());
			put("currency_id", currencyId);
		}};
		return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
	}

    @Override
	public void updateCurrencyLimit(int currencyId, OperationType operationType, List<Integer> roleIds, BigDecimal minAmount) {
		String sql = "UPDATE CURRENCY_LIMIT SET min_sum = :min_sum WHERE currency_id = :currency_id " +
				"AND operation_type_id = :operation_type_id AND user_role_id IN (:role_ids)";
		final Map<String,Object> params = new HashMap<String,Object>(){
			{
				put("min_sum", minAmount);
				put("currency_id", currencyId);
				put("operation_type_id", operationType.getType());
				put("role_ids", roleIds);
			}
		};

		jdbcTemplate.update(sql, params);
	}

	@Override
		public List<CurrencyPair> getAllCurrencyPairs() {
		String sql = "SELECT id, currency1_id, currency2_id, name, \n" +
				"(select name from CURRENCY where id = currency1_id) as currency1_name,\n" +
				"(select name from CURRENCY where id = currency2_id) as currency2_name\n" +
				" FROM CURRENCY_PAIR " +
				" WHERE hidden IS NOT TRUE " +
				" ORDER BY -pair_order DESC";

		List<CurrencyPair> currencyPairList = jdbcTemplate.query(sql, (rs, row) -> {
			CurrencyPair currencyPair = new CurrencyPair();
			currencyPair.setId(rs.getInt("id"));
			currencyPair.setName(rs.getString("name"));
			/**/
			Currency currency1 = new Currency();
			currency1.setId(rs.getInt("currency1_id"));
			currency1.setName(rs.getString("currency1_name"));
			currencyPair.setCurrency1(currency1);
			/**/
			Currency currency2 = new Currency();
			currency2.setId(rs.getInt("currency2_id"));
			currency2.setName(rs.getString("currency2_name"));
			currencyPair.setCurrency2(currency2);
			/**/
			return currencyPair;

		});

		return currencyPairList;
	}

	@Override
	public CurrencyPair getCurrencyPairById(int currency1Id, int currency2Id) {
		String sql = "SELECT id, currency1_id, currency2_id, name, \n" +
				"(select name from CURRENCY where id = currency1_id) as currency1_name,\n" +
				"(select name from CURRENCY where id = currency2_id) as currency2_name\n" +
				" FROM CURRENCY_PAIR WHERE currency1_id = :currency1Id AND currency2_id = :currency2Id";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("currency1Id", String.valueOf(currency1Id));
		namedParameters.put("currency2Id", String.valueOf(currency2Id));
		return jdbcTemplate.queryForObject(sql, namedParameters,(resultSet, i) -> {
			CurrencyPair currencyPair = new CurrencyPair();
			currencyPair.setId(resultSet.getInt("id"));
			currencyPair.setName(resultSet.getString("name"));
			/**/
			Currency currency1 = new Currency();
			currency1.setId(resultSet.getInt("currency1_id"));
			currency1.setName(resultSet.getString("currency1_name"));
			currencyPair.setCurrency1(currency1);
			/**/
			Currency currency2 = new Currency();
			currency2.setId(resultSet.getInt("currency2_id"));
			currency2.setName(resultSet.getString("currency2_name"));
			currencyPair.setCurrency2(currency2);
			/**/
			return currencyPair;
		});
	}

	@Override
	public CurrencyPair findCurrencyPairById(int currencyPairId) {
		String sql = "SELECT id, currency1_id, currency2_id, name, " +
				"(select name from CURRENCY where id = currency1_id) as currency1_name, " +
				"(select name from CURRENCY where id = currency2_id) as currency2_name " +
				" FROM CURRENCY_PAIR WHERE id = :currencyPairId";
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("currencyPairId", String.valueOf(currencyPairId));
		return jdbcTemplate.queryForObject(sql, namedParameters,(resultSet, i) -> {
			CurrencyPair currencyPair = new CurrencyPair();
			currencyPair.setId(resultSet.getInt("id"));
			currencyPair.setName(resultSet.getString("name"));
			/**/
			Currency currency1 = new Currency();
			currency1.setId(resultSet.getInt("currency1_id"));
			currency1.setName(resultSet.getString("currency1_name"));
			currencyPair.setCurrency1(currency1);
			/**/
			Currency currency2 = new Currency();
			currency2.setId(resultSet.getInt("currency2_id"));
			currency2.setName(resultSet.getString("currency2_name"));
			currencyPair.setCurrency2(currency2);
			/**/
			return currencyPair;
		});
	}
}