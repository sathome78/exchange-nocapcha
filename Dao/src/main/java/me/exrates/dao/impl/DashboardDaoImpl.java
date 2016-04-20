package me.exrates.dao.impl;

import me.exrates.dao.DashboardDao;
import me.exrates.jdbc.OrderRowMapper;
import me.exrates.model.CurrencyPair;
import me.exrates.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DashboardDaoImpl implements DashboardDao {

    @Autowired
    DataSource dataSource;

    @Override
    public List<Map<String, Object>> getDataForChart(CurrencyPair currencyPair) {
        String sql = "SELECT date_final, " +
                "CASE WHEN currency_buy = :currency_buy AND currency_sell = :currency_sell THEN\n" +
                "\t(amount_buy/amount_sell)\n" +
                "    ELSE CASE WHEN currency_buy = :currency_sell AND currency_sell = :currency_buy THEN\n" +
                "    (amount_sell/amount_buy) END END as amount,\n" +
                "CASE WHEN currency_buy = :currency_buy AND currency_sell = :currency_sell THEN\n" +
                "\tamount_sell\n" +
                "    ELSE CASE WHEN currency_buy = :currency_sell AND currency_sell = :currency_buy THEN\n" +
                "    amount_buy END END as amount_sell FROM ORDERS WHERE date_final IS NOT NULL order by date_final limit 12;";

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("currency_sell", String.valueOf(currencyPair.getCurrency1().getId()));
        namedParameters.put("currency_buy", String.valueOf(currencyPair.getCurrency2().getId()));
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("date_final", rs.getTimestamp("date_final"));
            map.put("amount", rs.getBigDecimal("amount"));
            map.put("amount_sell", rs.getBigDecimal("amount_sell"));
            return map;
        });

        return rows;
    }

    private BigDecimal getSumOrdersByCurrency(int currencyId) {
        String sql = "SELECT sum(amount_buy) FROM ORDERS;";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();

        return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class);
    }

    @Override
    public BigDecimal getBalanceByCurrency(int userId, int currencyId) {
        String sql = "SELECT active_balance FROM WALLET WHERE user_id = :userId AND currency_id = :currencyId;";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("userId", String.valueOf(userId));
        namedParameters.put("currencyId", String.valueOf(currencyId));

        BigDecimal value = new BigDecimal(0.0);
        try {
            value = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        return value;
    }

}
