package me.exrates.dao.impl;

import me.exrates.dao.OrderDao;
import me.exrates.jdbc.OrderRowMapper;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.vo.BackDealInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
    public List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
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
    public List<CandleChartItemDto> getDataForCandleChart(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        String s = "{call GET_DATA_FOR_CANDLE(NOW(), " + backDealInterval.intervalValue + ", '" + backDealInterval.intervalType.name() + "', " + currencyPair.getId() + ")}";
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        List<CandleChartItemDto> result = jdbcTemplate.execute(s, new PreparedStatementCallback<List<CandleChartItemDto>>() {
            @Override
            public List<CandleChartItemDto> doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
                ResultSet rs = ps.executeQuery();
                List<CandleChartItemDto> list = new ArrayList();
                while (rs.next()) {
                    CandleChartItemDto candleChartItemDto = new CandleChartItemDto();
                    candleChartItemDto.setBeginPeriod(rs.getTimestamp("pred_point").toLocalDateTime());
                    candleChartItemDto.setEndPeriod(rs.getTimestamp("current_point").toLocalDateTime());
                    candleChartItemDto.setOpenRate(rs.getBigDecimal("open_rate"));
                    candleChartItemDto.setCloseRate(rs.getBigDecimal("close_rate"));
                    candleChartItemDto.setLowRate(rs.getBigDecimal("low_rate"));
                    candleChartItemDto.setHighRate(rs.getBigDecimal("high_rate"));
                    candleChartItemDto.setBaseVolume(rs.getBigDecimal("base_volume"));
                    list.add(candleChartItemDto);
                }
                rs.close();
                return list;
            }
        });
        return result;
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
                "     JOIN EXORDERS LASTORDER ON (LASTORDER.date_acception = AGRIGATE.last_date_acception)" +
                "  ORDER BY FIRSTORDER.id ASC, LASTORDER.id DESC LIMIT 1";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(3));
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<ExOrderStatisticsDto>() {
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

    @Override
    public List<CoinmarketApiDto> getCoinmarketData(String currencyPairName, BackDealInterval backDealInterval){
        String sql = "SELECT " +
          "    CURRENCY_PAIR.name AS currency_pair_name, " +
          "    AGRIGATE.first AS FIRST, " +
          "    AGRIGATE.last AS last, " +
          "    MIN(LOWESTASKORDER.exrate) AS lowestAsk, " +
          "    MAX(HIGHESTBIDCORDER.exrate) AS highestBid, " +
          "    LAST/FIRST*100-100 AS percentChange, " +
          "    AGRIGATE.baseVolume AS baseVolume, " +
          "    0 as quoteVolume, " +
          "    0 as isFrozen, " +
          "    MAX(HIGH24ORDER.exrate) AS high24hr, " +
          "    MIN(LOW24ORDER.exrate) AS low24hr " +
          " FROM " +
          "    (SELECT " +
          "        EO.currency_pair_id, EO.status_id, " +
          "        MIN(EO.date_acception) AS first_date_acception, " +
          "        MAX(EO.date_acception) AS last_date_acception, " +
          "        SUM(EO.amount_base) AS baseVolume, " +
          "        ( " +
          "        SELECT FIRSTORDER.exrate FROM EXORDERS FIRSTORDER WHERE " +
          "                                    (FIRSTORDER.date_acception = MIN(EO.date_acception)) AND " +
          "                                    (FIRSTORDER.currency_pair_id=EO.currency_pair_id) AND " +
          "                                    (FIRSTORDER.status_id=EO.status_id) " +
          "                                    ORDER BY FIRSTORDER.id ASC LIMIT 1 " +
          "        ) AS first, " +
          "        ( " +
          "        SELECT LASTORDER.exrate FROM EXORDERS LASTORDER WHERE " +
          "                                    (LASTORDER.date_acception = MAX(EO.date_acception)) AND " +
          "                                    (LASTORDER.currency_pair_id=EO.currency_pair_id) AND " +
          "                                    (LASTORDER.status_id=EO.status_id) " +
          "                                    ORDER BY LASTORDER.id DESC LIMIT 1 " +
          "        ) AS LAST " +
          "    FROM EXORDERS  EO " +
          "    WHERE " +
                (currencyPairName!=null && !"".equals(currencyPairName) ?
                        "EO.currency_pair_id=(SELECT CURRENCY_PAIR.id FROM CURRENCY_PAIR WHERE CURRENCY_PAIR.name = '"+currencyPairName+"') AND" :
                        "") +
          "        EO.status_id = :closed_status_id AND " +
          "        EO.date_acception >= now() - INTERVAL "+backDealInterval.intervalValue.toString() + " " + backDealInterval.intervalType +
          "    GROUP BY EO.currency_pair_id, EO.status_id) " +
          "    AGRIGATE " +
          "    JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = AGRIGATE.currency_pair_id) " +
          "    LEFT JOIN EXORDERS LOWESTASKORDER ON (LOWESTASKORDER.date_acception >= AGRIGATE.first_date_acception) AND " +
          "                                    (LOWESTASKORDER.currency_pair_id=AGRIGATE.currency_pair_id) AND " +
          "                                    (LOWESTASKORDER.status_id=AGRIGATE.status_id) AND " +
          "                                    (LOWESTASKORDER.operation_type_id=:sell_operation_type) " +
          "    LEFT JOIN EXORDERS HIGHESTBIDCORDER ON (HIGHESTBIDCORDER.date_acception >= AGRIGATE.first_date_acception) AND " +
          "                                    (HIGHESTBIDCORDER.currency_pair_id=AGRIGATE.currency_pair_id) AND " +
          "                                    (HIGHESTBIDCORDER.status_id=AGRIGATE.status_id) AND " +
          "                                    (HIGHESTBIDCORDER.operation_type_id=:buy_operation_type) " +
          "    LEFT JOIN EXORDERS LOW24ORDER ON (LOW24ORDER.date_acception >= now() - INTERVAL 24 HOUR) AND " +
          "                                    (LOW24ORDER.currency_pair_id=AGRIGATE.currency_pair_id) AND " +
          "                                    (LOW24ORDER.status_id=AGRIGATE.status_id) " +
          "    LEFT JOIN EXORDERS HIGH24ORDER ON (HIGH24ORDER.date_acception >= now() - INTERVAL 24 HOUR) AND " +
          "                                    (HIGH24ORDER.currency_pair_id=AGRIGATE.currency_pair_id) AND " +
          "                                    (HIGH24ORDER.status_id=AGRIGATE.status_id) " +
          " GROUP BY currency_pair_name, first, LAST, baseVolume";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("closed_status_id", String.valueOf(OrderStatus.CLOSED.getStatus()));
        namedParameters.put("sell_operation_type", String.valueOf(OperationType.SELL.getType()));
        namedParameters.put("buy_operation_type", String.valueOf(OperationType.BUY.getType()));
        try {
            return namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<CoinmarketApiDto>() {
                @Override
                public CoinmarketApiDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    CoinmarketApiDto coinmarketApiDto = new CoinmarketApiDto();
                    coinmarketApiDto.setCurrency_pair_name(rs.getString("currency_pair_name"));
                    coinmarketApiDto.setFirst(rs.getBigDecimal("first"));
                    coinmarketApiDto.setLast(rs.getBigDecimal("last"));
                    coinmarketApiDto.setLowestAsk(rs.getBigDecimal("lowestAsk"));
                    coinmarketApiDto.setHighestBid(rs.getBigDecimal("highestBid"));
                    coinmarketApiDto.setPercentChange(rs.getBigDecimal("percentChange"));
                    coinmarketApiDto.setBaseVolume(rs.getBigDecimal("baseVolume"));
                    coinmarketApiDto.setQuoteVolume(rs.getBigDecimal("quoteVolume"));
                    coinmarketApiDto.setIsFrozen(rs.getInt("isFrozen"));
                    coinmarketApiDto.setHigh24hr(rs.getBigDecimal("high24hr"));
                    coinmarketApiDto.setLow24hr(rs.getBigDecimal("low24hr"));
                    return coinmarketApiDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

}


	

