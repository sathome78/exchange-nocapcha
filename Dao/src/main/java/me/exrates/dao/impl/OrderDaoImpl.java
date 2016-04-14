package me.exrates.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import me.exrates.model.User;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.enums.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import me.exrates.dao.OrderDao;
import me.exrates.jdbc.OrderRowMapper;
import me.exrates.model.Currency;
import me.exrates.model.Order;
import me.exrates.model.Wallet;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;

import org.springframework.stereotype.Repository;

@Repository
public class OrderDaoImpl implements OrderDao {

    //private static final Logger logger=Logger.getLogger(OrderDaoImpl.class);
    @Autowired
    DataSource dataSource;

    public int createOrder(Order order) {

        String sql = "INSERT INTO ORDERS"
                + "(wallet_id_sell,currency_sell, amount_sell, commission_amount_sell, "
                + "wallet_id_buy,currency_buy,amount_buy, commission_amount_buy, operation_type, exrate) "
                + "values"
                + "(:walletSell,:currencySell,:amountSell,:commissionAmountSell,"
                + ":walletBuy,:currencyBuy,:amountBuy,:commissionAmountBuy, :operationType, :exrate)";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("walletSell", order.getWalletIdSell())
                .addValue("amountSell", order.getAmountSell())
                .addValue("currencySell", order.getCurrencySell())
                .addValue("commissionAmountSell", order.getCommissionAmountSell())
                .addValue("commissionAmountBuy", order.getCommissionAmountBuy())
                .addValue("walletBuy", order.getWalletIdBuy())
                .addValue("currencyBuy", order.getCurrencyBuy())
                .addValue("amountBuy", order.getAmountBuy())
                .addValue("exrate", order.getExrate())
                .addValue("operationType", order.getOperationType().type);
        int result = namedParameterJdbcTemplate.update(sql, parameters, keyHolder);
        int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    @Override
    public List<Order> getMyOrders(int userId) {
        String sql = "select * from ORDERS where "
                + "(wallet_id_sell in (select id from WALLET where user_id=:user_id) ||"
                + "wallet_id_buy in (select id from WALLET where user_id=:user_id))"
                + "&& (status=1 || status=2 || status=3)";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("user_id", String.valueOf(userId));
        List<Order> orderList = new ArrayList<Order>();
        orderList = namedParameterJdbcTemplate.query(sql, namedParameters, new OrderRowMapper());
        return orderList;
    }

    @Override
    public List<Order> getAllOrders() {
        String sql = "select * from ORDERS where status = 2";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        List<Order> orderList = new ArrayList<Order>();
        orderList = jdbcTemplate.query(sql, new OrderRowMapper());
        return orderList;
    }

    @Override
    public List<OrderListDto> getOrdersSell() {
        String sql = "select id, exrate, amount_sell, amount_buy from ORDERS where status = 2 and operation_type= 3 order by -exrate DESC";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(sql, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setExrate(rs.getBigDecimal("exrate"));
            order.setCurrencyAmount1(rs.getBigDecimal("amount_sell"));
            order.setCurrencyAmount2(rs.getBigDecimal("amount_buy"));
            return order;
        });
    }

    @Override
    public List<OrderListDto> getOrdersBuy() {
        String sql = "select id, exrate, amount_sell, amount_buy from ORDERS where status = 2 and operation_type= 4 order by exrate DESC";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(sql, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setExrate(rs.getBigDecimal("exrate"));
            order.setCurrencyAmount1(rs.getBigDecimal("amount_buy"));
            order.setCurrencyAmount2(rs.getBigDecimal("amount_sell"));
            return order;
        });
    }

    @Override
    public boolean deleteOrder(int orderId) {
        String sql = "delete from ORDERS where id = :id";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("id", String.valueOf(orderId));
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        if (result > 0) {
            return true;
        } else return false;

    }

    @Override
    public Order getOrderById(int orderId) {
        String sql = "select * from ORDERS where id = :id";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("id", String.valueOf(orderId));
        List<Order> orderList = new ArrayList<Order>();
        orderList = namedParameterJdbcTemplate.query(sql, namedParameters, new OrderRowMapper());
        Order order = orderList.get(0);
        return order;
    }

    @Override
    public boolean setStatus(int orderId, OrderStatus status) {
        String sql = "update ORDERS set status=:status where id = :id";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("status", String.valueOf(status.getStatus()));
        namedParameters.put("id", String.valueOf(orderId));
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        if (result > 0) {
            return true;
        } else return false;

    }

    @Override
    public boolean updateOrder(Order order) {
        String sql = "update ORDERS set wallet_id_buy=:walletBuy, status=:status, date_final=:dateFinal  where id = :id";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<String, String>();
        namedParameters.put("walletBuy", String.valueOf(order.getWalletIdBuy()));
        namedParameters.put("status", String.valueOf(order.getStatus().getStatus()));
        namedParameters.put("dateFinal", String.valueOf(order.getDateFinal()));
        namedParameters.put("id", String.valueOf(order.getId()));
        System.out.println("ststus = " + order.getStatus().getStatus());
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        if (result > 0) {
            return true;
        } else return false;
    }

}


	

