package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.*;
import me.exrates.model.dto.WithdrawRequestFlatForReportDto;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WithdrawalRequestStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.util.Collections.singletonMap;
import static java.util.Optional.*;


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
        request.setStatus(WithdrawalRequestStatus.convert(resultSet.getInt("status")));
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
            " SELECT WITHDRAW_REQUEST.acceptance, WITHDRAW_REQUEST.wallet, WITHDRAW_REQUEST.processed_by, " +
                    "WITHDRAW_REQUEST.merchant_image_id, WITHDRAW_REQUEST.status, WITHDRAW_REQUEST.recipient_bank_name, " +
                    "WITHDRAW_REQUEST.recipient_bank_code, WITHDRAW_REQUEST.user_full_name, WITHDRAW_REQUEST.remark, MERCHANT_IMAGE.image_name, " +
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
                withdrawRequestFlatForReportDto.setStatus(WithdrawalRequestStatus.convert(rs.getInt("status")));
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


}
