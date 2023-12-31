package me.exrates.dao.impl;

import me.exrates.dao.InputOutputDao;
import me.exrates.model.dto.CurrencyInputOutputSummaryDto;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.PaginationWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;

/**
 * Created by ValkSam on 16.04.2017.
 */
@Repository
public class InputOutputDaoImpl implements InputOutputDao {

    private static final Logger log = LogManager.getLogger("inputoutput");

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private MessageSource messageSource;

    @Override
    public List<MyInputOutputHistoryDto> findMyInputOutputHistoryByOperationType(
            String email,
            Integer offset,
            Integer limit,
            List<Integer> operationTypeIdList,
            Locale locale) {
        String sql = " SELECT " +
                "    IF (WITHDRAW_REQUEST.date_creation IS NOT NULL, WITHDRAW_REQUEST.date_creation, REFILL_REQUEST.date_creation) AS datetime, " +
                "    CURRENCY.name as currency, TRANSACTION.amount, " +
                "    IF (WITHDRAW_REQUEST.id IS NOT NULL, (WITHDRAW_REQUEST.commission + WITHDRAW_REQUEST.merchant_commission), TRANSACTION.commission_amount) AS commission_amount, " +
                "    MERCHANT.name AS merchant,  " +
                "    TRANSACTION.source_type AS source_type, " +
                "    OPERATION_TYPE.name as operation_type, TRANSACTION.id AS transaction_id, " +
                "    IF (WITHDRAW_REQUEST.id IS NOT NULL, WITHDRAW_REQUEST.id, REFILL_REQUEST.id) AS operation_id," +
                "    (SELECT MAX(confirmation_number) FROM REFILL_REQUEST_CONFIRMATION RRC WHERE RRC.refill_request_id = REFILL_REQUEST.id) AS confirmation, " +
                "    IF(WITHDRAW_REQUEST.wallet IS NOT NULL, WITHDRAW_REQUEST.wallet, INVOICE_BANK.account_number) AS destination, " +
                "    USER.id AS user_id," +
                "    IF (WITHDRAW_REQUEST.status_id IS NOT NULL, WITHDRAW_REQUEST.status_id, REFILL_REQUEST.status_id) AS status_id," +
                "    IF (WITHDRAW_REQUEST.status_modification_date IS NOT NULL, WITHDRAW_REQUEST.status_modification_date, REFILL_REQUEST.status_modification_date) AS status_modification_date," +
                "    IF (WITHDRAW_REQUEST.user_full_name IS NOT NULL, WITHDRAW_REQUEST.user_full_name, RRP.user_full_name) AS user_full_name," +
                "    IF (WITHDRAW_REQUEST.remark IS NOT NULL, WITHDRAW_REQUEST.remark, REFILL_REQUEST.remark) AS remark," +
                "    IF (WITHDRAW_REQUEST.admin_holder_id IS NOT NULL, WITHDRAW_REQUEST.admin_holder_id, REFILL_REQUEST.admin_holder_id) AS admin_holder_id, " +
                "    IF (WITHDRAW_REQUEST.transaction_hash IS NOT NULL, WITHDRAW_REQUEST.transaction_hash, REFILL_REQUEST.merchant_transaction_id) AS transaction_hash" +
                "  FROM TRANSACTION " +
                "    left join CURRENCY on TRANSACTION.currency_id=CURRENCY.id" +
                "    left join WITHDRAW_REQUEST on TRANSACTION.source_type = 'WITHDRAW' AND WITHDRAW_REQUEST.id = TRANSACTION.source_id" +
                "    left join REFILL_REQUEST on TRANSACTION.source_type = 'REFILL' AND REFILL_REQUEST.id = TRANSACTION.source_id" +
                "    left join REFILL_REQUEST_ADDRESS RRA ON (RRA.id = REFILL_REQUEST.refill_request_address_id)  " +
                "    left join REFILL_REQUEST_PARAM RRP ON (RRP.id = REFILL_REQUEST.refill_request_param_id) " +
                "    left join INVOICE_BANK on INVOICE_BANK.id = RRP.recipient_bank_id " +
                "    left join MERCHANT on (MERCHANT.id = REFILL_REQUEST.merchant_id) OR (MERCHANT.id = WITHDRAW_REQUEST.merchant_id)" +
                "    left join OPERATION_TYPE on OPERATION_TYPE.id = TRANSACTION.operation_type_id" +
                "    left join WALLET on WALLET.id = TRANSACTION.user_wallet_id" +
                "    left join USER on WALLET.user_id=USER.id" +
                "  WHERE " +
                "    TRANSACTION.operation_type_id IN (:operation_type_id_list) and " +
                "    USER.email=:email " +
                "    AND TRANSACTION.source_type <>  'USER_TRANSFER'  " +

                "  UNION " +
                "  (SELECT " +
                "     RR.date_creation, " +
                "     CUR.name, RR.amount, NULL, " +
                "     M.name, " +
                "     'REFILL', " +
                "     'Input', NULL, " +
                "     RR.id, " +
                "     (SELECT MAX(confirmation_number) FROM REFILL_REQUEST_CONFIRMATION RRC WHERE RRC.refill_request_id = RR.id), " +
                "     INVOICE_BANK.account_number, " +
                "     USER.id, " +
                "     RR.status_id, " +
                "     RR.status_modification_date, " +
                "     RRP.user_full_name, " +
                "     RR.remark, " +
                "     RR.admin_holder_id, " +
                "     RR.merchant_transaction_id" +
                "   FROM REFILL_REQUEST RR " +
                "     JOIN CURRENCY CUR ON CUR.id=RR.currency_id " +
                "     JOIN USER USER ON USER.id=RR.user_id " +
                "     JOIN MERCHANT M ON M.id=RR.merchant_id " +
                "     LEFT JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = RR.refill_request_address_id)  " +
                "     LEFT JOIN REFILL_REQUEST_PARAM RRP ON (RRP.id = RR.refill_request_param_id) " +
                "     LEFT JOIN INVOICE_BANK on INVOICE_BANK.id = RRP.recipient_bank_id " +
                "   WHERE USER.email=:email AND " +
                "     NOT EXISTS(SELECT * FROM TRANSACTION TX WHERE TX.source_type='REFILL' AND TX.source_id=RR.id AND TX.operation_type_id=1) " +
                "  )  " +

                "  UNION " +
                "  (SELECT " +
                "     WR.date_creation, " +
                "     CUR.name, WR.amount, WR.commission + WR.merchant_commission, " +
                "     M.name, " +
                "     'WITHDRAW', " +
                "     'withdraw', NULL, " +
                "     WR.id, " +
                "     NULL, " +
                "     WR.wallet, " +
                "     USER.id, " +
                "     WR.status_id, " +
                "     WR.status_modification_date, " +
                "     WR.user_full_name, " +
                "     WR.remark, " +
                "     WR.admin_holder_id, " +
                "     WR.transaction_hash" +
                "   FROM WITHDRAW_REQUEST WR " +
                "     JOIN CURRENCY CUR ON CUR.id=WR.currency_id " +
                "     JOIN USER USER ON USER.id=WR.user_id " +
                "     JOIN MERCHANT M ON M.id=WR.merchant_id " +
                "   WHERE USER.email=:email AND " +
                "     NOT EXISTS(SELECT * FROM TRANSACTION TX WHERE TX.source_type='WITHDRAW' AND TX.source_id=WR.id AND TX.operation_type_id=2) " +
                "  )  " +

                "  UNION ALL " +
                "  (SELECT " +
                "     TR.date_creation, " +
                "     CUR.name, TR.amount, TR.commission, " +
                "     M.name, " +
                "     'USER_TRANSFER', " +
                "     'User transfer - Output', NULL, " +
                "     TR.id, " +
                "     NULL, " +
                "     TR.recipient_user_id, " +
                "     USER.id, " +
                "     TR.status_id, " +
                "     TR.status_modification_date, " +
                "     NULL, " +
                "     NULL, " +
                "     NULL, " +
                "     NULL" +
                "   FROM TRANSFER_REQUEST TR " +
                "     JOIN CURRENCY CUR ON CUR.id=TR.currency_id " +
                "     JOIN USER USER ON USER.id=TR.user_id " +
                "     JOIN MERCHANT M ON M.id=TR.merchant_id " +
                "   WHERE USER.email=:email " +
                "  )  " +
                "  UNION ALL " +
                "  (SELECT " +
                "     TR.date_creation, " +
                "     CUR.name, TR.amount, TR.commission, " +
                "     M.name, " +
                "     'USER_TRANSFER', " +
                "     'User transfer - Input', NULL, " +
                "     TR.id, " +
                "     NULL, " +
                "     TR.recipient_user_id, " +
                "     USER.id, " +
                "     TR.status_id, " +
                "     TR.status_modification_date, " +
                "     NULL, " +
                "     NULL, " +
                "     NULL, " +
                "     NULL" +
                "   FROM TRANSFER_REQUEST TR " +
                "     JOIN CURRENCY CUR ON CUR.id=TR.currency_id " +
                "     JOIN USER USER ON USER.id=TR.user_id " +
                "     JOIN USER REC ON REC.id = TR.recipient_user_id  " +
                "     JOIN MERCHANT M ON M.id=TR.merchant_id " +
                "   WHERE REC.email=:email AND TR.status_id = 2 " +
                "  )  " +
                "  UNION ALL " +
                "  (SELECT " +
                "     TR.datetime, " +
                "     CUR.name, TR.amount, NULL, " +
                "     NULL, " +
                "     'NOTIFICATIONS', " +
                "     TR.description, NULL, " +
                "     TR.id, " +
                "     NULL, " +
                "     NULL , " +
                "     U.id, " +
                "     NULL, " +
                "     NULL," +
                "     NULL, " +
                "     NULL, " +
                "     NULL, " +
                "     NULL" +
                "   FROM TRANSACTION TR " +
                "     JOIN CURRENCY CUR ON CUR.id=TR.currency_id " +
                "     JOIN WALLET W ON W.id = TR.user_wallet_id AND W.currency_id = CUR.id " +
                "     JOIN USER U ON U.id=W.user_id " +
                "   WHERE U.email=:email AND TR.source_type='NOTIFICATIONS'" +
                "  )  " +
                "  ORDER BY datetime DESC, operation_id DESC " +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
        final Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("operation_type_id_list", operationTypeIdList);
        return jdbcTemplate.query(sql, params, (rs, i) -> {
            MyInputOutputHistoryDto myInputOutputHistoryDto = new MyInputOutputHistoryDto();
            Timestamp datetime = rs.getTimestamp("datetime");
            myInputOutputHistoryDto.setDatetime(datetime == null ? null : datetime.toLocalDateTime());
            myInputOutputHistoryDto.setCurrencyName(rs.getString("currency"));
            myInputOutputHistoryDto.setAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount"), locale, 2));
            myInputOutputHistoryDto.setCommissionAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_amount"), locale, 2));
            myInputOutputHistoryDto.setMerchantName(rs.getString("merchant"));
            myInputOutputHistoryDto.setOperationType(rs.getString("operation_type"));
            myInputOutputHistoryDto.setConfirmation(rs.getInt("confirmation"));
            myInputOutputHistoryDto.setTransactionId(rs.getInt("transaction_id"));
            myInputOutputHistoryDto.setProvided(myInputOutputHistoryDto.getTransactionId() == null ? 0 : 1);
            myInputOutputHistoryDto.setTransactionProvided(myInputOutputHistoryDto.getProvided() == 0 ?
                    messageSource.getMessage("inputoutput.statusFalse", null, locale) :
                    messageSource.getMessage("inputoutput.statusTrue", null, locale));
            myInputOutputHistoryDto.setId(rs.getInt("operation_id"));
            myInputOutputHistoryDto.setUserId(rs.getInt("user_id"));
            myInputOutputHistoryDto.setBankAccount(rs.getString("destination"));
            TransactionSourceType sourceType = TransactionSourceType.convert(rs.getString("source_type"));
            myInputOutputHistoryDto.setSourceType(sourceType);
            myInputOutputHistoryDto.setStatus(rs.getInt("status_id"));
            Timestamp dateModification = rs.getTimestamp("status_modification_date");
            myInputOutputHistoryDto.setStatusUpdateDate(dateModification == null ? null : dateModification.toLocalDateTime());
            myInputOutputHistoryDto.setUserFullName(rs.getString("user_full_name"));
            myInputOutputHistoryDto.setRemark(rs.getString("remark"));
            myInputOutputHistoryDto.setAdminHolderId(rs.getInt("admin_holder_id"));
            myInputOutputHistoryDto.setTransactionHash(rs.getString("transaction_hash"));
            return myInputOutputHistoryDto;
        });
    }

    @Override
    public PaginationWrapper<List<MyInputOutputHistoryDto>> findUnconfirmedInvoices(Integer userId, Integer currencyId, @Nullable Integer limit, @Nullable Integer offset) {
        String limitSql = "";
        String offsetSql = "";
        Map<String, Object> params = new HashMap<>();
        if (limit != null) {
            limitSql = " LIMIT :limit ";
            params.put("limit", limit);
            if (offset != null) {
                offsetSql = " OFFSET :offset";
                params.put("offset", offset);
            }
        }
        String sql = "SELECT RR.id AS request_id, RR.date_creation AS datetime, CUR.name AS currency, RR.amount, COM.value AS commission_value," +
                " MER.name AS merchant, RR.user_id, IB.account_number AS destination, RR.status_id, RR.status_modification_date," +
                " RRP.user_full_name, RR.remark, RR.admin_holder_id, RR.merchant_transaction_id AS transaction_hash" +
                " FROM REFILL_REQUEST RR " +
                " JOIN CURRENCY CUR ON RR.currency_id = CUR.id" +
                " JOIN MERCHANT MER ON RR.merchant_id = MER.id" +
                " JOIN COMMISSION COM ON RR.commission_id = COM.id" +
                " LEFT JOIN REFILL_REQUEST_PARAM RRP ON RR.refill_request_param_id = RRP.id" +
                " LEFT JOIN INVOICE_BANK IB ON RRP.recipient_bank_id = IB.id " +
                " WHERE RR.user_id = :user_id AND RR.currency_id = :currency_id AND RR.status_id IN (:status_ids) " +
                " ORDER BY datetime DESC "
                + limitSql + offsetSql;
        String sqlCount = "SELECT COUNT(*) " +
                " FROM REFILL_REQUEST RR " +
                " JOIN CURRENCY CUR ON RR.currency_id = CUR.id" +
                " JOIN MERCHANT MER ON RR.merchant_id = MER.id" +
                " JOIN COMMISSION COM ON RR.commission_id = COM.id" +
                " WHERE RR.user_id = :user_id AND RR.currency_id = :currency_id AND RR.status_id IN (:status_ids) ";

        params.put("user_id", userId);
        params.put("currency_id", currencyId);
        params.put("status_ids", Arrays.asList(RefillStatusEnum.WAITING_CONFIRMATION_USER.getCode(), RefillStatusEnum.DECLINED_ADMIN.getCode()));

        Integer count = jdbcTemplate.queryForObject(sqlCount, params, Integer.class);
        List<MyInputOutputHistoryDto> result = jdbcTemplate.query(sql, params, (rs, i) -> {
            MyInputOutputHistoryDto myInputOutputHistoryDto = new MyInputOutputHistoryDto();
            myInputOutputHistoryDto.setId(rs.getInt("request_id"));
            Timestamp datetime = rs.getTimestamp("datetime");
            myInputOutputHistoryDto.setDatetime(datetime == null ? null : datetime.toLocalDateTime());
            myInputOutputHistoryDto.setCurrencyName(rs.getString("currency"));
            BigDecimal amount = rs.getBigDecimal("amount");
            BigDecimal commissionValue = rs.getBigDecimal("commission_value");
            BigDecimal commissionAmount = BigDecimalProcessing.doAction(amount, commissionValue, ActionType.MULTIPLY_PERCENT);
            myInputOutputHistoryDto.setAmount(BigDecimalProcessing.formatNonePoint(amount, 2));
            myInputOutputHistoryDto.setCommissionAmount(BigDecimalProcessing.formatNonePoint(commissionAmount, 2));
            myInputOutputHistoryDto.setMerchantName(rs.getString("merchant"));
            myInputOutputHistoryDto.setOperationType(OperationType.INPUT.name());
            myInputOutputHistoryDto.setUserId(rs.getInt("user_id"));
            myInputOutputHistoryDto.setBankAccount(rs.getString("destination"));
            myInputOutputHistoryDto.setSourceType(TransactionSourceType.REFILL);
            myInputOutputHistoryDto.setStatus(rs.getInt("status_id"));
            Timestamp dateModification = rs.getTimestamp("status_modification_date");
            myInputOutputHistoryDto.setStatusUpdateDate(dateModification == null ? null : dateModification.toLocalDateTime());
            myInputOutputHistoryDto.setUserFullName(rs.getString("user_full_name"));
            myInputOutputHistoryDto.setRemark(rs.getString("remark"));
            myInputOutputHistoryDto.setAdminHolderId(rs.getInt("admin_holder_id"));
            myInputOutputHistoryDto.setTransactionHash(rs.getString("transaction_hash"));
            return myInputOutputHistoryDto;
        });

        return new PaginationWrapper<>(result, count, limit == null ? 0 : limit);

    }


    @Override
    public List<CurrencyInputOutputSummaryDto> getInputOutputSummary(LocalDateTime startTime, LocalDateTime endTime, List<Integer> userRoleIdList) {
        String sql = "SELECT CUR.name AS currency_name, " +
                //wolper 19.04.18
                // MIN is used for performance reason
                // as an alternative to additional "group by CUR.ID"
                "MIN(CUR.ID)" +
                " as currency_id, ifnull(SUM(refill), 0) AS input, ifnull(SUM(withdraw), 0) AS output FROM " +
                "  (SELECT TX.currency_id, TX.amount AS refill, 0 AS withdraw FROM TRANSACTION TX " +
                "    JOIN WALLET W ON TX.user_wallet_id = W.id " +
                "    JOIN USER U ON W.user_id = U.id AND U.roleid IN (:user_roles) " +
                "  WHERE TX.operation_type_id = 1 AND TX.source_type = 'REFILL' " +
                "  AND TX.datetime BETWEEN STR_TO_DATE(:start_time, '%Y-%m-%d %H:%i:%s') " +
                "  AND STR_TO_DATE(:end_time, '%Y-%m-%d %H:%i:%s') " +

                "   UNION ALL" +

                "   SELECT TX.currency_id, 0 AS refill, TX.amount AS withdraw FROM TRANSACTION TX " +
                "     JOIN WALLET W ON TX.user_wallet_id = W.id " +
                "     JOIN USER U ON W.user_id = U.id AND U.roleid IN (:user_roles) " +
                "   WHERE TX.operation_type_id = 2 AND TX.source_type = 'WITHDRAW' " +
                "   AND TX.datetime BETWEEN STR_TO_DATE(:start_time, '%Y-%m-%d %H:%i:%s') " +
                "   AND STR_TO_DATE(:end_time, '%Y-%m-%d %H:%i:%s') " +
                "  ) AGGR " +
                "RIGHT JOIN CURRENCY CUR ON AGGR.currency_id = CUR.id " +
                "GROUP BY currency_name ORDER BY currency_id ASC";
        Map<String, Object> params = new HashMap<>();
        params.put("start_time", Timestamp.valueOf(startTime));
        params.put("end_time", Timestamp.valueOf(endTime));
        params.put("user_roles", userRoleIdList);

        return jdbcTemplate.query(sql, params, (rs, row) -> {
            CurrencyInputOutputSummaryDto dto = new CurrencyInputOutputSummaryDto();
            dto.setOrderNum(row + 1);
            //wolper 19.04.18
            //id added
            dto.setCurId(rs.getInt("currency_id"));
            dto.setCurrencyName(rs.getString("currency_name"));
            dto.setInput(rs.getBigDecimal("input"));
            dto.setOutput(rs.getBigDecimal("output"));
            dto.calculateAndSetDiff();
            return dto;
        });
    }


    @Override
    public List<InOutReportDto> getInputOutputSummaryWithCommissions(LocalDateTime startTime, LocalDateTime endTime, List<Integer> userRoleIdList) {
        String sql = "SELECT CUR.name AS currency_name, " +
                //wolper 19.04.18
                // MIN is used for performance reason
                // as an alternative to additional "group by CUR.ID"
                "MIN(CUR.ID)" +
                "  as currency_id,SUM(refill) AS input, SUM(withdraw) AS output, " +
                "  SUM(commission_refill) AS commission_in, SUM(commission_withdraw) AS commission_out " +
                "FROM " +
                "              (SELECT TX.currency_id, TX.amount AS refill, TX.commission_amount AS commission_refill, " +
                "                 0 AS withdraw, 0 AS commission_withdraw FROM TRANSACTION TX " +
                "                JOIN WALLET W ON TX.user_wallet_id = W.id " +
                "                JOIN USER U ON W.user_id = U.id AND U.roleid IN (:user_roles) " +
                "              WHERE TX.operation_type_id = 1 AND TX.source_type = 'REFILL' " +
                "              AND TX.datetime BETWEEN STR_TO_DATE(:start_time, '%Y-%m-%d %H:%i:%s') " +
                "              AND STR_TO_DATE(:end_time, '%Y-%m-%d %H:%i:%s') " +

                "               UNION ALL" +

                "               SELECT TX.currency_id, 0 AS refill, 0 AS commission_refill, " +
                "                 TX.amount AS withdraw, TX.commission_amount AS commission_withdraw FROM TRANSACTION TX " +
                "                 JOIN WALLET W ON TX.user_wallet_id = W.id " +
                "                 JOIN USER U ON W.user_id = U.id AND U.roleid IN (:user_roles) " +
                "               WHERE TX.operation_type_id = 2 AND TX.source_type = 'WITHDRAW' " +
                "               AND TX.datetime BETWEEN STR_TO_DATE(:start_time, '%Y-%m-%d %H:%i:%s') " +
                "               AND STR_TO_DATE(:end_time, '%Y-%m-%d %H:%i:%s') " +
                "              ) AGGR " +
                "            RIGHT JOIN CURRENCY CUR ON AGGR.currency_id = CUR.id " +
                "            GROUP BY currency_name ORDER BY currency_id ASC;";
        Map<String, Object> params = new HashMap<>();
        params.put("start_time", Timestamp.valueOf(startTime));
        params.put("end_time", Timestamp.valueOf(endTime));
        params.put("user_roles", userRoleIdList);

        return jdbcTemplate.query(sql, params, (rs, row) -> {
            InOutReportDto dto = new InOutReportDto();
            dto.setOrderNum(row + 1);
            dto.setCurrencyName(rs.getString("currency_name"));
            dto.setInput(rs.getBigDecimal("input"));
            dto.setOutput(rs.getBigDecimal("output"));
            dto.setInputCommission(rs.getBigDecimal("commission_in"));
            dto.setOutputCommission(rs.getBigDecimal("commission_out"));
            //wolper 19.04.18
            //currency id added
            dto.setCurrencyId(rs.getInt("currency_id"));
            return dto;
        });
    }

    @Override
    public List<MyInputOutputHistoryDto> findMyInputOutputHistoryByOperationType(String userEmail, Integer currencyId, String currencyName,
                                                                                 LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo,
                                                                                 Integer limit, Integer offset, List<Integer> operationTypesList,
                                                                                 Locale locale) {
        String limitStr = limit < 1 ? StringUtils.EMPTY : String.format(" LIMIT %d ", limit);
        String offsetStr = offset < 1 ? StringUtils.EMPTY : String.format(" OFFSET %d ", offset);

        String currencyCondition = StringUtils.EMPTY;
        String curId = StringUtils.EMPTY;
        if (currencyId > 0) {
            currencyCondition = " TRANSACTION.currency_id = :currencyId AND ";
            curId = " AND CUR.id = :currencyId ";
        } else if (currencyId == 0 && StringUtils.isNotBlank(currencyName)) {
            currencyCondition = " TRANSACTION.currency_id IN (SELECT CUR.id FROM CURRENCY CUR WHERE LOWER(CUR.name) LIKE LOWER(:currencyNamePart)) AND ";
            curId = " AND LOWER(CUR.name) LIKE LOWER(:currencyNamePart) ";
        }

        String dateClauseTransaction = StringUtils.EMPTY;
        String dateClauseRefillRequest = StringUtils.EMPTY;
        String dateClauseWithdrawRequest = StringUtils.EMPTY;
        String dateClauseTransferRequest = StringUtils.EMPTY;
        String dateClauseTransferRequestTr = StringUtils.EMPTY;
        if (Objects.nonNull(dateTimeFrom) && Objects.nonNull(dateTimeTo)) {
            dateClauseTransaction = " (TRANSACTION.datetime BETWEEN :dateFrom AND :dateTo) AND ";
            dateClauseRefillRequest = " AND (RR.date_creation BETWEEN :dateFrom AND :dateTo) ";
            dateClauseWithdrawRequest = " AND (WR.date_creation BETWEEN :dateFrom AND :dateTo) ";
            dateClauseTransferRequest = " AND (TR.date_creation BETWEEN :dateFrom AND :dateTo) ";
            dateClauseTransferRequestTr = " AND (TR.datetime BETWEEN :dateFrom AND :dateTo) ";
        } else if (Objects.nonNull(dateTimeFrom)) {
            dateClauseTransaction = " TRANSACTION.datetime >= :dateFrom AND ";
            dateClauseRefillRequest = " AND RR.date_creation >= :dateFrom ";
            dateClauseWithdrawRequest = " AND WR.date_creation >= :dateFrom ";
            dateClauseTransferRequest = " AND TR.date_creation >= :dateFrom ";
            dateClauseTransferRequestTr = " AND TR.datetime >= :dateFrom ";
        } else if (Objects.nonNull(dateTimeTo)) {
            dateClauseTransaction = " TRANSACTION.datetime <= :dateTo AND ";
            dateClauseRefillRequest = " AND RR.date_creation <= :dateTo ";
            dateClauseWithdrawRequest = " AND WR.date_creation <= :dateTo ";
            dateClauseTransferRequest = " AND TR.date_creation <= :dateTo ";
            dateClauseTransferRequestTr = " AND TR.datetime <= :dateTo ";
        }

        String sql = " SELECT " +
                "    IFNULL(IFNULL(WITHDRAW_REQUEST.date_creation,REFILL_REQUEST.date_creation),TRANSACTION.datetime) AS datetime, " +
                "    CURRENCY.name as currency, TRANSACTION.amount, " +
                "    IF (WITHDRAW_REQUEST.id IS NOT NULL, (WITHDRAW_REQUEST.commission + WITHDRAW_REQUEST.merchant_commission), TRANSACTION.commission_amount) AS commission_amount, " +
                "    MERCHANT.name AS merchant,  " +
                "    TRANSACTION.source_type AS source_type, " +
                "    OPERATION_TYPE.name as operation_type, TRANSACTION.id AS transaction_id, " +
                "    IF (WITHDRAW_REQUEST.id IS NOT NULL, WITHDRAW_REQUEST.id, REFILL_REQUEST.id) AS operation_id, " +
                "    (SELECT MAX(confirmation_number) FROM REFILL_REQUEST_CONFIRMATION RRC WHERE RRC.refill_request_id = REFILL_REQUEST.id) AS confirmation, " +
                "    IF(WITHDRAW_REQUEST.wallet IS NOT NULL, WITHDRAW_REQUEST.wallet, INVOICE_BANK.account_number) AS destination, " +
                "    USER.id AS user_id, " +
                "    IF (WITHDRAW_REQUEST.status_id IS NOT NULL, WITHDRAW_REQUEST.status_id, REFILL_REQUEST.status_id) AS status_id, " +
                "    IF (WITHDRAW_REQUEST.status_modification_date IS NOT NULL, WITHDRAW_REQUEST.status_modification_date, REFILL_REQUEST.status_modification_date) AS status_modification_date, " +
                "    IF (WITHDRAW_REQUEST.user_full_name IS NOT NULL, WITHDRAW_REQUEST.user_full_name, RRP.user_full_name) AS user_full_name, " +
                "    IF (WITHDRAW_REQUEST.remark IS NOT NULL, WITHDRAW_REQUEST.remark, REFILL_REQUEST.remark) AS remark, " +
                "    IF (WITHDRAW_REQUEST.admin_holder_id IS NOT NULL, WITHDRAW_REQUEST.admin_holder_id, REFILL_REQUEST.admin_holder_id) AS admin_holder_id, " +
                "    IF (WITHDRAW_REQUEST.transaction_hash IS NOT NULL, WITHDRAW_REQUEST.transaction_hash, REFILL_REQUEST.merchant_transaction_id) AS transaction_hash " +
                "  FROM TRANSACTION " +
                "    left join CURRENCY on TRANSACTION.currency_id=CURRENCY.id " +
                "    left join WITHDRAW_REQUEST on TRANSACTION.source_type = 'WITHDRAW' AND WITHDRAW_REQUEST.id = TRANSACTION.source_id " +
                "    left join REFILL_REQUEST on TRANSACTION.source_type = 'REFILL' AND REFILL_REQUEST.id = TRANSACTION.source_id " +
                "    left join REFILL_REQUEST_ADDRESS RRA ON (RRA.id = REFILL_REQUEST.refill_request_address_id) " +
                "    left join REFILL_REQUEST_PARAM RRP ON (RRP.id = REFILL_REQUEST.refill_request_param_id) " +
                "    left join INVOICE_BANK on INVOICE_BANK.id = RRP.recipient_bank_id " +
                "    left join MERCHANT on (MERCHANT.id = REFILL_REQUEST.merchant_id) OR (MERCHANT.id = WITHDRAW_REQUEST.merchant_id) " +
                "    left join OPERATION_TYPE on OPERATION_TYPE.id = TRANSACTION.operation_type_id " +
                "    left join WALLET on WALLET.id = TRANSACTION.user_wallet_id " +
                "    left join USER on WALLET.user_id=USER.id " +
                "  WHERE " +
                "    TRANSACTION.operation_type_id IN (:operation_type_id_list) AND " +
                currencyCondition +
                dateClauseTransaction +
                "    USER.email = :email " +
                "    AND TRANSACTION.source_type IN ('REFILL', 'WITHDRAW', 'IEO', 'FREE_COINS_TRANSFER') " +
                "    AND TRANSACTION.status_id IN (1, 2)" +

                "  UNION " +
                "  (SELECT " +
                "     RR.date_creation, " +
                "     CUR.name, RR.amount, NULL, " +
                "     M.name, " +
                "     'REFILL', " +
                "     'Input', NULL, " +
                "     RR.id, " +
                "     (SELECT MAX(confirmation_number) FROM REFILL_REQUEST_CONFIRMATION RRC WHERE RRC.refill_request_id = RR.id), " +
                "     INVOICE_BANK.account_number, " +
                "     USER.id, " +
                "     RR.status_id, " +
                "     RR.status_modification_date, " +
                "     RRP.user_full_name, " +
                "     RR.remark, " +
                "     RR.admin_holder_id, " +
                "     RR.merchant_transaction_id " +
                "   FROM REFILL_REQUEST RR " +
                "     JOIN CURRENCY CUR ON CUR.id=RR.currency_id " +
                "     JOIN USER USER ON USER.id=RR.user_id " +
                "     JOIN MERCHANT M ON M.id=RR.merchant_id " +
                "     LEFT JOIN REFILL_REQUEST_ADDRESS RRA ON (RRA.id = RR.refill_request_address_id)  " +
                "     LEFT JOIN REFILL_REQUEST_PARAM RRP ON (RRP.id = RR.refill_request_param_id) " +
                "     LEFT JOIN INVOICE_BANK on INVOICE_BANK.id = RRP.recipient_bank_id " +
                "   WHERE USER.email=:email " +
                " AND RR.status_id IN (8, 9, 10, 11, 12) " +
                " AND NOT EXISTS(SELECT * FROM TRANSACTION TX WHERE TX.source_type = 'REFILL' " +
                " AND TX.source_id = RR.id AND TX.operation_type_id = 1) " +
                curId +
                dateClauseRefillRequest +
                "  )  " +
                "  UNION " +
                "  (SELECT " +
                "     WR.date_creation, " +
                "     CUR.name, WR.amount, WR.commission + WR.merchant_commission, " +
                "     M.name, " +
                "     'WITHDRAW', " +
                "     'withdraw', NULL, " +
                "     WR.id, " +
                "     NULL, " +
                "     WR.wallet, " +
                "     USER.id, " +
                "     WR.status_id, " +
                "     WR.status_modification_date, " +
                "     WR.user_full_name, " +
                "     WR.remark, " +
                "     WR.admin_holder_id, " +
                "     WR.transaction_hash " +
                "   FROM WITHDRAW_REQUEST WR " +
                "     JOIN CURRENCY CUR ON CUR.id=WR.currency_id " +
                "     JOIN USER USER ON USER.id=WR.user_id " +
                "     JOIN MERCHANT M ON M.id=WR.merchant_id " +
                "   WHERE USER.email=:email " +
                " AND WR.status_id IN (7, 8, 9, 10, 12) " +
                " AND NOT EXISTS(SELECT * FROM TRANSACTION TX WHERE TX.source_type='WITHDRAW' " +
                " AND TX.source_id = WR.id AND TX.operation_type_id = 2) " +
                curId +
                dateClauseWithdrawRequest +
                "  )  " +
                "  UNION ALL " +
                "  (SELECT " +
                "     TR.date_creation, " +
                "     CUR.name, TR.amount, TR.commission, " +
                "     M.name, " +
                "     'USER_TRANSFER', " +
                "     'User transfer - Output', NULL, " +
                "     TR.id, " +
                "     NULL, " +
                "     TR.recipient_user_id, " +
                "     USER.id, " +
                "     TR.status_id, " +
                "     TR.status_modification_date, " +
                "     NULL, " +
                "     NULL, " +
                "     NULL, " +
                "     NULL " +
                "   FROM TRANSFER_REQUEST TR " +
                "     JOIN CURRENCY CUR ON CUR.id=TR.currency_id " +
                "     JOIN USER USER ON USER.id=TR.user_id " +
                "     JOIN MERCHANT M ON M.id=TR.merchant_id " +
                "   WHERE USER.email=:email " +
                " AND TR.status_id IN (2, 3, 5) " +
                curId +
                dateClauseTransferRequest +
                "  )  " +
                "  UNION ALL " +
                "  (SELECT " +
                "     TR.date_creation, " +
                "     CUR.name, TR.amount, TR.commission, " +
                "     M.name, " +
                "     'USER_TRANSFER', " +
                "     'User transfer - Input', NULL, " +
                "     TR.id, " +
                "     NULL, " +
                "     TR.recipient_user_id, " +
                "     USER.id, " +
                "     TR.status_id, " +
                "     TR.status_modification_date, " +
                "     NULL, " +
                "     NULL, " +
                "     NULL, " +
                "     NULL " +
                "   FROM TRANSFER_REQUEST TR " +
                "     JOIN CURRENCY CUR ON CUR.id=TR.currency_id " +
                "     JOIN USER USER ON USER.id=TR.user_id " +
                "     JOIN USER REC ON REC.id = TR.recipient_user_id  " +
                "     JOIN MERCHANT M ON M.id=TR.merchant_id " +
                "   WHERE REC.email=:email AND TR.status_id = 2 " +
                " AND TR.status_id IN (2, 3, 5) " +
                curId +
                dateClauseTransferRequest +
                "  )  " +
                "  UNION ALL " +
                "  (SELECT " +
                "     TR.datetime, " +
                "     CUR.name, TR.amount, NULL, " +
                "     NULL, " +
                "     'NOTIFICATIONS', " +
                "     TR.description, NULL, " +
                "     TR.id, " +
                "     NULL, " +
                "     NULL , " +
                "     U.id, " +
                "     NULL, " +
                "     NULL," +
                "     NULL, " +
                "     NULL, " +
                "     NULL, " +
                "     NULL " +
                "   FROM TRANSACTION TR " +
                "     JOIN CURRENCY CUR ON CUR.id=TR.currency_id " +
                "     JOIN WALLET W ON W.id = TR.user_wallet_id AND W.currency_id = CUR.id " +
                "     JOIN USER U ON U.id=W.user_id " +
                "   WHERE U.email=:email " +
                " AND TR.source_type='NOTIFICATIONS' " +
                " AND TR.status_id IN (2, 3, 5) " +
                curId +
                dateClauseTransferRequestTr +
                "  )  " +
                "  ORDER BY datetime DESC, operation_id DESC " + limitStr + offsetStr;

        Map<String, Object> params = new HashMap<>();
        params.put("email", userEmail);
        if (Objects.nonNull(dateTimeFrom)) {
            params.put("dateFrom", Timestamp.valueOf(dateTimeFrom));
        }
        if (Objects.nonNull(dateTimeTo)) {
            params.put("dateTo", Timestamp.valueOf(dateTimeTo));
        }
        params.put("operation_type_id_list", operationTypesList);
        if (currencyId > 0) {
            params.put("currencyId", currencyId);
        } else if (currencyId == 0 && StringUtils.isNotBlank(currencyName)) {
            params.put("currencyNamePart", String.join(StringUtils.EMPTY, "%", currencyName, "%"));
        }

        try {
            return jdbcTemplate.query(sql, params, rowMapper(locale));
        } catch (EmptyResultDataAccessException ex) {
            log.debug("Method 'InputOutputDaoImpl::findMyInputOutputHistoryByOperationType' did not return any result");
            return Collections.emptyList();
        }
    }

    private RowMapper<MyInputOutputHistoryDto> rowMapper(Locale locale) {
        return (rs, i) -> {
            MyInputOutputHistoryDto myInputOutputHistoryDto = new MyInputOutputHistoryDto();
            Timestamp datetime = rs.getTimestamp("datetime");
            myInputOutputHistoryDto.setDatetime(isNull(datetime) ? null : datetime.toLocalDateTime());
            myInputOutputHistoryDto.setCurrencyName(rs.getString("currency"));
            myInputOutputHistoryDto.setAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount"), locale, 2));
            myInputOutputHistoryDto.setCommissionAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_amount"), locale, 2));
            myInputOutputHistoryDto.setMerchantName(rs.getString("merchant"));
            myInputOutputHistoryDto.setOperationType(rs.getString("operation_type"));
            myInputOutputHistoryDto.setConfirmation(rs.getInt("confirmation"));
            myInputOutputHistoryDto.setTransactionId(rs.getInt("transaction_id"));
            myInputOutputHistoryDto.setProvided(isNull(myInputOutputHistoryDto.getTransactionId()) ? 0 : 1);
            myInputOutputHistoryDto.setTransactionProvided(myInputOutputHistoryDto.getProvided() == 0 ?
                    messageSource.getMessage("inputoutput.statusFalse", null, locale) :
                    messageSource.getMessage("inputoutput.statusTrue", null, locale));
            myInputOutputHistoryDto.setId(rs.getInt("operation_id"));
            myInputOutputHistoryDto.setUserId(rs.getInt("user_id"));
            myInputOutputHistoryDto.setBankAccount(rs.getString("destination"));
            TransactionSourceType sourceType = TransactionSourceType.convert(rs.getString("source_type"));
            myInputOutputHistoryDto.setSourceType(sourceType);
            myInputOutputHistoryDto.setStatus(rs.getInt("status_id"));
            Timestamp dateModification = rs.getTimestamp("status_modification_date");
            myInputOutputHistoryDto.setStatusUpdateDate(isNull(dateModification) ? null : dateModification.toLocalDateTime());
            myInputOutputHistoryDto.setUserFullName(rs.getString("user_full_name"));
            myInputOutputHistoryDto.setRemark(rs.getString("remark"));
            myInputOutputHistoryDto.setAdminHolderId(rs.getInt("admin_holder_id"));
            myInputOutputHistoryDto.setTransactionHash(rs.getString("transaction_hash"));
            return myInputOutputHistoryDto;
        };
    }
}
