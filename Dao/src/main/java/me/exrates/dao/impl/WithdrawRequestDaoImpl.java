package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.MerchantImage;
import me.exrates.model.PagingData;
import me.exrates.model.Transaction;
import me.exrates.model.WithdrawRequest;
import me.exrates.model.dto.WithdrawRequestCreateDto;
import me.exrates.model.dto.WithdrawRequestFlatForReportDto;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WithdrawalRequestStatus;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static me.exrates.model.enums.TransactionSourceType.BTC_INVOICE;
import static me.exrates.model.enums.TransactionSourceType.INVOICE;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CONFIRM;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.REVOKE;


/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Log4j
@Repository
public class WithdrawRequestDaoImpl implements WithdrawRequestDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final static RowMapper<WithdrawRequest> withdrawRequestRowMapper = (resultSet, i) -> {
        final Transaction transaction = TransactionDaoImpl.transactionRowMapper.mapRow(resultSet, i);
        final WithdrawRequest request = new WithdrawRequest();
        request.setId(resultSet.getInt("id"));
        request.setUserEmail(resultSet.getString("user_email"));
        request.setUserId(resultSet.getInt("user_id"));
        request.setWallet(resultSet.getString("wallet"));
        MerchantImage merchantImage = new MerchantImage();
        merchantImage.setId(resultSet.getInt("merchant_image_id"));
        merchantImage.setImage_name(resultSet.getString("image_name"));
        request.setMerchantImage(merchantImage);
        request.setTransaction(transaction);
        request.setProcessedBy(resultSet.getString("admin_email"));
        request.setProcessedById(resultSet.getInt("processed_by"));
        request.setAcceptance(resultSet
                .getTimestamp("acceptance")
                .toLocalDateTime()
        );
        request.setRecipientBankName(resultSet.getString("recipient_bank_name"));
        request.setRecipientBankCode(resultSet.getString("recipient_bank_code"));
        request.setUserFullName(resultSet.getString("user_full_name"));
        request.setRemark(resultSet.getString("remark"));
        request.setStatus(resultSet.getObject("status") == null ? null : WithdrawalRequestStatus.convert(resultSet.getInt("status")));
        request.setWithdrawStatus(resultSet.getObject("status_id") == null ? null : WithdrawStatusEnum.convert(resultSet.getInt("status_id")));
        request.setAmount(resultSet.getBigDecimal("amount"));
        request.setCommission(resultSet.getBigDecimal("commission"));
        return request;
    };

    private final static String JOINS_FOR_ALL_REQUESTS = "INNER JOIN TRANSACTION ON TRANSACTION.id = WITHDRAW_REQUEST.transaction_id " +
            "INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id " +
            "INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id " +
            "INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id " +
            "INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id " +
            "INNER JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id " +
            "INNER JOIN USER ON WALLET.user_id = USER.id " +
            "LEFT JOIN USER AS ADMIN ON ADMIN.id =  WITHDRAW_REQUEST.processed_by " +
            "LEFT JOIN MERCHANT_IMAGE ON WITHDRAW_REQUEST.merchant_image_id = MERCHANT_IMAGE.id";
    private final static String JOIN_FOR_PERMITTED = "INNER JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION AS permission ON permission.user_id = :current_user_id " +
            "AND permission.currency_id = TRANSACTION.currency_id AND permission.operation_direction = 'WITHDRAW'";

    private final static String SELECT_ALL_REQUESTS =
            " SELECT WITHDRAW_REQUEST.*, " +
                    "MERCHANT_IMAGE.image_name, " +
                    "USER.id, USER.email as user_email, ADMIN.email as admin_email, " +
                    "TRANSACTION.id,TRANSACTION.amount,TRANSACTION.commission_amount,TRANSACTION.datetime, " +
                    "TRANSACTION.operation_type_id,TRANSACTION.provided,TRANSACTION.confirmation, " +
                    "TRANSACTION.source_id, TRANSACTION.source_type, WALLET.id,WALLET.active_balance, " +
                    "WALLET.user_id, WALLET.reserved_balance,WALLET.currency_id,COMPANY_WALLET.id,COMPANY_WALLET.balance, " +
                    "COMPANY_WALLET.commission_balance,COMMISSION.id,COMMISSION.date,COMMISSION.value," +
                    "CURRENCY.id,CURRENCY.description,CURRENCY.name,MERCHANT.id,MERCHANT.name,MERCHANT.description " +
                    "FROM WITHDRAW_REQUEST " + JOINS_FOR_ALL_REQUESTS;


    private static final String SELECT_COUNT = "SELECT COUNT(*) FROM WITHDRAW_REQUEST " + JOINS_FOR_ALL_REQUESTS;

    @Override
    public void create(WithdrawRequest withdrawRequest) {
        final String sql = "INSERT INTO WITHDRAW_REQUEST (transaction_id,wallet, merchant_image_id, recipient_bank_name, recipient_bank_code, user_full_name, remark) " +
                "VALUES (:id, :wallet, :merchant_image_id, :payer_bank_name, :payer_bank_code, :user_full_name, :remark)";
        final Map<String, Object> params = new HashMap<String,Object>(){
            {
                put("id", withdrawRequest
                        .getTransaction()
                        .getId());
                put("wallet", withdrawRequest.getWallet());
                put("merchant_image_id", withdrawRequest.getMerchantImage().getId() == 0 ? null : withdrawRequest.getMerchantImage().getId());
                put("payer_bank_name", withdrawRequest.getRecipientBankName());
                put("payer_bank_code", withdrawRequest.getRecipientBankCode());
                put("user_full_name", withdrawRequest.getUserFullName());
                put("remark", withdrawRequest.getRemark());
            }
        };
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void delete(WithdrawRequest withdrawRequest) {
        final String sql = "DELETE FROM WITHDRAW_REQUEST WHERE transaction_id = :id";
        final Map<String, Integer> params = singletonMap("id", withdrawRequest
                .getTransaction()
                .getId());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void update(WithdrawRequest withdrawRequest) {
        final String sql = "UPDATE WITHDRAW_REQUEST SET " +
                "acceptance = CURRENT_TIMESTAMP(), processed_by = (SELECT id FROM USER WHERE email=:email), status = :status WHERE transaction_id = :id";
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("email", withdrawRequest.getProcessedBy());
                put("id", withdrawRequest
                        .getTransaction()
                        .getId()
                );
                put("status", withdrawRequest.getStatus().getType());
            }
        };
        jdbcTemplate.update(sql, params);
    }

    @Override
    public Optional<WithdrawRequest> findByIdAndBlock(int id) {
        String sql = "SELECT COUNT(*) " +
                "FROM WITHDRAW_REQUEST " +
                "JOIN TRANSACTION ON WITHDRAW_REQUEST.transaction_id = TRANSACTION.id " +
                "WHERE WITHDRAW_REQUEST.transaction_id = :id " +
                "FOR UPDATE ";
        jdbcTemplate.queryForObject(sql, singletonMap("id", id), Integer.class);
        return findById(id);
    }

    @Override
    public Optional<WithdrawRequest> findById(int id) {
        final String sql = SELECT_ALL_REQUESTS + " WHERE WITHDRAW_REQUEST.transaction_id = :id";
        try {
            return of(jdbcTemplate
                    .queryForObject(sql,
                            singletonMap("id", id),
                            withdrawRequestRowMapper)
            );
        } catch (EmptyResultDataAccessException e) {
            return empty();
        }
    }

    @Override
    public List<WithdrawRequest> findAll() {
        final String sql = SELECT_ALL_REQUESTS + " ORDER BY status ASC, datetime DESC";
        return jdbcTemplate.query(sql, withdrawRequestRowMapper);
    }

    @Override
    public PagingData<List<WithdrawRequest>> findByStatus(Integer requestStatus, Integer currentUserId, DataTableParams dataTableParams, WithdrawFilterData withdrawFilterData) {
        String whereClauseBasic = " WHERE WITHDRAW_REQUEST.status = :status ";
        String filter = withdrawFilterData.getSQLFilterClause();
        String whereClauseFilter = StringUtils.isEmpty(filter) ? "" :  " AND ".concat(filter);
        String orderClause = dataTableParams.getOrderByClause();
        String offsetAndLimit = " LIMIT :limit OFFSET :offset ";
        String sqlTotal = new StringJoiner(" ").add(SELECT_ALL_REQUESTS).add(JOIN_FOR_PERMITTED).add(whereClauseBasic)
                .add(whereClauseFilter).add(orderClause).add(offsetAndLimit).toString();
        String sqlCount = new StringJoiner(" ").add(SELECT_COUNT).add(JOIN_FOR_PERMITTED).add(whereClauseBasic).add(whereClauseFilter).toString();
        log.debug(String.format("sql for total: %s", sqlTotal));
        log.debug(String.format("sql count: %s", sqlCount));
        Map<String, Object> params = new HashMap<>();
        params.put("status", requestStatus);
        params.put("offset", dataTableParams.getStart());
        params.put("limit", dataTableParams.getLength());
        params.put("current_user_id", currentUserId);
        params.putAll(withdrawFilterData.getNamedParams());
        List<WithdrawRequest> requests = jdbcTemplate.query(sqlTotal, params, withdrawRequestRowMapper);
        Integer totalQuantity = jdbcTemplate.queryForObject(sqlCount, params, Integer.class);
        PagingData<List<WithdrawRequest>> result = new PagingData<>();
        result.setData(requests);
        result.setFiltered(totalQuantity);
        result.setTotal(totalQuantity);
        return result;
    }

    @Override
    public List<WithdrawRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(
        String startDate,
        String endDate,
        List<Integer> roleIdList,
        List<Integer> currencyList) {
        String sql = "SELECT  WR.*, " +
            "         USER.email AS user_email, " +
            "         ADM.email AS acceptance_user_email, " +
            "         TX.id, TX.amount, TX.commission_amount, TX.datetime, " +
            "         TX.operation_type_id,TX.provided,TX.confirmation, " +
            "         TX.source_type, " +
            "         MERCHANT.name AS merchant_name, " +
            "         CURRENCY.name AS currency_name" +
            " FROM WITHDRAW_REQUEST WR " +
            " JOIN TRANSACTION TX ON (TX.id = WR.transaction_id) AND (TX.currency_id IN (:currency_list)) " +
            " JOIN CURRENCY ON CURRENCY.id = TX.currency_id " +
            " JOIN WALLET ON WALLET.id = TX.user_wallet_id " +
            " JOIN MERCHANT ON MERCHANT.id = TX.merchant_id " +
            " JOIN USER AS USER ON USER.id = WALLET.user_id " +
            " LEFT JOIN USER AS ADM ON ADM.id = WR.processed_by " +
            " WHERE " +
            "    TX.datetime BETWEEN STR_TO_DATE(:start_date, '%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(:end_date, '%Y-%m-%d %H:%i:%s') " +
            (roleIdList.isEmpty() ? "" :
                " AND USER.roleid IN (:role_id_list)");
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("start_date", startDate);
            put("end_date", endDate);
            if (!roleIdList.isEmpty()) {
                put("role_id_list", roleIdList);
            }
            put("currency_list", currencyList);
        }};
        return jdbcTemplate.query(sql, params, new RowMapper<WithdrawRequestFlatForReportDto>() {
            @Override
            public WithdrawRequestFlatForReportDto mapRow(ResultSet rs, int i) throws SQLException {
                WithdrawRequestFlatForReportDto withdrawRequestFlatForReportDto = new WithdrawRequestFlatForReportDto();
                withdrawRequestFlatForReportDto.setInvoiceId(rs.getInt("transaction_id"));
                withdrawRequestFlatForReportDto.setWallet(rs.getString("wallet"));
                withdrawRequestFlatForReportDto.setRecipientBank(rs.getString("recipient_bank_name"));
                withdrawRequestFlatForReportDto.setAcceptanceUserEmail(rs.getString("acceptance_user_email"));
                withdrawRequestFlatForReportDto.setAcceptanceTime(rs.getTimestamp("acceptance") == null ? null : rs.getTimestamp("acceptance").toLocalDateTime());
                withdrawRequestFlatForReportDto.setStatus(rs.getObject("status") == null ? null : WithdrawalRequestStatus.convert(rs.getInt("status")));
                withdrawRequestFlatForReportDto.setWithdrawStatus(rs.getObject("status_id") == null ? null : WithdrawStatusEnum.convert(rs.getInt("status_id")));
                withdrawRequestFlatForReportDto.setUserFullName(rs.getString("user_full_name"));
                withdrawRequestFlatForReportDto.setUserEmail(rs.getString("user_email"));
                withdrawRequestFlatForReportDto.setAmount(rs.getBigDecimal("amount"));
                withdrawRequestFlatForReportDto.setCommissionAmount(rs.getBigDecimal("commission_amount"));
                withdrawRequestFlatForReportDto.setDatetime(rs.getTimestamp("datetime") == null ? null : rs.getTimestamp("datetime").toLocalDateTime());
                withdrawRequestFlatForReportDto.setCurrency(rs.getString("currency_name"));
                withdrawRequestFlatForReportDto.setSourceType(TransactionSourceType.valueOf(rs.getString("source_type")));
                withdrawRequestFlatForReportDto.setMerchant(rs.getString("merchant_name"));
                return withdrawRequestFlatForReportDto;
            }
        });
    }
    
    @Override
    public Integer findStatusIdByRequestId(Integer withdrawRequestId) {
        String sql = "SELECT status FROM WITHDRAW_REQUEST WHERE transaction_id = :request_id";
        return jdbcTemplate.queryForObject(sql, Collections.singletonMap("request_id", withdrawRequestId), Integer.class);
    }

    @Override
    public int create(WithdrawRequestCreateDto withdrawRequest) {
        final String sql = "INSERT INTO WITHDRAW_REQUEST " +
            "(wallet, merchant_image_id, recipient_bank_name, recipient_bank_code, user_full_name, remark, amount, commission, status_id," +
            " date_creation, status_modification_date, currency_id, merchant_id, user_id) " +
            "VALUES (:wallet, :merchant_image_id, :payer_bank_name, :payer_bank_code, :user_full_name, :remark, :amount, :commission, :status_id," +
            " NOW(), NOW(), :currency_id, :merchant_id, :user_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("wallet", withdrawRequest.getDestinationWallet())
                .addValue("merchant_image_id", withdrawRequest.getMerchantImage().getId() == 0 ? null : withdrawRequest.getMerchantImage().getId())
                .addValue("payer_bank_name", withdrawRequest.getRecipientBankName())
                .addValue("payer_bank_code", withdrawRequest.getRecipientBankCode())
                .addValue("user_full_name", withdrawRequest.getUserFullName())
                .addValue("remark", withdrawRequest.getRemark())
                .addValue("amount", withdrawRequest.getAmount())
                .addValue("commission", withdrawRequest.getCommission())
                .addValue("status_id", withdrawRequest.getStatusId())
                .addValue("currency_id", withdrawRequest.getCurrencyId())
                .addValue("merchant_id", withdrawRequest.getMerchantId())
                .addValue("user_id", withdrawRequest.getUserId());
        jdbcTemplate.update(sql, params, keyHolder);
        return (int) keyHolder.getKey().longValue();
    }

    /*@Override
    public List<MyInputOutputHistoryDto> findMyInputOutputHistoryByOperationType(
        String email,
        Integer offset,
        Integer limit,
        List<Integer> operationTypeIdList,
        Locale locale) {
        String sql = " SELECT " +
            "    TRANSACTION.datetime, CURRENCY.name as currency, TRANSACTION.amount, TRANSACTION.commission_amount, " +
            "    TRANSACTION.source_type, TRANSACTION.confirmation, " +
            "    case when OPERATION_TYPE.name = 'input' or WITHDRAW_REQUEST.merchant_image_id is null " +
            "      then MERCHANT.name " +
            "      else MERCHANT_IMAGE.image_name end as merchant,\n" +
            "    OPERATION_TYPE.name as operation_type, TRANSACTION.id, TRANSACTION.provided, " +
            "    INVOICE_BANK.account_number AS bank_account, " +
            "    USER.id AS user_id," +
            "    INVOICE_REQUEST.invoice_request_status_id, " +
            "    INVOICE_REQUEST.status_update_date AS invoice_request_status_update_date," +
            "    INVOICE_REQUEST.user_full_name, INVOICE_REQUEST.remark, " +
            "    PENDING_PAYMENT.pending_payment_status_id, " +
            "    PENDING_PAYMENT.status_update_date AS pending_payment_status_update_date," +
            "    WITHDRAW_REQUEST.status AS withdraw_request_status_id " +
            "  from TRANSACTION \n" +
            "    left join CURRENCY on CURRENCY.id = TRANSACTION.currency_id" +
//            "    left join WITHDRAW_REQUEST on TRANSACTION.id=WITHDRAW_REQUEST.transaction_id\n" +
            "    left join INVOICE_REQUEST on TRANSACTION.id=INVOICE_REQUEST.transaction_id\n" +
            "    left join PENDING_PAYMENT on TRANSACTION.id=PENDING_PAYMENT.invoice_id\n" +
            "    left join INVOICE_BANK on INVOICE_REQUEST.bank_id = INVOICE_BANK.id " +
//            "    left join MERCHANT_IMAGE on WITHDRAW_REQUEST.merchant_image_id=MERCHANT_IMAGE.id\n" +
            "    left join MERCHANT on TRANSACTION.merchant_id = MERCHANT.id \n" +
            "    left join OPERATION_TYPE on TRANSACTION.operation_type_id=OPERATION_TYPE.id\n" +
            "    left join WALLET on TRANSACTION.user_wallet_id=WALLET.id\n" +
            "    left join USER on WALLET.user_id=USER.id\n" +
            "  where TRANSACTION.operation_type_id IN (:operation_type_id_list) and USER.email=:email order by datetime DESC" +
            (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
        final Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("operation_type_id_list", operationTypeIdList);
        return jdbcTemplate.query(sql, params, (rs, i) -> {
            MyInputOutputHistoryDto myInputOutputHistoryDto = new MyInputOutputHistoryDto();
            myInputOutputHistoryDto.setDatetime(rs.getTimestamp("datetime").toLocalDateTime());
            myInputOutputHistoryDto.setCurrencyName(rs.getString("currency"));
            myInputOutputHistoryDto.setAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount"), locale, 2));
            myInputOutputHistoryDto.setCommissionAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_amount"), locale, 2));
            myInputOutputHistoryDto.setMerchantName(rs.getString("merchant"));
            myInputOutputHistoryDto.setOperationType(rs.getString("operation_type"));
            myInputOutputHistoryDto.setTransactionId(rs.getInt("id"));
            myInputOutputHistoryDto.setTransactionProvided(rs.getInt("provided") == 0 ?
                messageSource.getMessage("inputoutput.statusFalse", null, locale) :
                messageSource.getMessage("inputoutput.statusTrue", null, locale));
            myInputOutputHistoryDto.setUserId(rs.getInt("user_id"));
            myInputOutputHistoryDto.setBankAccount(rs.getString("bank_account"));
            Stream.of(rs.getObject("invoice_request_status_id"),
                rs.getObject("pending_payment_status_id"),
                rs.getObject("withdraw_request_status_id")).filter(Objects::nonNull).peek(log::debug).findFirst()
                .ifPresent(obj -> myInputOutputHistoryDto.setInvoiceRequestStatusId((Integer) obj));

            TransactionSourceType transactionSourceType = TransactionSourceType.convert(rs.getString("source_type"));
            myInputOutputHistoryDto.setSourceType(transactionSourceType.name());
      *//**//*
            Boolean confirmationRequired = false;
            if (myInputOutputHistoryDto.getInvoiceRequestStatusId() != null) {
                if (transactionSourceType == INVOICE) {
                    confirmationRequired = InvoiceRequestStatusEnum.convert(myInputOutputHistoryDto.getInvoiceRequestStatusId()).availableForAction(CONFIRM);
                } else if (transactionSourceType == BTC_INVOICE) {
                    confirmationRequired = PendingPaymentStatusEnum.convert(myInputOutputHistoryDto.getInvoiceRequestStatusId()).availableForAction(CONFIRM);
                }
            }
            myInputOutputHistoryDto.setConfirmationRequired(confirmationRequired);
      *//**//*
            Boolean mayBeRevoked = false;
            if (myInputOutputHistoryDto.getInvoiceRequestStatusId() != null) {
                if (transactionSourceType == INVOICE) {
                    mayBeRevoked = InvoiceRequestStatusEnum.convert(myInputOutputHistoryDto.getInvoiceRequestStatusId()).availableForAction(REVOKE);
                } else if (transactionSourceType == BTC_INVOICE) {
                    mayBeRevoked = PendingPaymentStatusEnum.convert(myInputOutputHistoryDto.getInvoiceRequestStatusId()).availableForAction(REVOKE);
                }
            }
            myInputOutputHistoryDto.setMayBeRevoked(mayBeRevoked);
      *//**//*
            LocalDateTime statusUpdateDate;
            if (rs.getTimestamp("invoice_request_status_update_date") == null) {
                statusUpdateDate = rs.getTimestamp("pending_payment_status_update_date") == null ? null :
                    rs.getTimestamp("pending_payment_status_update_date").toLocalDateTime();
            } else {
                statusUpdateDate = rs.getTimestamp("invoice_request_status_update_date").toLocalDateTime();
            }
            myInputOutputHistoryDto.setStatusUpdateDate(statusUpdateDate);
            myInputOutputHistoryDto.setUserFullName(rs.getString("user_full_name"));
            myInputOutputHistoryDto.setRemark(rs.getString("remark"));
            myInputOutputHistoryDto.setConfirmation((Integer) rs.getObject("confirmation"));
            log.debug(String.format("id: %s, status: %s, source: %s, optype: %s", myInputOutputHistoryDto.getTransactionId(), myInputOutputHistoryDto.getInvoiceRequestStatusId(),
                myInputOutputHistoryDto.getSourceType(), myInputOutputHistoryDto.getOperationType()));
            return myInputOutputHistoryDto;
        });
    }*/

}

