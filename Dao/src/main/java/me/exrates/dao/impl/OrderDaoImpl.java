package me.exrates.dao.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.WalletDao;
import me.exrates.jdbc.OrderRowMapper;
import me.exrates.model.Commission;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.*;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.*;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.WalletOperationData;
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
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class OrderDaoImpl implements OrderDao {

    @Autowired
    DataSource dataSource;

    @Autowired
    CommissionDao commissionDao;

    @Autowired
    WalletDao walletDao;

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
    public List<OrderListDto> getOrdersSellForCurrencyPair(CurrencyPair currencyPair, String email, Locale locale) {
        String sql = "SELECT EXORDERS.id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                (email == null || email.isEmpty() ? "" : " JOIN USER ON (USER.id=EXORDERS.user_id)  AND (USER.email != '" + email + "') ") +
                "  WHERE status_id = 2 and operation_type_id= 3 and currency_pair_id=:currency_pair_id" +
                "  ORDER BY exrate ASC";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPair.getId());
        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setOrderType(OperationType.convert(rs.getInt("operation_type_id")));
            order.setExrate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
            order.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, true));
            order.setAmountConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_convert"), locale, true));
            return order;
        });
    }

    @Override
    public List<OrderListDto> getOrdersBuyForCurrencyPair(CurrencyPair currencyPair, String email, Locale locale) {
        String sql = "SELECT EXORDERS.id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                (email == null || email.isEmpty() ? "" : " JOIN USER ON (USER.id=EXORDERS.user_id)  AND (USER.email != '" + email + "') ") +
                "  WHERE status_id = 2 and operation_type_id= 4 and currency_pair_id=:currency_pair_id" +
                "  ORDER BY exrate DESC";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setOrderType(OperationType.convert(rs.getInt("operation_type_id")));
            order.setExrate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
            order.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, true));
            order.setAmountConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_convert"), locale, true));
            return order;
        });
    }

    @Override
    public ExOrder getOrderById(int orderId) {
        String sql = "SELECT * FROM EXORDERS WHERE id = :id";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(orderId));
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new OrderRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
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
        String sql = "update EXORDERS set user_acceptor_id=:user_acceptor_id, status_id=:status_id, " +
                " date_acception=NOW()  " +
                " where id = :id";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("user_acceptor_id", String.valueOf(exOrder.getUserAcceptorId()));
        namedParameters.put("status_id", String.valueOf(exOrder.getStatus().getStatus()));
//        namedParameters.put("date_acception", String.valueOf(exOrder.getDateAcception()));
        namedParameters.put("id", String.valueOf(exOrder.getId()));
        int result = namedParameterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public List<Map<String, Object>> getDataForAreaChart(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        String sql = "SELECT date_acception, exrate, amount_base FROM EXORDERS " +
                " WHERE status_id=:status_id AND currency_pair_id=:currency_pair_id " +
                " AND date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                " ORDER BY date_acception";

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(3));
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("dateAcception", rs.getTimestamp("date_acception"));
            map.put("exrate", rs.getBigDecimal("exrate"));
            map.put("volume", rs.getBigDecimal("amount_base"));
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
                    candleChartItemDto.setBeginDate(rs.getTimestamp("pred_point"));
                    candleChartItemDto.setBeginPeriod(rs.getTimestamp("pred_point").toLocalDateTime());
                    candleChartItemDto.setEndDate(rs.getTimestamp("current_point"));
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
    public ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval, Locale locale) {
        String sql = "SELECT FIRSTORDER.amount_base AS first_amount_base, FIRSTORDER.exrate AS first_exrate," +
                "            LASTORDER.amount_base AS last_amount_base, LASTORDER.exrate AS last_exrate," +
                "            AGRIGATE.* " +
                "     FROM  " +
                "       (SELECT EXORDERS.currency_pair_id AS currency_pair_id," +
                "       MIN(EXORDERS.date_acception) AS first_date_acception, MAX(EXORDERS.date_acception) AS last_date_acception,  " +
                "       MIN(EXORDERS.exrate) AS min_exrate, MAX(EXORDERS.exrate) AS max_exrate,  " +
                "       SUM(EXORDERS.amount_base) AS deal_sum_base, SUM(EXORDERS.amount_convert) AS deal_sum_convert  " +
                "       FROM EXORDERS  " +
                "       WHERE   " +
                "       EXORDERS.currency_pair_id = :currency_pair_id AND EXORDERS.status_id = :status_id AND   " +
                "       EXORDERS.date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                "       ) AGRIGATE " +
                "     LEFT JOIN EXORDERS FIRSTORDER ON (FIRSTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND (FIRSTORDER.date_acception = AGRIGATE.first_date_acception)  " +
                "     LEFT JOIN EXORDERS LASTORDER ON (LASTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND (LASTORDER.date_acception = AGRIGATE.last_date_acception)" +
                " ORDER BY FIRSTORDER.id ASC, LASTORDER.id DESC LIMIT 1 ";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(3));
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<ExOrderStatisticsDto>() {
                @Override
                public ExOrderStatisticsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ExOrderStatisticsDto exOrderStatisticsDto = new ExOrderStatisticsDto(currencyPair);
                    exOrderStatisticsDto.setFirstOrderAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("first_amount_base"), locale, true));
                    exOrderStatisticsDto.setFirstOrderRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("first_exrate"), locale, true));
                    exOrderStatisticsDto.setLastOrderAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("last_amount_base"), locale, true));
                    exOrderStatisticsDto.setLastOrderRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("last_exrate"), locale, true));
                    exOrderStatisticsDto.setMinRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("min_exrate"), locale, true));
                    exOrderStatisticsDto.setMaxRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("max_exrate"), locale, true));
                    exOrderStatisticsDto.setSumBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("deal_sum_base"), locale, true));
                    exOrderStatisticsDto.setSumConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("deal_sum_convert"), locale, true));
                    return exOrderStatisticsDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return new ExOrderStatisticsDto(currencyPair);
        }
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getOrderStatisticByPairs(Locale locale) {
        String sql = "SELECT  " +
                "   CURRENCY_PAIR.name AS currency_pair_name,       " +
                "   (SELECT LASTORDER.exrate " +
                "       FROM EXORDERS LASTORDER  " +
                "       WHERE  " +
                "       (LASTORDER.currency_pair_id =AGRIGATE.currency_pair_id)  AND  " +
                "       (LASTORDER.status_id =AGRIGATE.status_id) " +
                "       ORDER BY LASTORDER.date_acception DESC, LASTORDER.id DESC " +
                "       LIMIT 1) AS last_exrate, " +
                "   (SELECT PRED_LASTORDER.exrate " +
                "       FROM EXORDERS PRED_LASTORDER  " +
                "       WHERE  " +
                "       (PRED_LASTORDER.currency_pair_id =AGRIGATE.currency_pair_id)  AND  " +
                "       (PRED_LASTORDER.status_id =AGRIGATE.status_id) " +
                "       ORDER BY PRED_LASTORDER.date_acception DESC, PRED_LASTORDER.id DESC " +
                "       LIMIT 1,1) AS pred_last_exrate " +
                " FROM ( " +
                "   SELECT " +
                "   EXORDERS.status_id AS status_id,  " +
                "   EXORDERS.currency_pair_id AS currency_pair_id " +
                "   FROM EXORDERS          " +
                "   WHERE EXORDERS.status_id = :status_id         " +
                "   GROUP BY currency_pair_id          " +
                "   ) " +
                " AGRIGATE " +
                " JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = AGRIGATE.currency_pair_id) AND (CURRENCY_PAIR.hidden IS NOT TRUE)"+
                " ORDER BY -CURRENCY_PAIR.pair_order DESC ";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(3));
        return namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<ExOrderStatisticsShortByPairsDto>() {
            @Override
            public ExOrderStatisticsShortByPairsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                ExOrderStatisticsShortByPairsDto exOrderStatisticsDto = new ExOrderStatisticsShortByPairsDto();
                exOrderStatisticsDto.setCurrencyPairName(rs.getString("currency_pair_name"));
                exOrderStatisticsDto.setLastOrderRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("last_exrate"), locale, true));
                exOrderStatisticsDto.setPredLastOrderRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("pred_last_exrate"), locale, true));
                return exOrderStatisticsDto;
            }
        });
    }

    @Override
    public List<CoinmarketApiDto> getCoinmarketData(String currencyPairName, BackDealInterval backDealInterval) {
        String sql = "SELECT " +
                "    CURRENCY_PAIR.name AS currency_pair_name, " +
                "    AGRIGATE.first AS FIRST, " +
                "    AGRIGATE.last AS last, " +
                "    MIN(LOWESTASKORDER.exrate) AS lowestAsk, " +
                "    MAX(HIGHESTBIDCORDER.exrate) AS highestBid, " +
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
                (currencyPairName != null && !"".equals(currencyPairName) ?
                        "EO.currency_pair_id=(SELECT CURRENCY_PAIR.id FROM CURRENCY_PAIR WHERE CURRENCY_PAIR.name = '" + currencyPairName + "') AND" :
                        "") +
                "        EO.status_id = :closed_status_id AND " +
                "        EO.date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                "    GROUP BY EO.currency_pair_id, EO.status_id) " +
                "    AGRIGATE " +
                "    JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = AGRIGATE.currency_pair_id) AND (CURRENCY_PAIR.hidden IS NOT TRUE) " +
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
                    coinmarketApiDto.setPercentChange(BigDecimalProcessing.doAction(coinmarketApiDto.getFirst(), coinmarketApiDto.getLast(), ActionType.PERCENT_GROWTH));
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

    @Override
    public OrderInfoDto getOrderInfo(int orderId, Locale locale) {
        String sql =
                " SELECT  " +
                        "     EXORDERS.id, EXORDERS.date_creation, EXORDERS.date_acception,  " +
                        "     ORDER_STATUS.name AS order_status_name,  " +
                        "     CURRENCY_PAIR.name as currency_pair_name,  " +
                        "     UPPER(ORDER_OPERATION.name) AS order_type_name,  " +
                        "     EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.amount_convert, " +
                        "     ORDER_CURRENCY_BASE.name as currency_base_name, ORDER_CURRENCY_CONVERT.name as currency_convert_name, " +
                        "     CREATOR.email AS order_creator_email, " +
                        "     ACCEPTOR.email AS order_acceptor_email, " +
                        "     COUNT(TRANSACTION.id) AS transaction_count,  " +
                        "     SUM(TRANSACTION.commission_amount) AS company_commission " +
                        " FROM EXORDERS " +
                        "      JOIN ORDER_STATUS ON (ORDER_STATUS.id = EXORDERS.status_id) " +
                        "      JOIN OPERATION_TYPE AS ORDER_OPERATION ON (ORDER_OPERATION.id = EXORDERS.operation_type_id) " +
                        "      JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                        "      JOIN CURRENCY ORDER_CURRENCY_BASE ON (ORDER_CURRENCY_BASE.id = CURRENCY_PAIR.currency1_id)   " +
                        "      JOIN CURRENCY ORDER_CURRENCY_CONVERT ON (ORDER_CURRENCY_CONVERT.id = CURRENCY_PAIR.currency2_id)  " +
                        "      JOIN WALLET ORDER_CREATOR_RESERVED_WALLET ON  " +
                        "              (ORDER_CREATOR_RESERVED_WALLET.user_id=EXORDERS.user_id) AND  " +
                        "              ( " +
                        "                  (upper(ORDER_OPERATION.name)='BUY' AND ORDER_CREATOR_RESERVED_WALLET.currency_id = CURRENCY_PAIR.currency2_id)  " +
                        "                  OR  " +
                        "                  (upper(ORDER_OPERATION.name)='SELL' AND ORDER_CREATOR_RESERVED_WALLET.currency_id = CURRENCY_PAIR.currency1_id) " +
                        "              ) " +
                        "      JOIN USER CREATOR ON (CREATOR.id = EXORDERS.user_id) " +
                        "      LEFT JOIN USER ACCEPTOR ON (ACCEPTOR.id = EXORDERS.user_acceptor_id) " +
                        "      LEFT JOIN TRANSACTION ON (TRANSACTION.source_type='ORDER') AND (TRANSACTION.source_id = EXORDERS.id) " +
                        "      LEFT JOIN OPERATION_TYPE TRANSACTION_OPERATION ON (TRANSACTION_OPERATION.id = TRANSACTION.operation_type_id) " +
                        "      LEFT JOIN WALLET USER_WALLET ON (USER_WALLET.id = TRANSACTION.user_wallet_id) " +
                        "      LEFT JOIN COMPANY_WALLET ON (COMPANY_WALLET.currency_id = TRANSACTION.company_wallet_id) and (TRANSACTION.commission_amount <> 0) " +
                        "      LEFT JOIN USER ON (USER.id = USER_WALLET.user_id) " +
                        " WHERE EXORDERS.id=:order_id" +
                        " GROUP BY EXORDERS.id";
        Map<String, String> mapParameters = new HashMap<>();
        mapParameters.put("order_id", String.valueOf(orderId));
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, mapParameters, new RowMapper<OrderInfoDto>() {
                @Override
                public OrderInfoDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    OrderInfoDto orderInfoDto = new OrderInfoDto();
                    orderInfoDto.setId(rs.getInt("id"));
                    orderInfoDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                    orderInfoDto.setDateAcception(rs.getTimestamp("date_acception") == null ? null : rs.getTimestamp("date_acception").toLocalDateTime());
                    orderInfoDto.setCurrencyPairName(rs.getString("currency_pair_name"));
                    orderInfoDto.setOrderTypeName(rs.getString("order_type_name"));
                    orderInfoDto.setOrderStatusName(rs.getString("order_status_name"));
                    orderInfoDto.setExrate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
                    orderInfoDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
                    orderInfoDto.setAmountConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_convert"), locale, 2));
                    orderInfoDto.setCurrencyBaseName(rs.getString("currency_base_name"));
                    orderInfoDto.setCurrencyConvertName(rs.getString("currency_convert_name"));
                    orderInfoDto.setOrderCreatorEmail(rs.getString("order_creator_email"));
                    orderInfoDto.setOrderAcceptorEmail(rs.getString("order_acceptor_email"));
                    orderInfoDto.setTransactionCount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("transaction_count"), locale, 2));
                    orderInfoDto.setCompanyCommission(BigDecimalProcessing.formatLocale(rs.getBigDecimal("company_commission"), locale, 2));
                    return orderInfoDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public int searchOrderByAdmin(Integer currencyPair, Integer orderType, String orderDate, BigDecimal orderRate, BigDecimal orderVolume) {
        String sql = "SELECT id " +
                "  FROM EXORDERS" +
                "  WHERE (    " +
                "      EXORDERS.currency_pair_id = :currency_pair_id AND " +
                "      EXORDERS.operation_type_id = :operation_type_id AND " +
                "      DATE_FORMAT(EXORDERS.date_creation, '%Y-%m-%d %H:%i:%s') = STR_TO_DATE(:date_creation, '%Y-%m-%d %H:%i:%s') AND " +
                "      EXORDERS.exrate = :exrate AND " +
                "      EXORDERS.amount_base = :amount_base" +
                "  )" +
                "  LIMIT 1";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair));
        namedParameters.put("operation_type_id", String.valueOf(orderType));
        namedParameters.put("date_creation", orderDate);
        namedParameters.put("exrate", String.valueOf(orderRate));
        namedParameters.put("amount_base", String.valueOf(orderVolume));
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<Integer>() {
                @Override
                public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getInt(1);
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    @Override
    public Object deleteOrderByAdmin(int orderId) {
        List<OrderDetailDto> list = getOrderRelatedDataAndBlock(orderId);
        if (list.isEmpty()) {
            return OrderDeleteStatus.NOT_FOUND;
        }
        int processedRows = 1;
        /**/
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        OrderStatus orderStatus = list.get(0).getOrderStatus();
        /**/
        String sql = "UPDATE EXORDERS " +
                " SET status_id = :status_id" +
                " WHERE id = :order_id ";
        Map<String, Object> params = new HashMap<>();
        params.put("status_id", OrderStatus.DELETED.getStatus());
        params.put("order_id", orderId);
        if (jdbcTemplate.update(sql, params) <= 0) {
            return OrderDeleteStatus.ORDER_UPDATE_ERROR;
        }
        /**/
        for (OrderDetailDto orderDetailDto : list) {
            if (orderStatus == OrderStatus.CLOSED) {
                if (orderDetailDto.getCompanyCommission().compareTo(BigDecimal.ZERO) != 0) {
                    sql = "UPDATE COMPANY_WALLET " +
                            " SET commission_balance = commission_balance - :amount" +
                            " WHERE id = :company_wallet_id ";
                    params = new HashMap<>();
                    params.put("amount", orderDetailDto.getCompanyCommission());
                    params.put("company_wallet_id", orderDetailDto.getCompanyWalletId());
                    if (orderDetailDto.getCompanyWalletId() != 0 && jdbcTemplate.update(sql, params) <= 0) {
                        return OrderDeleteStatus.COMPANY_WALLET_UPDATE_ERROR;
                    }
                }
                /**/
                WalletOperationData walletOperationData = new WalletOperationData();
                OperationType operationType = null;
                if (orderDetailDto.getTransactionType() == OperationType.OUTPUT) {
                    operationType = OperationType.INPUT;
                } else if (orderDetailDto.getTransactionType() == OperationType.INPUT) {
                    operationType = OperationType.OUTPUT;
                }
                if (operationType != null) {
                    walletOperationData.setOperationType(operationType);
                    walletOperationData.setWalletId(orderDetailDto.getUserWalletId());
                    walletOperationData.setAmount(orderDetailDto.getTransactionAmount());
                    walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
                    Commission commission = commissionDao.getCommission(OperationType.STORNO);
                    walletOperationData.setCommission(commission);
                    walletOperationData.setCommissionAmount(commission.getValue());
                    walletOperationData.setSourceType(TransactionSourceType.ORDER);
                    walletOperationData.setSourceId(orderId);
                    WalletTransferStatus walletTransferStatus = walletDao.walletBalanceChange(walletOperationData);
                    if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
                        return OrderDeleteStatus.TRANSACTION_CREATE_ERROR;
                    }
                }
                /**/
                sql = "UPDATE TRANSACTION " +
                        " SET status_id = :status_id" +
                        " WHERE id = :transaction_id ";
                params = new HashMap<>();
                params.put("status_id", TransactionStatus.DELETED.getStatus());
                params.put("transaction_id", orderDetailDto.getTransactionId());
                if (jdbcTemplate.update(sql, params) <= 0) {
                    return OrderDeleteStatus.TRANSACTION_UPDATE_ERROR;
                }
                /**/
                processedRows++;
            } else if (orderStatus == OrderStatus.OPENED) {
                walletDao.walletInnerTransfer(orderDetailDto.getOrderCreatorReservedWalletId(),
                        orderDetailDto.getOrderCreatorReservedAmount(), TransactionSourceType.ORDER, orderId);
                /**/
                sql = "UPDATE TRANSACTION " +
                        " SET status_id = :status_id" +
                        " WHERE id = :transaction_id ";
                params = new HashMap<>();
                params.put("status_id", TransactionStatus.DELETED.getStatus());
                params.put("transaction_id", orderDetailDto.getTransactionId());
                if (jdbcTemplate.update(sql, params) <= 0) {
                    return OrderDeleteStatus.TRANSACTION_UPDATE_ERROR;
                }
            }
        }
        return processedRows;
    }

    private List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId) {
        String sql =
                "  SELECT  " +
                        "    EXORDERS.id AS order_id, " +
                        "    EXORDERS.status_id AS order_status_id, " +
                        "    IF (upper(ORDER_OPERATION.name)='SELL', EXORDERS.amount_base, EXORDERS.amount_convert+EXORDERS.commission_fixed_amount) AS order_creator_reserved_amount, " +
                        "    ORDER_CREATOR_RESERVED_WALLET.id AS order_creator_reserved_wallet_id,  " +
                        "    TRANSACTION.id AS transaction_id,  " +
                        "    TRANSACTION.operation_type_id as transaction_type_id,  " +
                        "    TRANSACTION.amount as transaction_amount, " +
                        "    USER_WALLET.id as user_wallet_id,  " +
                        "    COMPANY_WALLET.id as company_wallet_id, " +
                        "    TRANSACTION.commission_amount AS company_commission " +
                        "  FROM EXORDERS " +
                        "    JOIN OPERATION_TYPE AS ORDER_OPERATION ON (ORDER_OPERATION.id = EXORDERS.operation_type_id) " +
                        "    JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                        "    JOIN WALLET ORDER_CREATOR_RESERVED_WALLET ON  " +
                        "            (ORDER_CREATOR_RESERVED_WALLET.user_id=EXORDERS.user_id) AND  " +
                        "            ( " +
                        "                (upper(ORDER_OPERATION.name)='BUY' AND ORDER_CREATOR_RESERVED_WALLET.currency_id = CURRENCY_PAIR.currency2_id)  " +
                        "                OR  " +
                        "                (upper(ORDER_OPERATION.name)='SELL' AND ORDER_CREATOR_RESERVED_WALLET.currency_id = CURRENCY_PAIR.currency1_id) " +
                        "            ) " +
                        "    LEFT JOIN TRANSACTION ON (TRANSACTION.source_type='ORDER') AND (TRANSACTION.source_id = EXORDERS.id) " +
                        "    LEFT JOIN WALLET USER_WALLET ON (USER_WALLET.id = TRANSACTION.user_wallet_id) " +
                        "    LEFT JOIN COMPANY_WALLET ON (COMPANY_WALLET.id = TRANSACTION.company_wallet_id) and (TRANSACTION.commission_amount <> 0) " +
                        "  WHERE EXORDERS.id=:deleted_order_id AND EXORDERS.status_id IN (2, 3)" +
                        "  FOR UPDATE "; //FOR UPDATE !Important
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, String> namedParameters = new HashMap<String, String>() {{
            put("deleted_order_id", String.valueOf(orderId));
        }};
        return jdbcTemplate.query(sql, namedParameters, new RowMapper<OrderDetailDto>() {
            @Override
            public OrderDetailDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new OrderDetailDto(
                        rs.getInt("order_id"),
                        rs.getInt("order_status_id"),
                        rs.getBigDecimal("order_creator_reserved_amount"),
                        rs.getInt("order_creator_reserved_wallet_id"),
                        rs.getInt("transaction_id"),
                        rs.getInt("transaction_type_id"),
                        rs.getBigDecimal("transaction_amount"),
                        rs.getInt("user_wallet_id"),
                        rs.getInt("company_wallet_id"),
                        rs.getBigDecimal("company_commission")
                );
            }
        });
    }

    @Override
    public List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(String email, BackDealInterval backDealInterval, Integer limit, CurrencyPair currencyPair, Locale locale) {
        String sql = "SELECT EXORDERS.id, EXORDERS.date_acception, EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.operation_type_id " +
                "  FROM EXORDERS " +
                (email == null || email.isEmpty() ? "" : " JOIN USER ON ((USER.id = EXORDERS.user_id) OR (USER.id = EXORDERS.user_acceptor_id)) AND USER.email='" + email + "'") +
                "  WHERE EXORDERS.status_id = :status " +
                "  AND EXORDERS.date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                "  AND EXORDERS.currency_pair_id = :currency_pair_id " +
                "  ORDER BY EXORDERS.date_acception DESC, EXORDERS.id DESC " +
                (limit == -1 ? "" : "  LIMIT " + limit);
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("status", 3);
            put("currency_pair_id", currencyPair.getId());
        }};
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        return jdbcTemplate.query(sql, params, new RowMapper<OrderAcceptedHistoryDto>() {
            @Override
            public OrderAcceptedHistoryDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                OrderAcceptedHistoryDto orderAcceptedHistoryDto = new OrderAcceptedHistoryDto();
                orderAcceptedHistoryDto.setOrderId(rs.getInt("id"));
                orderAcceptedHistoryDto.setDateAcceptionTime(rs.getTimestamp("date_acception").toLocalDateTime().toLocalTime().format(DateTimeFormatter.ISO_LOCAL_TIME));
                orderAcceptedHistoryDto.setRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, true));
                orderAcceptedHistoryDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, true));
                orderAcceptedHistoryDto.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
                return orderAcceptedHistoryDto;
            }
        });
    }

    @Override
    public OrderCommissionsDto getCommissionForOrder() {
        final String sql =
                "  SELECT SUM(sell_commission) as sell_commission, SUM(buy_commission) as buy_commission " +
                        "  FROM " +
                        "      ((SELECT SELL.value as sell_commission, 0 as buy_commission " +
                        "      FROM COMMISSION SELL " +
                        "      WHERE operation_type = 3 " +
                        "      ORDER BY date DESC LIMIT 1)  " +
                        "    UNION " +
                        "      (SELECT 0, BUY.value " +
                        "      FROM COMMISSION BUY " +
                        "      WHERE operation_type = 4 " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "  ) COMMISSION";
        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            return jdbcTemplate.queryForObject(sql, new RowMapper<OrderCommissionsDto>() {
                @Override
                public OrderCommissionsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    OrderCommissionsDto orderCommissionsDto = new OrderCommissionsDto();
                    orderCommissionsDto.setSellCommission(rs.getBigDecimal("sell_commission"));
                    orderCommissionsDto.setBuyCommission(rs.getBigDecimal("buy_commission"));
                    return orderCommissionsDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(String email, CurrencyPair currencyPair, OrderStatus status,
                                                       OperationType operationType,
                                                       Integer offset, Integer limit, Locale locale) {
        String sql = "SELECT EXORDERS.*, CURRENCY_PAIR.name AS currency_pair_name" +
                "  FROM EXORDERS " +
                "  JOIN USER ON (USER.id=EXORDERS.user_id AND USER.email = :email) " +
                "  JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                "  WHERE (status_id = :status_id)" +
                "    AND (operation_type_id = :operation_type_id)" +
                (currencyPair == null ? "" : " AND EXORDERS.currency_pair_id=" + currencyPair.getId()) +
                "  ORDER BY -date_acception ASC, date_creation DESC" +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        namedParameters.put("status_id", status.getStatus());
        namedParameters.put("operation_type_id", operationType.getType());
        return namedParameterJdbcTemplate.query(sql, namedParameters, new RowMapper<OrderWideListDto>() {
            @Override
            public OrderWideListDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                OrderWideListDto orderWideListDto = new OrderWideListDto();
                orderWideListDto.setId(rs.getInt("id"));
                orderWideListDto.setUserId(rs.getInt("user_id"));
                orderWideListDto.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
                orderWideListDto.setExExchangeRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
                orderWideListDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
                orderWideListDto.setAmountConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_convert"), locale, 2));
                orderWideListDto.setComissionId(rs.getInt("commission_id"));
                orderWideListDto.setCommissionFixedAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_fixed_amount"), locale, 2));
                BigDecimal amountWithCommission = rs.getBigDecimal("amount_convert");
                if (orderWideListDto.getOperationType() == OperationType.SELL) {
                    amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.SUBTRACT);
                } else if (orderWideListDto.getOperationType() == OperationType.BUY) {
                    amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.ADD);
                }
                orderWideListDto.setAmountWithCommission(BigDecimalProcessing.formatLocale(amountWithCommission, locale, 2));
                orderWideListDto.setUserAcceptorId(rs.getInt("user_acceptor_id"));
                orderWideListDto.setDateCreation(rs.getTimestamp("date_creation") == null ? null : rs.getTimestamp("date_creation").toLocalDateTime());
                orderWideListDto.setDateAcception(rs.getTimestamp("date_acception") == null ? null : rs.getTimestamp("date_acception").toLocalDateTime());
                orderWideListDto.setStatus(OrderStatus.convert(rs.getInt("status_id")));
                orderWideListDto.setDateStatusModification(rs.getTimestamp("status_modification_date") == null ? null : rs.getTimestamp("status_modification_date").toLocalDateTime());
                orderWideListDto.setCurrencyPairName(rs.getString("currency_pair_name"));
                return orderWideListDto;
            }
        });
    }

    @Override
    public OrderCreateDto getMyOrderById(int orderId) {
        String sql = "SELECT EXORDERS.id as order_id, EXORDERS.user_id, EXORDERS.status_id, EXORDERS.operation_type_id,  " +
                "  EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.amount_convert, EXORDERS.commission_fixed_amount, " +
                "  CURRENCY_PAIR.id AS currency_pair_id, CURRENCY_PAIR.name AS currency_pair_name  " +
                "  FROM EXORDERS " +
                "  LEFT JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                "  WHERE (EXORDERS.id = :order_id)";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("order_id", orderId);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<OrderCreateDto>() {
                @Override
                public OrderCreateDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    OrderCreateDto orderCreateDto = new OrderCreateDto();
                    orderCreateDto.setOrderId(rs.getInt("order_id"));
                    orderCreateDto.setUserId(rs.getInt("user_id"));
                    orderCreateDto.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
                    orderCreateDto.setStatus(OrderStatus.convert(rs.getInt("status_id")));
                    orderCreateDto.setExchangeRate(rs.getBigDecimal("exrate"));
                    CurrencyPair currencyPair = new CurrencyPair();
                    currencyPair.setId(rs.getInt("currency_pair_id"));
                    currencyPair.setName(rs.getString("currency_pair_name"));
                    orderCreateDto.setCurrencyPair(currencyPair);
                    orderCreateDto.setAmount(rs.getBigDecimal("amount_base"));
                    orderCreateDto.setTotal(rs.getBigDecimal("amount_convert"));
                    orderCreateDto.setComission(rs.getBigDecimal("commission_fixed_amount"));
                    if (orderCreateDto.getOperationType() == OperationType.SELL) {
                        orderCreateDto.setTotalWithComission(BigDecimalProcessing.doAction(orderCreateDto.getTotal(), orderCreateDto.getComission(), ActionType.SUBTRACT));
                    } else {
                        orderCreateDto.setTotalWithComission(BigDecimalProcessing.doAction(orderCreateDto.getTotal(), orderCreateDto.getComission(), ActionType.ADD));
                    }
                    return orderCreateDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                           OperationType operationType) {
        String sql = "SELECT USER.id AS user_id, WALLET.id AS wallet_id, WALLET.active_balance, COMM.id AS commission_id, COMM.value AS commission_value" +
                "  FROM USER " +
                "    LEFT JOIN WALLET ON (WALLET.user_id=USER.id) AND (WALLET.currency_id = :currency_id) " +
                "    LEFT JOIN ((SELECT COMMISSION.id, COMMISSION.value " +
                "           FROM COMMISSION " +
                "           WHERE COMMISSION.operation_type=:operation_type_id ORDER BY COMMISSION.date " +
                "           DESC LIMIT 1) AS COMM) ON (1=1) " +
                "  WHERE USER.email = :email";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        namedParameters.put("operation_type_id", operationType.getType());
        namedParameters.put("currency_id", currency.getId());
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<WalletsAndCommissionsForOrderCreationDto>() {
                @Override
                public WalletsAndCommissionsForOrderCreationDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    WalletsAndCommissionsForOrderCreationDto walletsAndCommissionsForOrderCreationDto = new WalletsAndCommissionsForOrderCreationDto();
                    walletsAndCommissionsForOrderCreationDto.setUserId(rs.getInt("user_id"));
                    walletsAndCommissionsForOrderCreationDto.setSpendWalletId(rs.getInt("wallet_id"));
                    walletsAndCommissionsForOrderCreationDto.setSpendWalletActiveBalance(rs.getBigDecimal("active_balance"));
                    walletsAndCommissionsForOrderCreationDto.setCommissionId(rs.getInt("commission_id"));
                    walletsAndCommissionsForOrderCreationDto.setCommissionValue(rs.getBigDecimal("commission_value"));
                    return walletsAndCommissionsForOrderCreationDto;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean lockOrdersListForAcception(List<Integer> ordersList) {
        for (Integer orderId : ordersList) {
            String sql = "SELECT id " +
                    "  FROM EXORDERS " +
                    "  WHERE id = :order_id " +
                    "  FOR UPDATE ";
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
            Map<String, Object> namedParameters = new HashMap<>();
            namedParameters.put("order_id", orderId);
            try {
                namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
            } catch (EmptyResultDataAccessException e) {
                return false;
            }
        }
        return true;
    }

}



	

