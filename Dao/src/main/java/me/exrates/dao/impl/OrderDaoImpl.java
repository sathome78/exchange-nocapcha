package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.CommissionDao;
import me.exrates.dao.OrderDao;
import me.exrates.dao.UserDao;
import me.exrates.dao.WalletDao;
import me.exrates.dao.exception.OrderDaoException;
import me.exrates.dao.exception.notfound.CommissionsNotFoundException;
import me.exrates.dao.exception.notfound.WalletNotFoundException;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.PagingData;
import me.exrates.model.dto.CurrencyPairTurnoverReportDto;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.OrderBasicInfoDto;
import me.exrates.model.dto.OrderCommissionsDto;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.OrderReportInfoDto;
import me.exrates.model.dto.UserSummaryOrdersByCurrencyPairsDto;
import me.exrates.model.dto.UserSummaryOrdersDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.mobileApiDto.dashboard.CommissionsDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.dto.openAPI.OpenOrderDto;
import me.exrates.model.dto.openAPI.OrderBookItem;
import me.exrates.model.dto.openAPI.TradeHistoryDto;
import me.exrates.model.dto.openAPI.TransactionDto;
import me.exrates.model.dto.openAPI.UserOrdersDto;
import me.exrates.model.dto.openAPI.UserTradeHistoryDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.OrderTableEnum;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.TransactionStatus;
import me.exrates.model.enums.UserRole;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.OrderRoleInfoForDelete;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static me.exrates.model.enums.OrderStatus.CLOSED;
import static me.exrates.model.enums.TransactionSourceType.ORDER;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
@Log4j2
public class OrderDaoImpl implements OrderDao {

    private static final Logger LOGGER = LogManager.getLogger(OrderDaoImpl.class);

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate masterJdbcTemplate;
    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;
    @Autowired
    @Qualifier(value = "slaveForReportsTemplate")
    private NamedParameterJdbcTemplate slaveForReportsTemplate;

    private final RowMapper<UserOrdersDto> userOrdersRowMapper = (rs, row) -> {
        int id = rs.getInt("order_id");
        String currencyPairName = rs.getString("currency_pair_name");
        String orderType = OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))).name();
        LocalDateTime dateCreation = rs.getTimestamp("date_creation").toLocalDateTime();
        Timestamp timestampAcceptance = rs.getTimestamp("date_acception");
        LocalDateTime dateAcceptance = timestampAcceptance == null ? null : timestampAcceptance.toLocalDateTime();
        BigDecimal amount = rs.getBigDecimal("amount_base");
        BigDecimal price = rs.getBigDecimal("exrate");
        return new UserOrdersDto(id, currencyPairName, amount, orderType, price, dateCreation, dateAcceptance);
    };

    @Override
    public int createOrder(ExOrder exOrder) {
        String sql = "INSERT INTO EXORDERS" +
                "  (user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, commission_fixed_amount, status_id, order_source_id, base_type)" +
                "  VALUES " +
                "  (:user_id, :currency_pair_id, :operation_type_id, :exrate, :amount_base, :amount_convert, :commission_id, :commission_fixed_amount, :status_id, :order_source_id, :base_type)";
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
                .addValue("status_id", OrderStatus.INPROCESS.getStatus())
                .addValue("order_source_id", exOrder.getSourceId())
                .addValue("base_type", exOrder.getOrderBaseType().name());

        int result = masterJdbcTemplate.update(sql, parameters, keyHolder);
        int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }


    @Override
    public void postAcceptedOrderToDB(ExOrder exOrder) {
        String sql = "INSERT INTO BOT_ORDERS" +
                "  (user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_id, " +
                "   commission_fixed_amount, status_id, order_source_id, date_creation, date_acception, user_acceptor_id, status_modification_date)" +
                "  VALUES " +
                "  (:user_id, :currency_pair_id, :operation_type_id, :exrate, :amount_base, :amount_convert, :commission_id, :commission_fixed_amount," +
                " :status_id, :order_source_id, :date_creation, :date_acception, :user_acceptor_id, :status_modification_date)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", exOrder.getUserId());
            put("currency_pair_id", exOrder.getCurrencyPairId());
            put("operation_type_id", exOrder.getOperationType().type);
            put("exrate", exOrder.getExRate());
            put("amount_base", exOrder.getAmountBase());
            put("amount_convert", exOrder.getAmountConvert());
            put("commission_id", exOrder.getComissionId());
            put("commission_fixed_amount", exOrder.getCommissionFixedAmount());
            put("status_id", exOrder.getStatus().getStatus());
            put("order_source_id", exOrder.getSourceId());
            put("user_acceptor_id", exOrder.getUserAcceptorId());
            put("date_creation", exOrder.getDateCreation());
            put("date_acception", exOrder.getDateAcception());
            put("status_modification_date", exOrder.getDateAcception());
        }};
        masterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<OrderListDto> getOrdersSellForCurrencyPair(CurrencyPair currencyPair, UserRole filterRole) {
        String sql = "SELECT EXORDERS.id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                (filterRole == null ? "" : " JOIN USER ON (USER.id=EXORDERS.user_id)  AND USER.roleid = :user_role_id ") +
                "  WHERE status_id = 2 and operation_type_id= 3 and currency_pair_id=:currency_pair_id" +
                "  ORDER BY exrate ASC";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPair.getId());
        if (filterRole != null) {
            namedParameters.put("user_role_id", filterRole.getRole());
        }
        return slaveJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setOrderType(OperationType.convert(rs.getInt("operation_type_id")));
            order.setExrate(rs.getString("exrate"));
            order.setAmountBase(rs.getString("amount_base"));
            order.setAmountConvert(rs.getString("amount_convert"));
            return order;
        });
    }

    @Override
    public List<OrderListDto> getOrdersBuyForCurrencyPair(CurrencyPair currencyPair, UserRole filterRole) {
        String sql = "SELECT EXORDERS.id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount" +
                "  FROM EXORDERS " +
                (filterRole == null ? "" : " JOIN USER ON (USER.id=EXORDERS.user_id)  AND USER.roleid = :user_role_id ") +
                "  WHERE status_id = 2 and operation_type_id= 4 and currency_pair_id=:currency_pair_id" +
                "  ORDER BY exrate DESC";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPair.getId());
        if (filterRole != null) {
            namedParameters.put("user_role_id", filterRole.getRole());
        }
        return slaveJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setOrderType(OperationType.convert(rs.getInt("operation_type_id")));
            order.setExrate(rs.getString("exrate"));
            order.setAmountBase(rs.getString("amount_base"));
            order.setAmountConvert(rs.getString("amount_convert"));
            return order;
        });
    }

    @Override
    public List<OrderListDto> getMyOpenOrdersForCurrencyPair(CurrencyPair currencyPair, OrderType orderType, int userId) {
        String sql = "SELECT EXORDERS.id, user_id, currency_pair_id, operation_type_id, exrate, amount_base, amount_convert, commission_fixed_amount, order_source_id " +
                "  FROM EXORDERS " +
                "  WHERE status_id = 2 and operation_type_id = :type_id and currency_pair_id = :currency_pair_id and user_id = :user_id " +
                "  ORDER BY exrate ASC";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPair.getId());
        namedParameters.put("type_id", orderType.getOperationType().type);
        namedParameters.put("user_id", userId);
        return slaveJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setUserId(rs.getInt("user_id"));
            order.setOrderType(OperationType.convert(rs.getInt("operation_type_id")));
            order.setExrate(rs.getString("exrate"));
            order.setAmountBase(rs.getString("amount_base"));
            order.setAmountConvert(rs.getString("amount_convert"));
            order.setOrderSourceId(rs.getInt("order_source_id"));
            return order;
        });
    }

    @Override
    public Optional<BigDecimal> getLastOrderPriceByCurrencyPairAndOperationType(int currencyPairId, int operationTypeId) {
        String sql = "SELECT last_rate FROM RATES WHERE  currency_pair_id = :currency_pair_id";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPairId);
        try {
            return Optional.of(slaveJdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<BigDecimal> getLowestOpenOrderPriceByCurrencyPairAndOperationType(int currencyPairId, int operationTypeId) {
        String sql = "SELECT exrate FROM EXORDERS WHERE status_id = 2 AND currency_pair_id = :currency_pair_id AND operation_type_id = :operation_type_id " +
                "ORDER BY exrate ASC  LIMIT 1";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPairId);
        namedParameters.put("operation_type_id", operationTypeId);
        try {
            return Optional.of(masterJdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public ExOrder getOrderById(int orderId) {
        OrderTableEnum orderTableEnum = getOrderTable(orderId);

        final String sql =
                "SELECT * FROM " + orderTableEnum.name() + " WHERE id = :id ";

        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(orderId));

        try {
            return masterJdbcTemplate.queryForObject(sql, namedParameters, getExOrderRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public boolean setStatus(int orderId, OrderStatus status) {
        OrderTableEnum tableEnum = this.getOrderTable(orderId);

        String sql = "UPDATE " + tableEnum.name() + " SET status_id=:status_id WHERE id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(status.getStatus()));
        namedParameters.put("id", String.valueOf(orderId));
        int result = masterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public boolean updateOrder(ExOrder exOrder) {
        String sql = "update EXORDERS set user_acceptor_id=:user_acceptor_id, status_id=:status_id, " +
                " date_acception=NOW(6), counter_order_type = :counterType " +
                " where id = :id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("user_acceptor_id", String.valueOf(exOrder.getUserAcceptorId()));
        namedParameters.put("status_id", String.valueOf(exOrder.getStatus().getStatus()));
        namedParameters.put("counterType", exOrder.getCounterOrderBaseType() == null
                ? OrderBaseType.LIMIT.name()
                : exOrder.getCounterOrderBaseType().name());
        namedParameters.put("id", String.valueOf(exOrder.getId()));
        int result = masterJdbcTemplate.update(sql, namedParameters);
        return result > 0;
    }

    @Override
    public ExOrderStatisticsDto getOrderStatistic(CurrencyPair currencyPair, BackDealInterval backDealInterval) {
        String sql = "SELECT FIRSTORDER.amount_base AS first_amount_base, FIRSTORDER.exrate AS first_exrate," +
                "            LASTORDER.amount_base AS last_amount_base, LASTORDER.exrate AS last_exrate," +
                "            AGRIGATE.* " +
                "     FROM  " +
                "       (SELECT AGG.currency_pair_id AS currency_pair_id, MIN(AGG.first_date_acception) AS first_date_acception, " +
                "         MAX(AGG.last_date_acception) AS last_date_acception, MIN(AGG.min_exrate) AS min_exrate, " +
                "         MAX(AGG.max_exrate) AS max_exrate, SUM(AGG.deal_sum_base) AS deal_sum_base, SUM(AGG.deal_sum_convert) AS deal_sum_convert" +
                "         FROM    " +
                    "       (SELECT ORDERS.currency_pair_id AS currency_pair_id," +
                    "       MIN(ORDERS.date_acception) AS first_date_acception, MAX(ORDERS.date_acception) AS last_date_acception,  " +
                    "       MIN(ORDERS.exrate) AS min_exrate, MAX(ORDERS.exrate) AS max_exrate,  " +
                    "       SUM(ORDERS.amount_base) AS deal_sum_base, SUM(ORDERS.amount_convert) AS deal_sum_convert  " +
                    "       FROM ORDERS  " +
                    "       WHERE   " +
                    "       ORDERS.currency_pair_id = :currency_pair_id AND ORDERS.status_id = :status_id AND   " +
                    "       ORDERS.date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                    "       GROUP BY currency_pair_id " +
                    "         UNION ALL " +
                    "       SELECT BOT_ORDERS.currency_pair_id AS currency_pair_id," +
                    "       MIN(BOT_ORDERS.date_acception) AS first_date_acception, MAX(BOT_ORDERS.date_acception) AS last_date_acception,  " +
                    "       MIN(BOT_ORDERS.exrate) AS min_exrate, MAX(BOT_ORDERS.exrate) AS max_exrate,  " +
                    "       SUM(BOT_ORDERS.amount_base) AS deal_sum_base, SUM(BOT_ORDERS.amount_convert) AS deal_sum_convert  " +
                    "       FROM BOT_ORDERS  " +
                    "       WHERE   " +
                    "       BOT_ORDERS.currency_pair_id = :currency_pair_id AND BOT_ORDERS.status_id = :status_id AND   " +
                    "       BOT_ORDERS.date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                    "       GROUP BY BOT_ORDERS.currency_pair_id) AGG " +
                "       ) AGRIGATE " +
                "     LEFT JOIN ORDERS FIRSTORDER ON (FIRSTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND (FIRSTORDER.date_acception = AGRIGATE.first_date_acception)  " +
                "     LEFT JOIN ORDERS LASTORDER ON (LASTORDER.currency_pair_id = AGRIGATE.currency_pair_id) AND (LASTORDER.date_acception = AGRIGATE.last_date_acception)" +
                " ORDER BY FIRSTORDER.id ASC, LASTORDER.id DESC LIMIT 1 ";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("status_id", String.valueOf(3));
        namedParameters.put("currency_pair_id", String.valueOf(currencyPair.getId()));
        try {
            return slaveJdbcTemplate.queryForObject(sql, namedParameters, (rs, rowNum) -> {
                ExOrderStatisticsDto exOrderStatisticsDto = new ExOrderStatisticsDto(currencyPair);
                exOrderStatisticsDto.setFirstOrderAmountBase(rs.getString("first_amount_base"));
                exOrderStatisticsDto.setFirstOrderRate(rs.getString("first_exrate"));
                exOrderStatisticsDto.setLastOrderAmountBase(rs.getString("last_amount_base"));
                exOrderStatisticsDto.setLastOrderRate(rs.getString("last_exrate"));
                exOrderStatisticsDto.setMinRate(rs.getString("min_exrate"));
                exOrderStatisticsDto.setMaxRate(rs.getString("max_exrate"));
                exOrderStatisticsDto.setSumBase(rs.getString("deal_sum_base"));
                exOrderStatisticsDto.setSumConvert(rs.getString("deal_sum_convert"));
                return exOrderStatisticsDto;
            });
        } catch (EmptyResultDataAccessException e) {
            return new ExOrderStatisticsDto(currencyPair);
        }
    }

    @Override
    public OrderInfoDto getOrderInfo(int orderId, Locale locale) {
        OrderTableEnum orderTable = getOrderTable(orderId);
        String sql =
                " SELECT  " +
                        "     EXORDERS.id, EXORDERS.date_creation, EXORDERS.date_acception, EXORDERS.base_type, " +
                        "     ORDER_STATUS.name AS order_status_name,  " +
                        "     CURRENCY_PAIR.name as currency_pair_name,  " +
                        "     UPPER(ORDER_OPERATION.name) AS order_type_name,  " +
                        "     EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.amount_convert, " +
                        "     ORDER_CURRENCY_BASE.name as currency_base_name, ORDER_CURRENCY_CONVERT.name as currency_convert_name, " +
                        "     CREATOR.email AS order_creator_email, " +
                        "     ACCEPTOR.email AS order_acceptor_email, " +
                        "     COUNT(TRANSACTION.id) AS transaction_count,  " +
                        "     SUM(TRANSACTION.commission_amount) AS company_commission," +
                        "     EXORDERS.order_source_id AS source_id  " +
                        " FROM " + orderTable.name() + " AS EXORDERS " +
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
                        " GROUP BY " +
                        "     EXORDERS.id, EXORDERS.date_creation, EXORDERS.date_acception,  " +
                        "     order_status_name,  " +
                        "     currency_pair_name,  " +
                        "     order_type_name,  " +
                        "     EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.amount_convert, " +
                        "     currency_base_name, currency_convert_name, " +
                        "     order_creator_email, " +
                        "     order_acceptor_email ";
        Map<String, String> mapParameters = new HashMap<>();
        mapParameters.put("order_id", String.valueOf(orderId));
        try {
            return slaveJdbcTemplate.queryForObject(sql, mapParameters, (rs, rowNum) -> {
                OrderInfoDto orderInfoDto = new OrderInfoDto();
                OrderBaseType orderBaseType = OrderBaseType.valueOf(rs.getString("base_type"));
                orderInfoDto.setId(rs.getInt("id"));
                orderInfoDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                orderInfoDto.setDateAcception(rs.getTimestamp("date_acception") == null ? null : rs.getTimestamp("date_acception").toLocalDateTime());
                orderInfoDto.setCurrencyPairName(rs.getString("currency_pair_name"));
                orderInfoDto.setOrderTypeName(rs.getString("order_type_name").concat(" ").concat(orderBaseType.name()));
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
                orderInfoDto.setSource((Integer) rs.getObject("source_id"));
                orderInfoDto.setChildren(getOrderChildren(orderId));
                return orderInfoDto;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private List<Integer> getOrderChildren(int id) {
        String sql = " SELECT id FROM EXORDERS WHERE order_source_id = :id" +
                     " UNION ALL " +
                     " SELECT id FROM ORDERS WHERE order_source_id = :id";
        return slaveJdbcTemplate.queryForList(sql, Collections.singletonMap("id", id), Integer.class);
    }

    @Override
    public List<OrderAcceptedHistoryDto> getOrderAcceptedForPeriod(String email, BackDealInterval backDealInterval, Integer limit, CurrencyPair currencyPair) {
        String sql = "SELECT * " +
                     " FROM " +
                     "    ((SELECT EXORDERS.id, EXORDERS.date_acception, EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.operation_type_id " +
                     "     FROM ORDERS AS EXORDERS" +
                           (email == null || email.isEmpty() ? "" : " JOIN USER ON ((USER.id = EXORDERS.user_id) OR (USER.id = EXORDERS.user_acceptor_id)) AND USER.email='" + email + "'") +
                     "      WHERE EXORDERS.status_id = :status " +
                     "      AND EXORDERS.date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                     "      AND EXORDERS.currency_pair_id = :currency_pair_id " +
                     "      ORDER BY EXORDERS.date_acception DESC, EXORDERS.id DESC " +
                            (limit == -1 ? "" : "  LIMIT " + limit) +
                     ")     UNION ALL " +
                     "      (SELECT EXORDERS.id, EXORDERS.date_acception, EXORDERS.exrate, EXORDERS.amount_base, EXORDERS.operation_type_id " +
                     "      FROM BOT_ORDERS AS EXORDERS" +
                            (email == null || email.isEmpty() ? "" : " JOIN USER ON ((USER.id = EXORDERS.user_id) OR (USER.id = EXORDERS.user_acceptor_id)) AND USER.email='" + email + "'") +
                     "       WHERE EXORDERS.status_id = :status " +
                     "       AND EXORDERS.date_acception >= now() - INTERVAL " + backDealInterval.getInterval() +
                     "       AND EXORDERS.currency_pair_id = :currency_pair_id " +
                     "       ORDER BY EXORDERS.date_acception DESC, EXORDERS.id DESC " +
                             (limit == -1 ? "" : "  LIMIT " + limit) +
                     "      )) AS AGGR " +
                     "  ORDER BY AGGR.date_acception DESC, AGGR.id DESC " +
                     (limit == -1 ? "" : "  LIMIT " + limit);
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("status", 3);
            put("currency_pair_id", currencyPair.getId());
        }};
        return slaveJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            OrderAcceptedHistoryDto orderAcceptedHistoryDto = new OrderAcceptedHistoryDto();
            orderAcceptedHistoryDto.setOrderId(rs.getInt("id"));
            orderAcceptedHistoryDto.setDateAcceptionTime(rs.getTimestamp("date_acception").toLocalDateTime().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            orderAcceptedHistoryDto.setAcceptionTime(rs.getTimestamp("date_acception"));
            orderAcceptedHistoryDto.setRate(rs.getString("exrate"));
            orderAcceptedHistoryDto.setAmountBase(rs.getString("amount_base"));
            orderAcceptedHistoryDto.setOperationType(OperationType.getOpposite(OperationType.convert(rs.getInt("operation_type_id"))));
            return orderAcceptedHistoryDto;
        });
    }

    @Override
    public OrderCommissionsDto getCommissionForOrder(UserRole userRole) {
        final String sql =
                "  SELECT SUM(sell_commission) as sell_commission, SUM(buy_commission) as buy_commission " +
                        "  FROM " +
                        "      ((SELECT SELL.value as sell_commission, 0 as buy_commission " +
                        "      FROM COMMISSION SELL " +
                        "      WHERE operation_type = 3 AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1)  " +
                        "    UNION " +
                        "      (SELECT 0, BUY.value " +
                        "      FROM COMMISSION BUY " +
                        "      WHERE operation_type = 4 AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "  ) COMMISSION";
        try {
            Map<String, Integer> params = Collections.singletonMap("user_role", userRole.getRole());
            return slaveJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                OrderCommissionsDto orderCommissionsDto = new OrderCommissionsDto();
                orderCommissionsDto.setSellCommission(rs.getBigDecimal("sell_commission"));
                orderCommissionsDto.setBuyCommission(rs.getBigDecimal("buy_commission"));
                return orderCommissionsDto;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public CommissionsDto getAllCommissions(UserRole userRole) {
        final String sql =
                "  SELECT SUM(sell_commission) as sell_commission, SUM(buy_commission) as buy_commission, " +
                        "SUM(input_commission) as input_commission, SUM(output_commission) as output_commission, SUM(transfer_commission) as transfer_commission" +
                        "  FROM " +
                        "      ((SELECT SELL.value as sell_commission, 0 as buy_commission, 0 as input_commission, 0 as output_commission, " +
                        " 0 as transfer_commission " +
                        "      FROM COMMISSION SELL " +
                        "      WHERE operation_type = 3 AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1)  " +
                        "    UNION " +
                        "      (SELECT 0, BUY.value, 0, 0, 0 " +
                        "      FROM COMMISSION BUY " +
                        "      WHERE operation_type = 4  AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "    UNION " +
                        "      (SELECT 0, 0, INPUT.value, 0, 0  " +
                        "      FROM COMMISSION INPUT " +
                        "      WHERE operation_type = 1  AND user_role = :user_role " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "    UNION " +
                        "      (SELECT 0, 0, 0, OUTPUT.value, 0  " +
                        "      FROM COMMISSION OUTPUT " +
                        "      WHERE operation_type = 2 AND user_role = :user_role  " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "    UNION " +
                        "      (SELECT 0, 0, 0, 0, TRANSFER.value " +
                        "      FROM COMMISSION TRANSFER " +
                        "      WHERE operation_type = 9 AND user_role = :user_role  " +
                        "      ORDER BY date DESC LIMIT 1) " +
                        "  ) COMMISSION";

        Map<String, Integer> params = Collections.singletonMap("user_role", userRole.getRole());
        try {
            return slaveJdbcTemplate.queryForObject(sql, params, (rs, row) -> {
                CommissionsDto commissionsDto = new CommissionsDto();
                commissionsDto.setSellCommission(rs.getBigDecimal("sell_commission"));
                commissionsDto.setBuyCommission(rs.getBigDecimal("buy_commission"));
                commissionsDto.setInputCommission(rs.getBigDecimal("input_commission"));
                commissionsDto.setOutputCommission(rs.getBigDecimal("output_commission"));
                commissionsDto.setTransferCommission(rs.getBigDecimal("transfer_commission"));
                return commissionsDto;
            });
        } catch (EmptyResultDataAccessException ex) {
            throw new CommissionsNotFoundException(String.format("Commissions for role: %s not found", userRole));
        }
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(Integer userId, CurrencyPair currencyPair, OrderStatus status,
                                                       OperationType operationType, String scope, Integer offset,
                                                       Integer limit, Locale locale, UserRole userRole) {
        if (StringUtils.isEmpty(scope)) {
            scope = "OTHER";
        }

        String userFilterClause;
        switch (scope) {
            case "ALL":
                userFilterClause = " AND (o.user_id = :user_id OR o.user_acceptor_id = :user_id) ";
                break;
            case "ACCEPTED":
                userFilterClause = " AND o.user_acceptor_id = :user_id ";
                break;
            default:
                userFilterClause = " AND o.user_id = :user_id ";
                break;
        }

        String currencyPairClause = Objects.isNull(currencyPair)
                ? StringUtils.EMPTY
                : " AND o.currency_pair_id = :currency_pair_id ";


        String orderClause = " ORDER BY o.date_acception DESC, o.date_creation DESC ";

        String limitClause = limit == -1
                ? StringUtils.EMPTY
                : String.format(" LIMIT %s OFFSET %s ", String.valueOf(limit), String.valueOf(offset));

        OrderTableEnum orderTable = OrderTableEnum.getTableNameByStatusAndRole(status, userRole);

        String sql = "SELECT o.id," +
                " o.user_id," +
                " o.operation_type_id," +
                " o.exrate," +
                " o.amount_base," +
                " o.amount_convert," +
                " o.commission_id," +
                " o.commission_fixed_amount," +
                " o.user_acceptor_id," +
                " o.date_creation," +
                " o.date_acception," +
                " o.status_id," +
                " o.status_modification_date," +
                " o.currency_pair_id," +
                " o.base_type," +
                " cp.name AS currency_pair_name " +
                "FROM "+ orderTable.name() + " o " +
                "JOIN CURRENCY_PAIR cp ON cp.id = o.currency_pair_id " +
                "WHERE o.status_id = :status_id " +
                "AND o.operation_type_id IN (:operation_type_ids) " +
                currencyPairClause +
                userFilterClause +
                orderClause +
                limitClause;

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("status_id", status.getStatus());
        if (Objects.nonNull(operationType)) {
            params.put("operation_type_ids", operationType.getType());
        } else {
            params.put("operation_type_ids", Arrays.asList(3, 4));
        }
        if (nonNull(currencyPair)) {
            params.put("currency_pair_id", currencyPair.getId());
        }

        return slaveJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            OrderWideListDto orderWideListDto = new OrderWideListDto();
            orderWideListDto.setId(rs.getInt("id"));
            orderWideListDto.setUserId(rs.getInt("user_id"));
            orderWideListDto.setOperationTypeEnum(OperationType.convert(rs.getInt("operation_type_id")));
            orderWideListDto.setExExchangeRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
            orderWideListDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
            orderWideListDto.setAmountConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_convert"), locale, 2));
            orderWideListDto.setComissionId(rs.getInt("commission_id"));
            orderWideListDto.setCommissionFixedAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_fixed_amount"), locale, 2));
            BigDecimal amountWithCommission = rs.getBigDecimal("amount_convert");
            if (orderWideListDto.getOperationTypeEnum() == OperationType.SELL) {
                amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.SUBTRACT);
            } else if (orderWideListDto.getOperationTypeEnum() == OperationType.BUY) {
                amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.ADD);
            }
            orderWideListDto.setAmountWithCommission(BigDecimalProcessing.formatLocale(amountWithCommission, locale, 2));
            orderWideListDto.setUserAcceptorId(rs.getInt("user_acceptor_id"));
            orderWideListDto.setDateCreation(isNull(rs.getTimestamp("date_creation")) ? null : rs.getTimestamp("date_creation").toLocalDateTime());
            orderWideListDto.setDateAcception(isNull(rs.getTimestamp("date_acception")) ? null : rs.getTimestamp("date_acception").toLocalDateTime());
            orderWideListDto.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            orderWideListDto.setDateStatusModification(isNull(rs.getTimestamp("status_modification_date")) ? null : rs.getTimestamp("status_modification_date").toLocalDateTime());
            orderWideListDto.setCurrencyPairId(rs.getInt("currency_pair_id"));
            orderWideListDto.setCurrencyPairName(rs.getString("currency_pair_name"));
            orderWideListDto.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            orderWideListDto.setOperationType(String.join(" ", orderWideListDto.getOperationTypeEnum().name(), orderWideListDto.getOrderBaseType().name()));
            return orderWideListDto;
        });
    }

    @Override
    public int getUnfilteredOrdersCount(int id, CurrencyPair currencyPair, OrderStatus status, OperationType operationType, String scope, int offset, int limit, UserRole userRole) {
        if (StringUtils.isEmpty(scope)) {
            scope = "OTHER";
        }

        String userFilterClause;
        switch (scope) {
            case "ALL":
                userFilterClause = " AND (o.user_id = :user_id OR o.user_acceptor_id = :user_id) ";
                break;
            case "ACCEPTED":
                userFilterClause = " AND o.user_acceptor_id = :user_id ";
                break;
            default:
                userFilterClause = " AND o.user_id = :user_id ";
                break;
        }

        String currencyPairClause = Objects.isNull(currencyPair)
                ? StringUtils.EMPTY
                : String.format(" AND o.currency_pair_id = %s ", String.valueOf(currencyPair.getId()));


        String orderClause = " ORDER BY o.date_acception ASC, o.date_creation DESC ";


        String limitClause = limit == -1
                ? StringUtils.EMPTY
                : String.format(" LIMIT %s OFFSET %s ", String.valueOf(limit), String.valueOf(offset));

        OrderTableEnum orderTable = OrderTableEnum.getTableNameByStatusAndRole(status, userRole);

        String sql = "SELECT COUNT(o.id) " +
                "FROM " + orderTable.name() + " o " +
                "WHERE o.status_id IN (:status_ids) " +
                "AND o.operation_type_id IN (:operation_type_ids) " +
                currencyPairClause +
                userFilterClause +
                orderClause +
                limitClause;

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", id);
        params.put("status_ids", status.getStatus());
        if (Objects.nonNull(operationType)) {
            params.put("operation_type_ids", operationType.getType());
        } else {
            params.put("operation_type_ids", Arrays.asList(3, 4));
        }

        return slaveJdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    @Override
    public List<OrderWideListDto> getMyOrdersWithState(Integer userId, CurrencyPair currencyPair, String currencyName,
                                                       OrderStatus orderStatus, String scope, Integer limit, Integer offset,
                                                       Boolean hideCanceled, String sortByCreated,
                                                       LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, Locale locale, UserRole userRole) {
        String currencyPairClauseWhere = StringUtils.EMPTY;
        String currencyPairClauseWhereForStopLimit = StringUtils.EMPTY;
        if (nonNull(currencyPair) && currencyPair.getId() > 0) {
            currencyPairClauseWhere = " AND o.currency_pair_id = :currency_pair_id ";
            currencyPairClauseWhereForStopLimit = " AND so.currency_pair_id = :currency_pair_id ";
        }

        String createdClause = StringUtils.EMPTY;
        String createdStopLimitClause = StringUtils.EMPTY;
        if (Objects.nonNull(dateTimeFrom) && Objects.nonNull(dateTimeTo)) {
            createdClause = " AND (o.date_creation BETWEEN :date_from AND :date_before) ";
            createdStopLimitClause = " AND (so.date_creation BETWEEN :date_from AND :date_before) ";
        } else if (Objects.nonNull(dateTimeFrom)) {
            createdClause = " AND o.date_creation >= :date_from ";
            createdStopLimitClause = " AND so.date_creation >= :date_from ";
        } else if (Objects.nonNull(dateTimeTo)) {
            createdClause = " AND o.date_creation <= :date_before ";
            createdStopLimitClause = " AND so.date_creation <= :date_before ";
        }

        String currencyNameClause = isBlank(currencyName)
                ? StringUtils.EMPTY
                : " AND LOWER(cp.name) LIKE LOWER(:currency_name_part) ";

        String userFilterClause;
        switch (scope) {
            case "ALL":
                userFilterClause = " AND (o.user_id = :user_id OR o.user_acceptor_id = :user_id) ";
                break;
            case "ACCEPTED":
                userFilterClause = " AND o.user_acceptor_id = :user_id ";
                break;
            default:
                userFilterClause = " AND o.user_id = :user_id ";
                break;
        }

        String orderClause = String.format(" ORDER BY x.date_creation %s ", sortByCreated);

        String limitStr = limit < 1
                ? StringUtils.EMPTY
                : String.format(" LIMIT %d ", limit);
        String offsetStr = offset < 1
                ? StringUtils.EMPTY
                : String.format(" OFFSET %d ", offset);

        OrderTableEnum orderTable = OrderTableEnum.getTableNameByStatusAndRole(orderStatus, userRole);

        String sqlWithBothOrders = "SELECT * " +
                "FROM ((SELECT o.id, " +
                "             o.user_id," +
                "             o.operation_type_id," +
                "             o.exrate, " +
                "             o.amount_base, " +
                "             o.amount_convert, " +
                "             o.commission_id," +
                "             o.commission_fixed_amount, " +
                "             o.user_acceptor_id," +
                "             o.date_acception, " +
                "             o.status_id, " +
                "             o.status_modification_date, " +
                "             o.currency_pair_id, " +
                "             o.base_type, " +
                "             o.counter_order_type, " +
                "             cp.name                AS currency_pair_name, " +
                "             com.value              AS commission_value, " +
//              "             o.date_creation        AS date_creation, " +
                "             IF ((o.user_acceptor_id = :user_id AND o.user_id <> :user_id), o.date_acception, o.date_creation) as date_creation, " +
                "             null                   AS child_order_id, " +
                "             null                   AS stop_rate, " +
                "             null                   AS limit_rate," +
                "             null                   AS date_modification " +
                "      FROM " + orderTable.name() + " o " +
                "      JOIN CURRENCY_PAIR cp ON cp.id = o.currency_pair_id " +
                "      INNER JOIN COMMISSION com ON com.id = o.commission_id " +
                "      WHERE o.status_id in (:status_id) AND o.operation_type_id IN (:operation_type_id) "
                + createdClause
                + currencyPairClauseWhere
                + userFilterClause
                + currencyNameClause
                + ") UNION ALL " +
                "      (SELECT so.id, " +
                "             so.user_id, " +
                "             so.operation_type_id, " +
                "             null                          AS exrate, " +
                "             so.amount_base, " +
                "             so.amount_convert, " +
                "             so.commission_id, " +
                "             so.commission_fixed_amount, " +
                "             null                          AS user_acceptor_id, " +
                "             null                          AS date_acception, " +
                "             so.status_id, " +
                "             null                          AS status_modification_date, " +
                "             so.currency_pair_id, " +
                "             'STOP_LIMIT', " +
                "             'MARKET', " +
                "             cp.name                       AS currency_pair_name, " +
                "             com.value                     AS commission_value, " +
                "             so.date_creation              AS date_creation, " +
                "             so.child_order_id             AS child_order_id, " +
                "             so.stop_rate                  AS stop_rate, " +
                "             so.limit_rate                 AS limit_rate, " +
                "             so.date_modification          AS date_modification " +
                "      FROM STOP_ORDERS so " +
                "      JOIN CURRENCY_PAIR cp ON cp.id = so.currency_pair_id " +
                "      INNER JOIN COMMISSION com ON com.id = so.commission_id " +
                "      WHERE so.status_id in (:status_id) AND so.operation_type_id IN (:operation_type_id) " +
                "      AND so.user_id = :user_id "
                + currencyPairClauseWhereForStopLimit
                + createdStopLimitClause
                + currencyNameClause +
                ")) x " +
                orderClause +
                limitStr +
                offsetStr;

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("status_id", getListOrderStatus(orderStatus, hideCanceled));
        params.put("operation_type_id", Arrays.asList(3, 4));
        if (nonNull(currencyPair) && currencyPair.getId() > 0) {
            params.put("currency_pair_id", currencyPair.getId());
        }
        if (nonNull(dateTimeFrom)) {
            params.put("date_from", dateTimeFrom);
        }
        if (nonNull(dateTimeTo)) {
            params.put("date_before", dateTimeTo);
        }
        if (isNotBlank(currencyName)) {
            params.put("currency_name_part", String.join(StringUtils.EMPTY, "%", currencyName, "%"));
        }

        return slaveJdbcTemplate.query(sqlWithBothOrders, params, orderWithStateRowMapper(locale, userId));
    }

    @Override
    public WalletsAndCommissionsForOrderCreationDto getWalletAndCommission(String email, Currency currency,
                                                                           OperationType operationType, UserRole userRole) {
        String sql = "SELECT USER.id AS user_id, WALLET.id AS wallet_id, WALLET.active_balance, COMM.id AS commission_id, COMM.value AS commission_value" +
                "  FROM USER " +
                "    LEFT JOIN WALLET ON (WALLET.user_id=USER.id) AND (WALLET.currency_id = :currency_id) " +
                "    LEFT JOIN ((SELECT COMMISSION.id, COMMISSION.value " +
                "           FROM COMMISSION " +
                "           WHERE COMMISSION.operation_type=:operation_type_id AND COMMISSION.user_role = :user_role ORDER BY COMMISSION.date " +
                "           DESC LIMIT 1) AS COMM) ON (1=1) " +
                "  WHERE USER.email = :email";

        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("email", email);
        namedParameters.put("operation_type_id", operationType.getType());
        namedParameters.put("currency_id", currency.getId());
        namedParameters.put("user_role", userRole.getRole());

        try {
            return masterJdbcTemplate.queryForObject(sql, namedParameters, new RowMapper<WalletsAndCommissionsForOrderCreationDto>() {
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
        } catch (EmptyResultDataAccessException ex) {
            throw new WalletNotFoundException(String.format("Wallet for user: %s not found", email));
        }
    }

    @Override
    public boolean lockOrdersListForAcception(List<Integer> ordersList) {
        String sql = "SELECT id " +
                "  FROM EXORDERS " +
                "  WHERE id IN (:order_ids) FOR UPDATE ";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("order_ids", ordersList);
        try {
            final List<Integer> records = masterJdbcTemplate.queryForList(sql, namedParameters, Integer.class);
            return records.containsAll(ordersList);
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Override
    public PagingData<List<OrderBasicInfoDto>> searchOrders(AdminOrderFilterData adminOrderFilterData, DataTableParams dataTableParams, Locale locale) {

        String limitAndOffset = dataTableParams.getLimitAndOffsetClause();
        String orderBy = dataTableParams.getOrderByClause();

        String mainSelect = String.join(" ", "SELECT aggr.* FROM (%s) aggr " +  orderBy.replace("EXORDERS", "aggr") + limitAndOffset );
        String mainSelectCount = String.join(" ", "SELECT SUM(aggr.count) FROM (%s) aggr ");
        String union = " UNION ALL ";

        String sqlSelect = " (SELECT  " +
                "     EXORDERS.id, EXORDERS.date_creation, EXORDERS.status_id AS status, EXORDERS.base_type, " +
                "     CURRENCY_PAIR.name as currency_pair_name,  " +
                "     UPPER(ORDER_OPERATION.name) AS order_type_name,  " +
                "     EXORDERS.exrate, EXORDERS.amount_base, " +
                "     CREATOR.email AS order_creator_email, " +
                "     CREATOR.roleid AS role  ";
        String sqlFrom = "FROM %s as EXORDERS" +
                "      JOIN OPERATION_TYPE AS ORDER_OPERATION ON (ORDER_OPERATION.id = EXORDERS.operation_type_id) " +
                "      JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                "      JOIN USER CREATOR ON (CREATOR.id = EXORDERS.user_id) ";
        String sqlSelectCount = " SELECT COUNT(*) as count ";

        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("offset", dataTableParams.getStart());
        namedParameters.put("limit", dataTableParams.getLength());
        namedParameters.putAll(adminOrderFilterData.getNamedParams());
        String criteria = adminOrderFilterData.getSQLFilterClause();
        String whereClause = StringUtils.isNotEmpty(criteria) ? "WHERE " + criteria : "";

        String unionSelect = Arrays.stream(OrderTableEnum.values())
                .map(p -> String.join(" ", sqlSelect, String.format(sqlFrom, p), whereClause, orderBy, limitAndOffset, ") "))
                .collect(Collectors.joining(union));

        String unionSelectCount = Arrays.stream(OrderTableEnum.values())
                .map(p -> String.join(" ", sqlSelectCount, String.format(sqlFrom, p), whereClause))
                .collect(Collectors.joining(union));

        String selectQuery = String.format(mainSelect, unionSelect);
        String selectCountQuery = String.format(mainSelectCount, unionSelectCount);

        log.debug(selectQuery);
        log.debug(selectCountQuery);

        PagingData<List<OrderBasicInfoDto>> result = new PagingData<>();
        //
        List<OrderBasicInfoDto> infoDtoList = slaveForReportsTemplate.query(selectQuery, namedParameters, (rs, rowNum) -> {
            OrderBasicInfoDto infoDto = new OrderBasicInfoDto();
            OrderBaseType baseType = OrderBaseType.convert(rs.getString("base_type"));
            infoDto.setId(rs.getInt("id"));
            infoDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            infoDto.setCurrencyPairName(rs.getString("currency_pair_name"));
            infoDto.setOrderTypeName(rs.getString("order_type_name").concat(" ").concat(baseType.name()));
            infoDto.setExrate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
            infoDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
            infoDto.setOrderCreatorEmail(rs.getString("order_creator_email"));
            infoDto.setStatusId(rs.getInt("status"));
            infoDto.setStatus(OrderStatus.convert(rs.getInt("status")).toString());
            infoDto.setRole(UserRole.convert(rs.getInt("role")).name());
            return infoDto;

        });
        int total = masterJdbcTemplate.queryForObject(selectCountQuery, namedParameters, Integer.class);
        result.setData(infoDtoList);
        result.setTotal(total);
        result.setFiltered(total);
        return result;
    }

    @Override
    public List<OrderReportInfoDto> getOrdersForReport(AdminOrderFilterData adminOrderFilterData) {
        //Need, because table EXORDERS has many data
        String limit = "LIMIT 100000";

        String mainSql = "SELECT aggr.* FROM (%s) aggr ORDER BY aggr.date_creation ";

        String sqlSelect =   " (SELECT EXORDERS.id, EXORDERS.date_creation, EXORDERS.date_acception, cp.name AS currency_pair_name, " +
                             " UPPER(ORDER_OPERATION.name) as operation_type, EXORDERS.base_type as order_base_type, " +
                             " EXORDERS.exrate, EXORDERS.amount_base, CREATOR.email AS order_creator_email, " +
                             " CREATOR.roleid AS creator_role_id, ACCEPTOR.email AS order_acceptor_email, " +
                             " ACCEPTOR.roleid AS acceptor_role_id, EXORDERS.status_id " +
                             " FROM %s as EXORDERS " +
                             " JOIN OPERATION_TYPE AS ORDER_OPERATION ON (ORDER_OPERATION.id = EXORDERS.operation_type_id) " +
                             " JOIN CURRENCY_PAIR as cp ON (cp.id = EXORDERS.currency_pair_id) " +
                             " JOIN USER as CREATOR ON (CREATOR.id = EXORDERS.user_id) " +
                             " LEFT JOIN USER as ACCEPTOR ON (ACCEPTOR.id = EXORDERS.user_acceptor_id) ";

        Map<String, Object> namedParameters = new HashMap<>(adminOrderFilterData.getNamedParams());

        String criteria = adminOrderFilterData.getSQLFilterClause();
        String whereClause = StringUtils.isNotEmpty(criteria) ? "WHERE " + criteria : "";
        String selectExOrdersQuery = String.join(" ", String.format(sqlSelect, OrderTableEnum.EXORDERS), whereClause, limit, ") ");
        String selectOrdersQuery = String.join(" ", String.format(sqlSelect, OrderTableEnum.ORDERS), whereClause, limit, ") ");

        String query = String.format(mainSql, String.join(" UNION ALL ", selectExOrdersQuery, selectOrdersQuery));

        log.debug(query);

        return slaveJdbcTemplate.query(query, namedParameters, (rs, row) -> {
            OrderReportInfoDto orderReportInfoDto = new OrderReportInfoDto();
            orderReportInfoDto.setId(rs.getInt("id"));
            orderReportInfoDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            orderReportInfoDto.setDateAcception(rs.getTimestamp("date_acception") != null
                    ? rs.getTimestamp("date_acception").toLocalDateTime() : null);
            orderReportInfoDto.setCurrencyPairName(rs.getString("currency_pair_name"));
            orderReportInfoDto.setOrderTypeName(rs.getString("operation_type").concat(" ").concat(rs.getString("order_base_type")));
            orderReportInfoDto.setExrate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), Locale.ENGLISH, 2));
            orderReportInfoDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), Locale.ENGLISH, 2));
            orderReportInfoDto.setOrderCreatorEmail(rs.getString("order_creator_email"));
            orderReportInfoDto.setCreatorRole(UserRole.convert(rs.getInt("creator_role_id")).name());
            orderReportInfoDto.setOrderAcceptorEmail(rs.getString("order_acceptor_email"));
            orderReportInfoDto.setAcceptorRole(rs.getInt("acceptor_role_id") != 0 ? UserRole.convert(rs.getInt("acceptor_role_id")).name() : null);
            orderReportInfoDto.setOrderStatusName(OrderStatus.convert(rs.getInt("status_id")).toString());

            return orderReportInfoDto;
        });
    }

    @Override
    public List<ExOrder> selectTopOrders(Integer currencyPairId, BigDecimal exrate,
                                         OperationType orderType, boolean sameRoleOnly, Integer userAcceptorRoleId, OrderBaseType orderBaseType) {
        String sortDirection = "";
        String exrateClause = "";
        if (orderType == OperationType.BUY) {
            sortDirection = "DESC";
            exrateClause = "AND EO.exrate >= :exrate ";
        } else if (orderType == OperationType.SELL) {
            sortDirection = "ASC";
            exrateClause = "AND EO.exrate <= :exrate ";
        }

        String roleJoinClause = String.format(
                " JOIN USER U ON EO.user_id = U.id AND U.roleid IN (SELECT user_role_id FROM USER_ROLE_SETTINGS " +
                        "WHERE user_role_id = :acceptor_role_id OR order_acception_same_role_only = %d) ", sameRoleOnly ? -1 : 0);

        String sqlSetVar = "SET @cumsum := 0";

        /*needs to return several orders with best exrate if their total sum is less than amount in param,
         * or at least one order if base amount is greater than param amount*/
        String sql = "SELECT EO.id, EO.user_id, EO.currency_pair_id, EO.operation_type_id, EO.exrate, EO.amount_base, EO.amount_convert, " +
                "EO.commission_id, EO.commission_fixed_amount, EO.date_creation, EO.status_id, EO.base_type, EO.order_source_id " +
                "FROM EXORDERS EO " + roleJoinClause +
                "WHERE EO.status_id = 2 AND EO.currency_pair_id = :currency_pair_id AND EO.base_type =:order_base_type " +
                "AND EO.operation_type_id = :operation_type_id " + exrateClause +
                " ORDER BY EO.exrate " + sortDirection + ", EO.amount_base ASC ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("currency_pair_id", currencyPairId);
            put("exrate", exrate);
            put("operation_type_id", orderType.getType());
            put("acceptor_role_id", userAcceptorRoleId);
            put("order_base_type", orderBaseType.name());
        }};
        masterJdbcTemplate.execute(sqlSetVar, PreparedStatement::execute);

        return masterJdbcTemplate.query(sql, params, (rs, row) -> {
            ExOrder exOrder = new ExOrder();
            exOrder.setId(rs.getInt("id"));
            exOrder.setUserId(rs.getInt("user_id"));
            exOrder.setCurrencyPairId(rs.getInt("currency_pair_id"));
            exOrder.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            exOrder.setExRate(rs.getBigDecimal("exrate"));
            exOrder.setAmountBase(rs.getBigDecimal("amount_base"));
            exOrder.setAmountConvert(rs.getBigDecimal("amount_convert"));
            exOrder.setComissionId(rs.getInt("commission_id"));
            exOrder.setCommissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"));
            exOrder.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
            exOrder.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            exOrder.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            exOrder.setSourceId(rs.getInt("order_source_id"));
            return exOrder;
        });
    }

    @Override
    public OrderRoleInfoForDelete getOrderRoleInfo(int orderId) {
        OrderTableEnum orderTable = getOrderTable(orderId);

        String sql = "SELECT EO.status_id, CREATOR.roleid AS creator_role, ACCEPTOR.roleid AS acceptor_role, COUNT(TX.id) AS tx_count " +
                "from " + orderTable.name() + " EO " +
                "  JOIN USER CREATOR ON EO.user_id = CREATOR.id " +
                "  LEFT JOIN USER ACCEPTOR ON EO.user_acceptor_id = ACCEPTOR.id " +
                // join on source type and source id to use index
                "  LEFT JOIN TRANSACTION TX ON TX.source_type = 'ORDER' AND TX.source_id = EO.id " +
                "WHERE EO.id = :order_id;";
        return masterJdbcTemplate.queryForObject(sql, Collections.singletonMap("order_id", orderId), (rs, rowNum) -> {
            Integer statusId = getInteger(rs, "status_id");
            Integer creatorRoleId = getInteger(rs, "creator_role");
            Integer acceptorRoleId = getInteger(rs, "acceptor_role");
            OrderStatus status = statusId == null ? null : OrderStatus.convert(statusId);
            UserRole creatorRole = creatorRoleId == null ? null : UserRole.convert(creatorRoleId);
            UserRole acceptorRole = acceptorRoleId == null ? null : UserRole.convert(acceptorRoleId);
            int txCount = rs.getInt("tx_count");
            return new OrderRoleInfoForDelete(status, creatorRole, acceptorRole, txCount);
        });
    }

    private Integer getInteger(ResultSet rs, String fieldName) throws SQLException {
        Integer result = rs.getInt(fieldName);
        if (rs.wasNull()) {
            result = null;
        }
        return result;
    }

    @Override
    public List<OrderBookItem> getOrderBookItemsForType(Integer currencyPairId, OrderType orderType) {
        String orderDirection = orderType == OrderType.BUY ? " DESC " : " ASC ";
        String sql = "SELECT amount_base, exrate FROM EXORDERS WHERE currency_pair_id = :currency_pair_id " +
                "AND status_id = :status_id AND operation_type_id = :operation_type_id " +
                "ORDER BY exrate " + orderDirection;

        Map<String, Object> params = new HashMap<>();
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", OrderStatus.OPENED.getStatus());
        params.put("operation_type_id", orderType.getOperationType().type);

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {
            OrderBookItem item = new OrderBookItem();
            item.setOrderType(orderType);
            item.setAmount(rs.getBigDecimal("amount_base"));
            item.setRate(rs.getBigDecimal("exrate"));
            return item;
        });
    }

    @Override
    public List<OrderBookItem> getOrderBookItems(Integer currencyPairId) {
        String sql = "SELECT operation_type_id, amount_base, exrate FROM EXORDERS WHERE currency_pair_id = :currency_pair_id " +
                "AND status_id = :status_id ";

        Map<String, Object> params = new HashMap<>();
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", OrderStatus.OPENED.getStatus());

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {
            OrderBookItem item = new OrderBookItem();
            item.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))));
            item.setAmount(rs.getBigDecimal("amount_base"));
            item.setRate(rs.getBigDecimal("exrate"));
            return item;
        });
    }

    @Override
    public List<OpenOrderDto> getOpenOrders(Integer currencyPairId, OrderType orderType) {
        String orderByDirection = orderType == OrderType.SELL ? " ASC " : " DESC ";
        String orderBySql = " ORDER BY exrate " + orderByDirection;
        String sql = "SELECT id, operation_type_id, amount_base, exrate FROM EXORDERS " +
                "WHERE currency_pair_id = :currency_pair_id " +
                "AND status_id = :status_id AND operation_type_id = :operation_type_id " + orderBySql;

        Map<String, Object> params = new HashMap<>();
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", OrderStatus.OPENED.getStatus());
        params.put("operation_type_id", orderType.getOperationType().type);

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {
            OpenOrderDto item = new OpenOrderDto();
            item.setId(rs.getInt("id"));
            item.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))).name());
            item.setAmount(rs.getBigDecimal("amount_base"));
            item.setPrice(rs.getBigDecimal("exrate"));
            return item;
        });
    }

    @Override
    public List<TradeHistoryDto> getTradeHistory(Integer currencyPairId,
                                                 LocalDateTime fromDate,
                                                 LocalDateTime toDate,
                                                 Integer limit,
                                                 String direction) {
        String directionSql = "ASC".equalsIgnoreCase(direction)
                ? " ORDER BY o.date_acception ASC"
                : " ORDER BY o.date_acception DESC";
        String limitSql = nonNull(limit) ? " LIMIT :limit" : StringUtils.EMPTY;

        String sql = "SELECT  o.* FROM " +
                    "((SELECT o.id as order_id, " +
                    "o.date_creation as created, " +
                    "o.date_acception as date_acception, " +
                    "o.amount_base as amount, " +
                    "o.exrate as price, " +
                    "o.amount_convert as sum, " +
                    "c.value as commission, " +
                    "o.operation_type_id" +
                    " FROM ORDERS o" +
                    " JOIN COMMISSION c on o.commission_id = c.id" +
                    " WHERE o.currency_pair_id=:currency_pair_id AND o.status_id=:status_id" +
                    " AND o.date_acception BETWEEN :start_date AND :end_date"
                    + directionSql
                    + limitSql +
                ") UNION ALL " +
                    "(SELECT o.id as order_id, " +
                    "o.date_creation as created, " +
                    "o.date_acception as date_acception, " +
                    "o.amount_base as amount, " +
                    "o.exrate as price, " +
                    "o.amount_convert as sum, " +
                    "c.value as commission, " +
                    "o.operation_type_id" +
                    " FROM BOT_ORDERS o" +
                    " JOIN COMMISSION c on o.commission_id = c.id" +
                    " WHERE o.currency_pair_id=:currency_pair_id AND o.status_id=:status_id" +
                    " AND o.date_acception BETWEEN :start_date AND :end_date"
                    + directionSql
                    + limitSql +
                    ")) o " +
                 directionSql +
                 limitSql;

        Map<String, Object> params = new HashMap<>();
        params.put("status_id", CLOSED.getStatus());
        params.put("currency_pair_id", currencyPairId);
        params.put("start_date", fromDate);
        params.put("end_date", toDate);
        params.put("limit", limit);

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {
            TradeHistoryDto tradeHistoryDto = new TradeHistoryDto();
            tradeHistoryDto.setOrderId(rs.getInt("order_id"));
            tradeHistoryDto.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            tradeHistoryDto.setDateAcceptance(rs.getTimestamp("date_acception").toLocalDateTime());
            tradeHistoryDto.setAmount(rs.getBigDecimal("amount"));
            tradeHistoryDto.setPrice(rs.getBigDecimal("price"));
            tradeHistoryDto.setTotal(rs.getBigDecimal("sum"));
            tradeHistoryDto.setCommission(rs.getBigDecimal("commission"));
            tradeHistoryDto.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))));
            return tradeHistoryDto;
        });
    }

    @Override
    public List<UserOrdersDto> getUserOpenOrders(Integer userId, @Nullable Integer currencyPairId) {
        String currencyPairSql = currencyPairId == null ? "" : " AND EO.currency_pair_id = :currency_pair_id ";
        String sql = "SELECT EO.id AS order_id, EO.amount_base, EO.exrate, CP.name AS currency_pair_name, EO.operation_type_id, " +
                " EO.date_creation, EO.date_acception FROM EXORDERS EO " +
                " JOIN CURRENCY_PAIR CP ON EO.currency_pair_id = CP.id " +
                " WHERE EO.user_id = :user_id AND EO.status_id = :status_id " + currencyPairSql +
                " ORDER BY EO.date_creation DESC";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", OrderStatus.OPENED.getStatus());

        return masterJdbcTemplate.query(sql, params, userOrdersRowMapper);
    }

    @Override
    public List<UserOrdersDto> getUserOrdersByStatus(Integer userId,
                                                     Integer currencyPairId,
                                                     OrderStatus status,
                                                     int limit,
                                                     int offset,
                                                     UserRole role) {
        String orderSql = status == OrderStatus.CLOSED ? " ASC " : " DESC ";
        String currencyPairSql = nonNull(currencyPairId) ? " AND EO.currency_pair_id = :currency_pair_id " : StringUtils.EMPTY;
        String limitSql = limit > 0 ? " LIMIT :limit " : StringUtils.EMPTY;
        String offsetSql = (limit > 0 && offset > 0) ? "OFFSET :offset" : StringUtils.EMPTY;

        OrderTableEnum table = OrderTableEnum.getTableNameByStatusAndRole(status, role);

        String sql = "SELECT EO.id AS order_id, EO.amount_base, EO.exrate, CP.name AS currency_pair_name, EO.operation_type_id, " +
                " EO.date_creation, EO.date_acception FROM " + table.name() + " EO " +
                " JOIN CURRENCY_PAIR CP ON EO.currency_pair_id = CP.id " +
                " WHERE (EO.user_id = :user_id OR EO.user_acceptor_id = :user_id) AND EO.status_id = :status_id " + currencyPairSql +
                " ORDER BY EO.date_creation " + orderSql + limitSql + offsetSql;

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", status.getStatus());
        params.put("limit", limit);
        params.put("offset", offset);

        return slaveJdbcTemplate.query(sql, params, userOrdersRowMapper);
    }

    @Override
    public List<UserTradeHistoryDto> getUserTradeHistoryByCurrencyPair(Integer userId,
                                                                       Integer currencyPairId,
                                                                       LocalDateTime fromDate,
                                                                       LocalDateTime toDate,
                                                                       Integer limit,
                                                                       UserRole userRole) {
        String limitSql = nonNull(limit) ? " LIMIT :limit" : StringUtils.EMPTY;

        OrderStatus orderStatus = CLOSED;

        OrderTableEnum table = OrderTableEnum.getTableNameByStatusAndRole(orderStatus, userRole);

        String sql = "SELECT o.id as order_id, " +
                "o.user_id as user_id, " +
                "o.date_creation as created, " +
                "o.date_acception as accepted, " +
                "o.amount_base as amount, " +
                "o.exrate as price, " +
                "o.amount_convert as sum, " +
                "c.value as commission, " +
                "o.operation_type_id" +
                " FROM " + table.name() + " o " +
                " JOIN COMMISSION c on o.commission_id = c.id" +
                " WHERE (o.user_id = :user_id OR o.user_acceptor_id = :user_id) AND o.currency_pair_id = :currency_pair_id" +
                " AND o.status_id = :status_id AND o.date_acception BETWEEN :start_date AND :end_date" +
                " ORDER BY o.date_acception ASC"
                + limitSql;

        Map<String, Object> params = new HashMap<>();
        params.put("status_id", orderStatus.getStatus());
        params.put("user_id", userId);
        params.put("currency_pair_id", currencyPairId);
        params.put("start_date", fromDate);
        params.put("end_date", toDate);
        params.put("limit", limit);

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> {
            UserTradeHistoryDto userTradeHistoryDto = new UserTradeHistoryDto();
            userTradeHistoryDto.setUserId(userId);
            userTradeHistoryDto.setIsMaker(userId == rs.getInt("user_id"));
            userTradeHistoryDto.setOrderId(rs.getInt("order_id"));
            userTradeHistoryDto.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            userTradeHistoryDto.setDateAcceptance(rs.getTimestamp("accepted").toLocalDateTime());
            userTradeHistoryDto.setAmount(rs.getBigDecimal("amount"));
            userTradeHistoryDto.setPrice(rs.getBigDecimal("price"));
            userTradeHistoryDto.setTotal(rs.getBigDecimal("sum"));
            userTradeHistoryDto.setCommission(rs.getBigDecimal("commission"));
            userTradeHistoryDto.setOrderType(OrderType.fromOperationType(OperationType.convert(rs.getInt("operation_type_id"))));
            return userTradeHistoryDto;
        });
    }

    @Override
    public List<ExOrder> getAllOpenedOrdersByUserId(Integer userId) {
        String sql = "SELECT o.id AS order_id, " +
                "o.currency_pair_id, " +
                "o.operation_type_id, " +
                "o.exrate AS price, " +
                "o.amount_base AS amount, " +
                "o.amount_convert AS sum, " +
                "o.commission_id, " +
                "o.commission_fixed_amount, " +
                "o.date_creation AS created, " +
                "o.status_id, " +
                "o.base_type, " +
                "o.order_source_id " +
                " FROM EXORDERS o" +
                " WHERE o.user_id = :user_id AND o.status_id = :status_id";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("status_id", OrderStatus.OPENED.getStatus());

        return masterJdbcTemplate.query(sql, params, (rs, row) -> {
            ExOrder exOrder = new ExOrder();
            exOrder.setId(rs.getInt("order_id"));
            exOrder.setUserId(userId);
            exOrder.setCurrencyPairId(rs.getInt("currency_pair_id"));
            exOrder.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            exOrder.setExRate(rs.getBigDecimal("price"));
            exOrder.setAmountBase(rs.getBigDecimal("amount"));
            exOrder.setAmountConvert(rs.getBigDecimal("sum"));
            exOrder.setComissionId(rs.getInt("commission_id"));
            exOrder.setCommissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"));
            exOrder.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            exOrder.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            exOrder.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            exOrder.setSourceId(rs.getInt("order_source_id"));
            return exOrder;
        });
    }

    @Override
    public List<ExOrder> getOpenedOrdersByCurrencyPair(Integer userId, String currencyPair) {
        String sql = "SELECT o.id AS order_id, " +
                "o.currency_pair_id, " +
                "o.operation_type_id, " +
                "o.exrate AS price, " +
                "o.amount_base AS amount, " +
                "o.amount_convert AS sum, " +
                "o.commission_id, " +
                "o.commission_fixed_amount, " +
                "o.date_creation AS created, " +
                "o.status_id, " +
                "o.base_type, " +
                "o.order_source_id " +
                " FROM EXORDERS o" +
                " JOIN CURRENCY_PAIR cp on o.currency_pair_id = cp.id" +
                " WHERE o.user_id = :user_id AND cp.name = :currency_pair AND o.status_id = :status_id";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("currency_pair", currencyPair);
        params.put("status_id", OrderStatus.OPENED.getStatus());

        return masterJdbcTemplate.query(sql, params, (rs, row) -> {
            ExOrder exOrder = new ExOrder();
            exOrder.setId(rs.getInt("order_id"));
            exOrder.setUserId(userId);
            exOrder.setCurrencyPairId(rs.getInt("currency_pair_id"));
            exOrder.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            exOrder.setExRate(rs.getBigDecimal("price"));
            exOrder.setAmountBase(rs.getBigDecimal("amount"));
            exOrder.setAmountConvert(rs.getBigDecimal("sum"));
            exOrder.setComissionId(rs.getInt("commission_id"));
            exOrder.setCommissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"));
            exOrder.setDateCreation(rs.getTimestamp("created").toLocalDateTime());
            exOrder.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            exOrder.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            exOrder.setSourceId(rs.getInt("order_source_id"));
            return exOrder;
        });
    }

    @Override
    public List<TransactionDto> getOrderTransactions(Integer userId, Integer orderId) {
        OrderTableEnum tableEnum = getOrderTable(orderId);

        String sql = "SELECT t.id, " +
                "t.user_wallet_id, " +
                "t.amount, " +
                "t.commission_amount AS commission, " +
                "cur.name AS currency, " +
                "t.datetime AS time, " +
                "t.operation_type_id, " +
                "t.status_id AS transaction_status_id," +
                "o.status_id AS order_status_id" +
                " FROM TRANSACTION t" +
                " JOIN CURRENCY cur on t.currency_id = cur.id" +
                " JOIN " + tableEnum.name() + " o on o.id = t.source_id" +
                " WHERE (o.user_id = :user_id OR o.user_acceptor_id = :user_id)" +
                " AND t.source_id = :order_id" +
                " AND t.source_type = :source_type" +
                " ORDER BY t.id";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("order_id", orderId);
        params.put("source_type", ORDER.name());

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> TransactionDto.builder()
                .transactionId(rs.getInt("id"))
                .walletId(rs.getInt("user_wallet_id"))
                .amount(rs.getBigDecimal("amount"))
                .commission(rs.getBigDecimal("commission"))
                .currency(rs.getString("currency"))
                .time(rs.getTimestamp("time").toLocalDateTime())
                .operationType(OperationType.convert(rs.getInt("operation_type_id")))
                .orderStatus(OrderStatus.convert(rs.getInt("order_status_id")))
                .transactionStatus(TransactionStatus.convert(rs.getInt("transaction_status_id")))
                .build());
    }

    @Override
    public List<CurrencyPairTurnoverReportDto> getCurrencyPairTurnoverByPeriodAndRoles(LocalDateTime startTime,
                                                                                       LocalDateTime endTime,
                                                                                       List<UserRole> roles) {
        String sql = "SELECT MIN(cp.id) AS currency_pair_id, " +
                "cp.name AS currency_pair_name, " +
                "cur.name as convert_currency_name, " +
                "COUNT(o.id) AS quantity, " +
                "SUM(o.amount_convert) AS convert_amount," +
                "SUM((SELECT SUM(t.commission_amount) FROM TRANSACTION t WHERE t.source_type = 'ORDER' AND t.source_id = o.id AND t.operation_type_id <> 5)) AS commission_amount" +
                " FROM ORDERS o " +
                " JOIN CURRENCY_PAIR cp ON o.currency_pair_id = cp.id " +
                " JOIN CURRENCY cur ON cp.currency2_id = cur.id " +
                " JOIN USER creator ON creator.id = o.user_id AND creator.roleid IN (:user_roles) " +
                " JOIN USER acceptor ON acceptor.id = o.user_acceptor_id AND acceptor.roleid IN (:user_roles) " +
                " WHERE o.status_id = 3 AND o.operation_type_id IN (3, 4) AND o.date_acception BETWEEN :start_time AND :end_time" +
                " GROUP BY currency_pair_name, convert_currency_name" +
                " ORDER BY currency_pair_name ASC";

        Map<String, Object> params = new HashMap<>();
        params.put("start_time", Timestamp.valueOf(startTime));
        params.put("end_time", Timestamp.valueOf(endTime));
        params.put("user_roles", roles
                .stream()
                .map(UserRole::getRole)
                .collect(Collectors.toList()));

        return slaveJdbcTemplate.query(sql, params, (rs, row) -> CurrencyPairTurnoverReportDto.builder()
                .currencyPairId(rs.getInt("currency_pair_id"))
                .currencyPairName(rs.getString("currency_pair_name"))
                .currencyAccountingName(rs.getString("convert_currency_name"))
                .quantity(rs.getInt("quantity"))
                .amountConvert(rs.getBigDecimal("convert_amount"))
                .amountCommission(rs.getBigDecimal("commission_amount"))
                .build());
    }

    @Override
    public List<UserSummaryOrdersDto> getUserBuyOrdersDataByPeriodAndRoles(LocalDateTime startTime,
                                                                           LocalDateTime endTime,
                                                                           List<UserRole> userRoles,
                                                                           int requesterId) {
        String sql = "SELECT creator.email AS creator_email, " +
                "creator_role.name AS creator_role, " +
                "acceptor.email AS acceptor_email, " +
                "acceptor_role.name AS acceptor_role, " +
                "cp.name AS currency_pair_name, " +
                "cur.name as convert_currency_name, " +
                "SUM(o.amount_convert) AS convert_amount, " +
                "SUM((SELECT SUM(t.commission_amount) FROM TRANSACTION t WHERE t.source_type = 'ORDER' AND t.source_id = o.id AND t.operation_type_id <> 5)) AS commission_amount" +
                " FROM ORDERS o" +
                " JOIN CURRENCY_PAIR cp ON o.currency_pair_id = cp.id" +
                " JOIN CURRENCY cur ON cp.currency2_id = cur.id" +
                " JOIN USER creator ON creator.id = o.user_id" +
                " JOIN USER_ROLE creator_role on creator_role.id = creator.roleid AND creator_role.name IN (:user_roles)" +
                " JOIN USER acceptor ON acceptor.id = o.user_acceptor_id" +
                " JOIN USER_ROLE acceptor_role on acceptor_role.id = acceptor.roleid AND acceptor_role.name IN (:user_roles)" +
                " WHERE EXISTS (SELECT * FROM USER_CURRENCY_INVOICE_OPERATION_PERMISSION iop WHERE iop.currency_id = cur.id AND iop.user_id = :requester_user_id)" +
                " AND o.status_id = 3 AND o.operation_type_id = 4 AND o.date_acception BETWEEN :start_time AND :end_time" +
                " GROUP BY creator_email, acceptor_email, currency_pair_name, convert_currency_name";

        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("start_time", Timestamp.valueOf(startTime));
            put("end_time", Timestamp.valueOf(endTime));
            put("user_roles", userRoles
                    .stream()
                    .map(UserRole::getName)
                    .collect(Collectors.toList()));
            put("requester_user_id", requesterId);
        }};

        try {
            return slaveJdbcTemplate.query(sql, namedParameters, (rs, idx) -> UserSummaryOrdersDto.builder()
                    .creatorEmail(rs.getString("creator_email"))
                    .creatorRole(rs.getString("creator_role"))
                    .acceptorEmail(rs.getString("acceptor_email"))
                    .acceptorRole(rs.getString("acceptor_role"))
                    .currencyPairName(rs.getString("currency_pair_name"))
                    .currencyName(rs.getString("convert_currency_name"))
                    .amount(rs.getBigDecimal("convert_amount"))
                    .commission(rs.getBigDecimal("commission_amount"))
                    .build());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<UserSummaryOrdersDto> getUserSellOrdersDataByPeriodAndRoles(LocalDateTime startTime,
                                                                            LocalDateTime endTime,
                                                                            List<UserRole> userRoles,
                                                                            int requesterId) {
        String sql = "SELECT creator.email AS creator_email, " +
                "creator_role.name AS creator_role, " +
                "acceptor.email AS acceptor_email, " +
                "acceptor_role.name AS acceptor_role, " +
                "cp.name AS currency_pair_name, " +
                "cur.name as convert_currency_name, " +
                "SUM(o.amount_convert) AS convert_amount, " +
                "SUM((SELECT SUM(t.commission_amount) FROM TRANSACTION t WHERE t.source_type = 'ORDER' AND t.source_id = o.id AND t.operation_type_id <> 5)) AS commission_amount" +
                " FROM ORDERS o" +
                " JOIN CURRENCY_PAIR cp ON o.currency_pair_id = cp.id" +
                " JOIN CURRENCY cur ON cp.currency2_id = cur.id" +
                " JOIN USER creator ON creator.id = o.user_id" +
                " JOIN USER_ROLE creator_role on creator_role.id = creator.roleid AND creator_role.name IN (:user_roles)" +
                " JOIN USER acceptor ON acceptor.id = o.user_acceptor_id" +
                " JOIN USER_ROLE acceptor_role on acceptor_role.id = acceptor.roleid AND acceptor_role.name IN (:user_roles)" +
                " WHERE EXISTS (SELECT * FROM USER_CURRENCY_INVOICE_OPERATION_PERMISSION iop WHERE iop.currency_id = cur.id AND iop.user_id = :requester_user_id)" +
                " AND o.status_id = 3 AND o.operation_type_id = 3 AND o.date_acception BETWEEN :start_time AND :end_time" +
                " GROUP BY creator_email, acceptor_email, currency_pair_name, convert_currency_name";

        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("start_time", Timestamp.valueOf(startTime));
            put("end_time", Timestamp.valueOf(endTime));
            put("user_roles", userRoles
                    .stream()
                    .map(UserRole::getName)
                    .collect(Collectors.toList()));
            put("requester_user_id", requesterId);
        }};

        try {
            return slaveJdbcTemplate.query(sql, namedParameters, (rs, idx) -> UserSummaryOrdersDto.builder()
                    .creatorEmail(rs.getString("creator_email"))
                    .creatorRole(rs.getString("creator_role"))
                    .acceptorEmail(rs.getString("acceptor_email"))
                    .acceptorRole(rs.getString("acceptor_role"))
                    .currencyPairName(rs.getString("currency_pair_name"))
                    .currencyName(rs.getString("convert_currency_name"))
                    .amount(rs.getBigDecimal("convert_amount"))
                    .commission(rs.getBigDecimal("commission_amount"))
                    .build());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    /*todo: used in outdated api V1, select from all tables, REMOVE it?*/
    @Override
    public List<UserOrdersDto> getUserOrders(Integer userId, Integer currencyPairId, int limit, int offset) {

        String currencyPairSql = nonNull(currencyPairId) ? " AND EO.currency_pair_id = :currency_pair_id " : StringUtils.EMPTY;
        String limitSql = limit > 0 ? " LIMIT :limit " : StringUtils.EMPTY;
        String offsetSql = (limit > 0 && offset > 0) ? "OFFSET :offset" : StringUtils.EMPTY;

        String sql = "SELECT EO.id AS order_id, EO.amount_base, EO.exrate, CP.name AS currency_pair_name, EO.operation_type_id, " +
                " EO.date_creation, EO.date_acception FROM EXORDERS EO " +
                " JOIN CURRENCY_PAIR CP ON EO.currency_pair_id = CP.id " +
                " WHERE (EO.user_id = :user_id OR EO.user_acceptor_id = :user_id) AND EO.status_id = IN (:status_id) " + currencyPairSql +
                " ORDER BY EO.date_creation DESC " + limitSql + offsetSql;

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("currency_pair_id", currencyPairId);
        params.put("status_id", Arrays.asList(OrderStatus.CLOSED, OrderStatus.OPENED, OrderStatus.INPROCESS, OrderStatus.CANCELLED, OrderStatus.DELETED));
        params.put("limit", limit);
        params.put("offset", offset);
        return slaveJdbcTemplate.query(sql, params, userOrdersRowMapper);

    }


    @Override
    public Integer getMyOrdersWithStateCount(Integer userId, CurrencyPair currencyPair, String currencyName,
                                             OrderStatus orderStatus, String scope, Boolean hideCanceled,
                                             LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo, UserRole userRole) {
        String currencyPairClauseWhere = StringUtils.EMPTY;
        String currencyPairClauseWhereForStopLimit = StringUtils.EMPTY;
        if (nonNull(currencyPair) && currencyPair.getId() > 0) {
            currencyPairClauseWhere = " AND o.currency_pair_id = :currency_pair_id ";
            currencyPairClauseWhereForStopLimit = " AND so.currency_pair_id = :currency_pair_id ";
        }

        String createdClause = StringUtils.EMPTY;
        String createdStopLimitClause = StringUtils.EMPTY;
        if (Objects.nonNull(dateTimeFrom) && Objects.nonNull(dateTimeTo)) {
            createdClause = " AND (o.date_creation BETWEEN :date_from AND :date_before) ";
            createdStopLimitClause = " AND (so.date_creation BETWEEN :date_from AND :date_before) ";
        } else if (Objects.nonNull(dateTimeFrom)) {
            createdClause = " AND o.date_creation >= :date_from ";
            createdStopLimitClause = " AND so.date_creation >= :date_from ";
        } else if (Objects.nonNull(dateTimeTo)) {
            createdClause = " AND o.date_creation <= :date_before ";
            createdStopLimitClause = " AND so.date_creation <= :date_before ";
        }

        String currencyNameClause = isBlank(currencyName)
                ? StringUtils.EMPTY
                : " AND LOWER(cp.name) LIKE LOWER(:currency_name_part) ";

        String currencyNameJoinClause = StringUtils.EMPTY;
        String currencyNameJoinClauseForStopLimits = StringUtils.EMPTY;
        if (isNoneBlank(currencyName)) {
            currencyNameJoinClause = " JOIN CURRENCY_PAIR cp ON cp.id = o.currency_pair_id ";
            currencyNameJoinClauseForStopLimits = " JOIN CURRENCY_PAIR cp ON cp.id = so.currency_pair_id ";
        }

        String userFilterClause;
        switch (scope) {
            case "ALL":
                userFilterClause = " AND (o.user_id = :user_id OR o.user_acceptor_id = :user_id) ";
                break;
            case "ACCEPTED":
                userFilterClause = " AND o.user_acceptor_id = :user_id ";
                break;
            default:
                userFilterClause = " AND o.user_id = :user_id ";
                break;
        }

        OrderTableEnum orderTable = OrderTableEnum.getTableNameByStatusAndRole(orderStatus, userRole);

        String sqlFresh = "SELECT " +
                "(SELECT COUNT(o.id) FROM " + orderTable.name() + " o " +
                currencyNameJoinClause +
                " WHERE o.status_id in (:status_id) " +
                " AND o.operation_type_id IN (:operation_type_id) "
                + createdClause
                + currencyPairClauseWhere
                + currencyNameClause
                + userFilterClause +
                ") + " +
                "(SELECT COUNT(so.id) FROM STOP_ORDERS so " +
                currencyNameJoinClauseForStopLimits +
                " WHERE so.status_id in (:status_id) " +
                " AND so.operation_type_id IN (:operation_type_id) " +
                " AND so.user_id = :user_id " +
                createdStopLimitClause +
                currencyPairClauseWhereForStopLimit +
                currencyNameClause +
                ") " +
                " AS SumCount";

        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        params.put("status_id", getListOrderStatus(orderStatus, hideCanceled));
        params.put("operation_type_id", Arrays.asList(3, 4));
        if (nonNull(currencyPair) && currencyPair.getId() > 0) {
            params.put("currency_pair_id", currencyPair.getId());
        }
        if (nonNull(dateTimeFrom)) {
            params.put("date_from", dateTimeFrom);
        }
        if (nonNull(dateTimeTo)) {
            params.put("date_before", dateTimeTo);
        }
        if (isNotBlank(currencyName)) {
            params.put("currency_name_part", String.join(StringUtils.EMPTY, "%", currencyName, "%"));
        }

        try {
            return slaveJdbcTemplate.queryForObject(sqlFresh, params, Integer.TYPE);
        } catch (EmptyResultDataAccessException ex) {
            log.debug("Method 'OrderDaoImpl::getMyOrdersWithStateCount' did not return any result");
            return 0;
        }
    }

    private List<Integer> getListOrderStatus(OrderStatus orderStatus, boolean hideCanceled) {
        if (orderStatus == OrderStatus.OPENED) {
            return Collections.singletonList(OrderStatus.OPENED.getStatus());
        }
        if (hideCanceled) {
            return Arrays.asList(OrderStatus.CLOSED.getStatus(), OrderStatus.DELETED.getStatus());
        } else {
            return Arrays.asList(OrderStatus.CLOSED.getStatus(), OrderStatus.DELETED.getStatus(), OrderStatus.CANCELLED.getStatus());
        }
    }


    @Override
    public Optional<BigDecimal> getLastOrderPriceByCurrencyPair(int currencyPairId) {
        String sql = "SELECT last_rate FROM RATES WHERE currency_pair_id = :currency_pair_id ";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPairId);
        try {
            return Optional.of(masterJdbcTemplate.queryForObject(sql, namedParameters, BigDecimal.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<OrderListDto> findAllByOrderTypeAndCurrencyId(Integer currencyId, OrderType... orderTypes) {
        String orderTypeIds = Arrays.stream(orderTypes)
                .map(orderType -> String.valueOf(orderType.getOperationType().getType()))
                .collect(Collectors.joining(", "));

        String sql = "SELECT id, currency_pair_id, operation_type_id, exrate, amount_base, " +
                " amount_convert, commission_fixed_amount, date_creation, date_acception" +
                "  FROM EXORDERS " +
                "  WHERE status_id = 2 AND operation_type_id IN (:operationTypeIds) AND currency_pair_id=:currency_pair_id" +
//                "  AND date_creation >= (DATE_SUB(CURDATE(), INTERVAL 10 DAY))" +
                "  ORDER BY exrate ASC";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyId);
        namedParameters.put("operationTypeIds", orderTypeIds);
        return slaveJdbcTemplate.query(sql, namedParameters, openOrderListDtoRowMapper());
    }

    @Override
    public ExOrder getOrderById(int orderId, int userId) {
        OrderTableEnum tableEnum = getOrderTable(orderId);

        String sql = "SELECT * FROM " + tableEnum.name() + " WHERE id = :orderId AND (user_id = :userId OR user_acceptor_id = :userId)";

        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("orderId", String.valueOf(orderId));
        namedParameters.put("userId", String.valueOf(userId));

        try {
            return masterJdbcTemplate.queryForObject(sql, namedParameters, getExOrderRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getRatesDataForCache(Integer currencyPairId) {
        String whereClause = StringUtils.EMPTY;
        if (Objects.nonNull(currencyPairId)) {
            whereClause = " WHERE CP.id = :currency_pair_id ";
        }

        final String sql = "SELECT IFNULL(RAT.last_rate, 0) as last, IFNULL(RAT.previous_rate, 0) as pred_last, CP.id as id " +
                           "FROM CURRENCY_PAIR CP " +
                           "LEFT JOIN RATES RAT ON RAT.currency_pair_id = CP.id " +
                           whereClause;


        Map<String, Object> params = new HashMap<>();
        if (Objects.nonNull(currencyPairId)) {
            params.put("currency_pair_id", currencyPairId);
        }

        return slaveJdbcTemplate.query(sql, params, (rs, rowNum) -> ExOrderStatisticsShortByPairsDto.builder()
                .currencyPairId(rs.getInt("id"))
                .lastOrderRate(rs.getBigDecimal("last").toPlainString())
                .predLastOrderRate(rs.getBigDecimal("pred_last").toPlainString())
                .build());
    }

    @Override
    public ExOrderStatisticsShortByPairsDto getBeforeLastRateForCache(Integer currencyPairId) {

        String sql = "SELECT IFNULL(last_rate, 0) as last, IFNULL(previous_rate, 0) as pred_last, currency_pair_id  " +
                     "FROM RATES WHERE currency_pair_id = :currency_pair_id ";

        Map<String, Object> params = new HashMap<>();
        params.put("currency_pair_id", currencyPairId);

        List<ExOrderStatisticsShortByPairsDto> objectList = masterJdbcTemplate.query(sql, params, (rs, rowNum) -> ExOrderStatisticsShortByPairsDto.builder()
                .currencyPairId(rs.getInt("currency_pair_id"))
                .lastOrderRate(rs.getBigDecimal("last").toPlainString())
                .predLastOrderRate(rs.getBigDecimal("pred_last").toPlainString())
                .build());
        return objectList.stream()
                .findFirst()
                .orElseThrow(() -> {
                    String message = "Failed to find currency pair with id: " + currencyPairId;
                    log.warn(message);
                    return new RuntimeException(message);
                });
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getAllDataForCache(Integer currencyPairId) {
        String whereClause = StringUtils.EMPTY;
        if (Objects.nonNull(currencyPairId)) {
            whereClause = " WHERE CP2.id = :currency_pair_id ";
        }

        final String sql = "SELECT " +
                "                 AGR.id AS currency_pair_id, " +
                "                 AGR.name AS currency_pair_name, " +
                "                 CP2.scale AS currency_pair_precision, " +
                "                 CP2.market, " +
                "                 CP2.type AS currency_pair_type, " +
                "                 CP2.hidden, " +
                "                 AGR.baseVolume, " +
                "                 AGR.quoteVolume, " +
                "                 AGR.high24hr, " +
                "                 AGR.low24hr, " +
                "                (IFNULL ((SELECT last_rate FROM RATES WHERE  currency_pair_id = AGR.id), 0)) AS last24hr " +
                "                FROM " +
                "                   (SELECT " +
                "                       CP.name, " +
                "                       CP.id, " +
                "                       IFNULL(SUM(EO.amount_base), 0) AS baseVolume, " +
                "                       IFNULL(SUM(EO.amount_convert), 0) AS quoteVolume, " +
                "                       IFNULL(MAX(EO.exrate), 0) AS high24hr, " +
                "                       IFNULL(MIN(EO.exrate), 0) AS low24hr " +
                "                        FROM ORDERS EO " +
                "                        RIGHT JOIN CURRENCY_PAIR CP ON (CP.id = EO.currency_pair_id) AND EO.date_acception >= now() - INTERVAL 24 HOUR AND EO.status_id = 3 " +
                "                        GROUP BY CP.id " +
                "                     UNION ALL " +
                "                    SELECT " +
                "                        CP.name, " +
                "                        CP.id, " +
                "                        IFNULL(SUM(EO.amount_base), 0) AS baseVolume, " +
                "                        IFNULL(SUM(EO.amount_convert), 0) AS quoteVolume, " +
                "                        IFNULL(MAX(EO.exrate), 0) AS high24hr, " +
                "                        IFNULL(MIN(EO.exrate), 0) AS low24hr " +
                "                        FROM BOT_ORDERS EO " +
                "                        RIGHT JOIN CURRENCY_PAIR CP ON (CP.id = EO.currency_pair_id) AND EO.date_acception >= now() - INTERVAL 24 HOUR AND EO.status_id = 3 " +
                "                    GROUP BY CP.id " +
                "                ) AGR " +
                "JOIN CURRENCY_PAIR CP2 ON CP2.id = AGR.id " +
                whereClause +
                "GROUP BY AGR.id ";

        Map<String, Object> params = new HashMap<>();
        if (Objects.nonNull(currencyPairId)) {
            params.put("currency_pair_id", currencyPairId);
        }

        return slaveJdbcTemplate.query(sql, params, (rs, rowNum) -> ExOrderStatisticsShortByPairsDto.builder()
                .currencyPairId(rs.getInt("currency_pair_id"))
                .currencyPairName(rs.getString("currency_pair_name"))
                .currencyPairPrecision(rs.getInt("currency_pair_precision"))
                .type(CurrencyPairType.valueOf(rs.getString("currency_pair_type")))
                .market(rs.getString("market"))
                .hidden(rs.getBoolean("hidden"))
                .volume(rs.getBigDecimal("baseVolume").toPlainString())
                .currencyVolume(rs.getBigDecimal("quoteVolume").toPlainString())
                .high24hr(rs.getBigDecimal("high24hr").toPlainString())
                .low24hr(rs.getBigDecimal("low24hr").toPlainString())
                .lastOrderRate24hr(rs.getBigDecimal("last24hr").toPlainString())
                .build());
    }

    @Override
    public List<ExOrder> findAllMarketOrderCandidates(Integer currencyId, OperationType operationType) {
        String sortDirection = StringUtils.EMPTY;
        if (operationType == OperationType.BUY) {
            sortDirection = "DESC";
        } else if (operationType == OperationType.SELL) {
            sortDirection = "ASC";
        }
        String sql = "SELECT E.* FROM EXORDERS E" +
                " JOIN USER U ON U.id = E.user_id" +
                " JOIN USER_ROLE_SETTINGS URS ON URS.user_role_id = U.roleid" +
                " WHERE E.status_id = 2 AND E.operation_type_id = :typeId AND E.currency_pair_id = :pairId AND URS.order_acception_same_role_only = 0" +
                " ORDER BY E.exrate " + sortDirection +
                ", E.date_creation ASC FOR UPDATE";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("pairId", currencyId);
        namedParameters.put("typeId", operationType.getType());
        return masterJdbcTemplate.query(sql, namedParameters, getExOrderRowMapper());
    }

    @Override
    public Integer deleteClosedExorders() {
        final String sql = "DELETE FROM EXORDERS WHERE status_id > 2 LIMIT 300000";
        return masterJdbcTemplate.update(sql, Collections.EMPTY_MAP);
    }

    private RowMapper<ExOrder> getExOrderRowMapper() {
        return (rs, rowNum) -> {
            ExOrder exOrder = new ExOrder();
            exOrder.setId(rs.getInt("id"));
            exOrder.setUserId(rs.getInt("user_id"));
            exOrder.setCurrencyPairId(rs.getInt("currency_pair_id"));
            exOrder.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
            exOrder.setExRate(rs.getBigDecimal("exrate"));
            exOrder.setAmountBase(rs.getBigDecimal("amount_base"));
            exOrder.setAmountConvert(rs.getBigDecimal("amount_convert"));
            exOrder.setComissionId(rs.getInt("commission_id"));
            exOrder.setCommissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"));
            exOrder.setDateCreation(convertTimeStampToLocalDateTime(rs, "date_creation"));
            exOrder.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            exOrder.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
            exOrder.setUserAcceptorId(rs.getInt("user_acceptor_id"));
            exOrder.setSourceId(rs.getInt("order_source_id"));
            final Timestamp dateAcceptionTmsp = rs.getTimestamp("date_acception");
            if (Objects.nonNull(dateAcceptionTmsp)) {
                exOrder.setDateAcception(dateAcceptionTmsp.toLocalDateTime());
            }
            return exOrder;
        };
    }

    private RowMapper<OrderListDto> openOrderListDtoRowMapper() {
        return (rs, rowNum) -> {
            OrderListDto order = new OrderListDto();
            order.setId(rs.getInt("id"));
            order.setOrderType(OperationType.convert(rs.getInt("operation_type_id")));
            order.setExrate(rs.getString("exrate"));
            order.setAmountBase(rs.getString("amount_base"));
            order.setCreated(convertTimeStampToLocalDateTime(rs, "date_creation"));
            return order;
        };
    }

    private LocalDateTime convertTimeStampToLocalDateTime(ResultSet rs, String columnName) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(columnName);
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

    private RowMapper<OrderWideListDto> orderWithStateRowMapper(Locale locale, int userId) {
        return (rs, rowNum) -> {
            OrderWideListDto orderWideListDto = new OrderWideListDto();
            final int acceptorId = Optional.of(rs.getInt("user_acceptor_id")).orElse(0);
            String baseType = rs.getString("base_type");
            final OrderBaseType orderBaseType = OrderBaseType.valueOf(baseType);
            final OperationType operationType = OperationType.convert(rs.getInt("operation_type_id"));
            final String counterOrderType = rs.getString("counter_order_type");
            orderWideListDto.setId(rs.getInt("id"));
            orderWideListDto.setUserId(rs.getInt("user_id"));
            orderWideListDto.setUserAcceptorId(acceptorId);
            orderWideListDto.setOperationTypeEnum(getOperationTypeBasedOnUserId(userId, acceptorId, operationType));
            orderWideListDto.setAmountBase(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_base"), locale, 2));
            orderWideListDto.setAmountConvert(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount_convert"), locale, 2));
            orderWideListDto.setComissionId(rs.getInt("commission_id"));
            orderWideListDto.setCommissionFixedAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_fixed_amount"), locale, 2));
            BigDecimal amountWithCommission = rs.getBigDecimal("amount_convert");
            orderWideListDto.setCommissionValue(rs.getDouble("commission_value"));
            if (Objects.equals(OperationType.SELL, orderWideListDto.getOperationTypeEnum())) {
                amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.SUBTRACT);
            } else if (Objects.equals(OperationType.BUY, orderWideListDto.getOperationTypeEnum())) {
                amountWithCommission = BigDecimalProcessing.doAction(amountWithCommission, rs.getBigDecimal("commission_fixed_amount"), ActionType.ADD);
            }
            orderWideListDto.setAmountWithCommission(BigDecimalProcessing.formatLocale(amountWithCommission, locale, 2));
            LocalDateTime orderDate = userId == acceptorId
                    ? getLocalDateTime(rs, "date_acception")
                    : getLocalDateTime(rs, "date_creation");
            orderWideListDto.setDateCreation(orderDate);
            orderWideListDto.setStatus(OrderStatus.convert(rs.getInt("status_id")));
            orderWideListDto.setCurrencyPairId(rs.getInt("currency_pair_id"));
            orderWideListDto.setCurrencyPairName(rs.getString("currency_pair_name"));
            orderWideListDto.setOrderBaseType(orderBaseType);
            orderWideListDto.setChildOrderId(rs.getInt("child_order_id"));
            orderWideListDto.setDateStatusModification(getLocalDateTime(rs, "status_modification_date"));
            orderWideListDto.setDateModification(getLocalDateTime(rs, "date_modification"));

            if (StringUtils.isNotEmpty(counterOrderType) && counterOrderType.equalsIgnoreCase(OrderBaseType.MARKET.name())
                    && userId == acceptorId) {
                baseType = OrderBaseType.MARKET.name();
            }
            if (orderBaseType == OrderBaseType.LIMIT) {
                orderWideListDto.setExExchangeRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("exrate"), locale, 2));
                orderWideListDto.setDateAcception(getLocalDateTime(rs, "date_acception"));
                orderWideListDto.setDateStatusModification(getLocalDateTime(rs, "status_modification_date"));
            } else {
                orderWideListDto.setStopRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("stop_rate"), locale, 2));
                orderWideListDto.setLimitRate(BigDecimalProcessing.formatLocale(rs.getBigDecimal("limit_rate"), locale, 2));
                orderWideListDto.setDateModification(getLocalDateTime(rs, "date_modification"));
            }
            orderWideListDto.setOperationType(String.join(" ", getOperationTypeBasedOnUserId(userId, acceptorId, operationType).name(), baseType));
            return orderWideListDto;
        };
    }

    private OperationType getOperationTypeBasedOnUserId(int userId, int acceptorId, OperationType operationType) {
        if (userId == acceptorId) {
            if (operationType == OperationType.BUY) {
                return OperationType.SELL;
            }
            return OperationType.BUY;
        }
        return operationType;
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String columnName) {
        try {
            final Timestamp rsTimestamp = rs.getTimestamp(columnName);
            if (nonNull(rsTimestamp)) {
                return rsTimestamp.toLocalDateTime();
            }
        } catch (SQLException e) {
            log.warn("Failed to get timestamp data for column " + columnName, e);
        }
        return null;
    }

    @Override
    public OrderTableEnum getOrderTable(int id) {
        final String sql = String.format("SELECT" +
                "       COALESCE((SELECT '%s' FROM %s WHERE id = :id)," +
                "               (SELECT '%s' FROM %s WHERE id = :id AND status_id = %d)," +
                "               (SELECT '%s' FROM %s WHERE id = :id))" ,
                OrderTableEnum.ORDERS, OrderTableEnum.ORDERS,
                OrderTableEnum.EXORDERS, OrderTableEnum.EXORDERS, OrderStatus.OPENED.getStatus(),
                OrderTableEnum.BOT_ORDERS, OrderTableEnum.BOT_ORDERS);
        return OrderTableEnum.convertToOrderTableEnum(masterJdbcTemplate.queryForObject(sql, Collections.singletonMap("id", id), String.class));
    }

}
