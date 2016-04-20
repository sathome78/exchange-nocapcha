package me.exrates.dao.impl;

import me.exrates.dao.OrderDao;
import me.exrates.jdbc.OrderRowMapper;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
    public List<ExOrder> getMyOrders(int userId) {
        String sql = "SELECT * " +
                "  FROM EXORDERS " +
                "  WHERE user_id=:user_id and (status_id = 1 or status_id = 2 or status_id = 3)" +
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
    public List<OrderListDto> getOrdersSell() {
        String sql = "SELECT id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                "  WHERE status_id = 2 and operation_type_id= 3 " +
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
    public List<OrderListDto> getOrdersBuy() {
        String sql = "SELECT id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                "  WHERE status_id = 2 and operation_type_id= 4 " +
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
    public boolean deleteOrder(int orderId) {
        String sql = "DELETE FROM EORDERS WHERE id = :id";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(orderId));
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
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
    public ExOrder getLastClosedOrder(){
        String sql = "SELECT * " +
                "  FROM EXORDERS " +
                "  WHERE status_id = satus_id" +
                "  ORDER BY date_acception DESC LIMIT 1";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(3));
        return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new OrderRowMapper());
    }

    @Override
    public ExOrder getLastClosedOrderForCurrencyPair(CurrencyPair currencyPair){
        String sql = "SELECT * " +
                "  FROM EXORDERS " +
                "  WHERE status_id = :status_id and currency_pair_id=:currency_pair_id" +
                "  ORDER BY date_acception DESC LIMIT 1";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(3));
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new OrderRowMapper());
    }

}


	

