package me.exrates.dao.impl;

import me.exrates.dao.OrderDao;
import me.exrates.jdbc.OrderRowMapper;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.vo.BackDealInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderDaoImpl implements OrderDao {

    @Autowired
    DataSource dataSource;

    public int createOrder(ExOrder exOrder) {
        String sql = "INSERT INTO EXORDERS" +
                "  (user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount, status_id)" +
                "  VALUES " +
                "  (:user_id, :currency_pair_id, :operation_type_id, :exrate, :amount_base, :amount_convert, :commission_id, :commission_fixed_amount, :status_id)";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", exOrder.getUserId())
                .addValue("currency_pair_id", exOrder.getCurrencyPairId())
                .addValue("operation_type_id", exOrder.getOperationType().getType())
                .addValue("exrate", exOrder.getExRate())
                .addValue("amount_base", exOrder.getAmountBase())
                .addValue("amount_convert", exOrder.getAmountConvert())
                .addValue("commission_id", exOrder.getComissionId())
                .addValue("commission_fixed_amount", exOrder.getCommissionFixedAmount())
                .addValue("status_id", OrderStatus.INPROCESS.getStatus());
        int result = namedParameterJdbcTemplate.update(sql, parameters, keyHolder);
        int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    @Override
    public List<ExOrder> getMyOrders(int userId, CurrencyPair currencyPair) {
        String sql = "SELECT * " +
                "  FROM EXORDERS " +
                "  WHERE user_id=:user_id and (status_id = 1 or status_id = 2 or status_id = 3)" +
                (currencyPair == null ? "" : " and EXORDERS.currency_pair_id=" + currencyPair.getId()) +
                "  ORDER BY -date_acception ASC, date_creation DESC";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("user_id", String.valueOf(userId));
        return namedParameterJdbcTemplate.query(sql, namedParameters, new OrderRowMapper());
    }

    @Override
    public List<ExOrder> getAllOpenedOrders() {
        String sql = "SELECT * " +
                "  FROM EXORDERS " +
                "  WHERE status_id = 2" +
                "  ORDER BY date_creation DESC";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(sql, new OrderRowMapper());
    }

    @Override
    public List<OrderListDto> getOrdersSell(CurrencyPair currencyPair) {
        String sql = "SELECT id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                "  WHERE status_id = 2 and operation_type_id= 3 " +
                (currencyPair == null ? "" : " and EXORDERS.currency_pair_id=" + currencyPair.getId()) +
                "  ORDER BY -exrate DESC";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(sql, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setExrate(rs.getBigDecimal("exrate"));
            order.setAmountBase(rs.getBigDecimal("amount_base"));
            order.setAmountConvert(rs.getBigDecimal("amount_convert"));
            return order;
        });
    }

    @Override
    public List<OrderListDto> getOrdersSellForCurrencyPair(CurrencyPair currencyPair) {
        String sql = "SELECT id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                "  WHERE status_id = 2 and operation_type_id= 3 and currency_pair_id=:currency_pair_id" +
                "  ORDER BY exrate DESC";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setExrate(rs.getBigDecimal("exrate"));
            order.setAmountBase(rs.getBigDecimal("amount_base"));
            order.setAmountConvert(rs.getBigDecimal("amount_convert"));
            return order;
        });
    }

    @Override
    public List<OrderListDto> getOrdersBuy(CurrencyPair currencyPair) {
        String sql = "SELECT id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                "  WHERE status_id = 2 and operation_type_id= 4 " +
                (currencyPair == null ? "" : " and EXORDERS.currency_pair_id=" + currencyPair.getId()) +
                "  ORDER BY exrate DESC";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(sql, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setExrate(rs.getBigDecimal("exrate"));
            order.setAmountBase(rs.getBigDecimal("amount_base"));
            order.setAmountConvert(rs.getBigDecimal("amount_convert"));
            return order;
        });
    }

    @Override
    public List<OrderListDto> getOrdersBuyForCurrencyPair(CurrencyPair currencyPair) {
        String sql = "SELECT id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                "  WHERE status_id = 2 and operation_type_id= 4 and currency_pair_id=:currency_pair_id" +
                "  ORDER BY exrate DESC";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setExrate(rs.getBigDecimal("exrate"));
            order.setAmountBase(rs.getBigDecimal("amount_base"));
            order.setAmountConvert(rs.getBigDecimal("amount_convert"));
            return order;
        });
    }

    @Override
    public ExOrder getOrderById(int orderId) {
        String sql = "SELECT * FROM EXORDERS WHERE id = :id";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(orderId));
        return namedParameterJdbcTemplate.query(sql, namedParameters, new OrderRowMapper()).get(0);
    }

    @Override
    public boolean setStatus(int orderId, OrderStatus status) {
        String sql = "UPDATE EXORDERS SET status_id=:status_id WHERE id = :id";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(status.getStatus()));
        namedParameters.put("id", String.valueOf(orderId));
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public boolean updateOrder(ExOrder exOrder) {
        String sql = "update EXORDERS set user_acceptor_id=:user_acceptor_id, status_id=:status_id, date_acception=:date_acception  where id = :id";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("user_acceptor_id", String.valueOf(exOrder.getUserAcceptorId()));
        namedParameters.put("status_id", String.valueOf(exOrder.getStatus().getStatus()));
        namedParameters.put("date_acception", String.valueOf(exOrder.getDateAcception()));
        namedParameters.put("id", String.valueOf(exOrder.getId()));
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public List<Map<String, Object>> getDataForChart(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        String sql = "SELECT date_acception, exrate FROM EXORDERS " +
                " WHERE status_id=:status_id AND currency_pair_id=:currency_pair_id " +
                " AND date_acception >= now() - INTERVAL " + backDealInterval.intervalValue.toString() + " " + backDealInterval.intervalType +
                " ORDER BY date_acception";

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(3));
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("dateAcception", rs.getTimestamp("date_acception"));
            map.put("exrate", rs.getBigDecimal("exrate"));
            return map;
        });

        return rows;
    }

    @Override
    public ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        String sql = "SELECT FIRSTORDER.amount_base AS first_amount_base, FIRSTORDER.exrate AS first_exrate," +
                "            LASTORDER.amount_base AS last_amount_base, LASTORDER.exrate AS last_exrate," +
                "            AGRIGATE.* " +
                "     FROM  " +
                "       (SELECT MIN(EXORDERS.date_acception) AS first_date_acception, MAX(EXORDERS.date_acception) AS last_date_acception,  " +
                "       MIN(EXORDERS.exrate) AS min_exrate, MAX(EXORDERS.exrate) AS max_exrate,  " +
                "       SUM(EXORDERS.amount_base) AS deal_sum_base, SUM(EXORDERS.amount_convert) AS deal_sum_convert  " +
                "       FROM EXORDERS  " +
                "       WHERE   " +
                "       EXORDERS.currency_pair_id = :currency_pair_id AND EXORDERS.status_id = :status_id AND   " +
                "       EXORDERS.date_acception >= now() - INTERVAL " + backDealInterval.intervalValue.toString() + " " + backDealInterval.intervalType +
                "       ) AGRIGATE " +
                "     JOIN EXORDERS FIRSTORDER ON (FIRSTORDER.date_acception = AGRIGATE.first_date_acception)  " +
                "     JOIN EXORDERS LASTORDER ON (LASTORDER.date_acception = AGRIGATE.last_date_acception)";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(3));
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<ExOrderStatisticsDto>(){
                @Override
                public ExOrderStatisticsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExOrderStatisticsDto exOrderStatisticsDto = new ExOrderStatisticsDto(currencyPair);
                    exOrderStatisticsDto.setFirstOrderAmountBase(rs.getBigDecimal("first_amount_base"));
                    exOrderStatisticsDto.setFirstOrderRate(rs.getBigDecimal("first_exrate"));
                    exOrderStatisticsDto.setLastOrderAmountBase(rs.getBigDecimal("last_amount_base"));
                    exOrderStatisticsDto.setLastOrderRate(rs.getBigDecimal("last_exrate"));
                    exOrderStatisticsDto.setMinRate(rs.getBigDecimal("min_exrate"));
                    exOrderStatisticsDto.setMaxRate(rs.getBigDecimal("max_exrate"));
                    exOrderStatisticsDto.setSumBase(rs.getBigDecimal("deal_sum_base"));
                    exOrderStatisticsDto.setSumConvert(rs.getBigDecimal("deal_sum_convert"));
                    return exOrderStatisticsDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return new ExOrderStatisticsDto(currencyPair);
        }
    }


}


	

