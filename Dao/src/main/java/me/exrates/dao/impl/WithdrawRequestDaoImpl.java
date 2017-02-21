package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.*;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.enums.WithdrawalRequestStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
                    "FROM WITHDRAW_REQUEST " +
                    "INNER JOIN TRANSACTION ON TRANSACTION.id = WITHDRAW_REQUEST.transaction_id " +
                    "INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id " +

                    "INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id " +
                    "INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id " +
                    "INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id " +
                    "INNER JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id " +
                    "INNER JOIN USER ON WALLET.user_id = USER.id " +
                    "LEFT JOIN USER AS ADMIN ON ADMIN.id =  WITHDRAW_REQUEST.processed_by " +
                    "LEFT JOIN MERCHANT_IMAGE ON WITHDRAW_REQUEST.merchant_image_id = MERCHANT_IMAGE.id";

    private static final String SELECT_COUNT = "SELECT COUNT (*) " +
            "FROM WITHDRAW_REQUEST " +
            "INNER JOIN TRANSACTION ON TRANSACTION.id = WITHDRAW_REQUEST.transaction_id " +
            "INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id " +

            "INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id " +
            "INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id " +
            "INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id " +
            "INNER JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id " +
            "INNER JOIN USER ON WALLET.user_id = USER.id " +
            "LEFT JOIN MERCHANT_IMAGE ON WITHDRAW_REQUEST.merchant_image_id = MERCHANT_IMAGE.id";

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
    public PagingData<List<WithdrawRequest>> findByStatus(Integer requestStatus, DataTableParams dataTableParams) {
        String whereClauseBasic = " WHERE WITHDRAW_REQUEST.status = :status ";
        String searchClause;
        if (StringUtils.isEmpty(dataTableParams.getSearchValue())) {
            searchClause = "";
        } else {
            searchClause = " AND (CONVERT(USER.email USING utf8) LIKE :searchValue OR CONVERT(ADMIN.email USING utf8) LIKE :searchValue " +
                    "OR CONVERT(MERCHANT.name USING utf8) LIKE :searchValue OR CONVERT(MERCHANT_IMAGE.image_name USING utf8) LIKE :searchValue) ";
        }
        String orderClause = " ORDER BY :orderColumn";
        String offsetAndLimit = " OFFSET :offset LIMIT :limit";
        String sqlTotal = new StringJoiner(" ").add(SELECT_ALL_REQUESTS).add(whereClauseBasic)
                .add(searchClause).add(orderClause).add(offsetAndLimit).toString();
        String sqlCount = new StringJoiner(" ").add(SELECT_COUNT).add(whereClauseBasic).add(searchClause).toString();
        log.debug(String.format("sql for total: %s", sqlTotal));
        log.debug(String.format("sql count: %s", sqlCount));
        Map<String, Object> params = new HashMap<>();
        params.put("status", requestStatus);
        params.put("searchValue", dataTableParams.getSearchValue());
        params.put("orderColumn", dataTableParams.getOrderColumnName());
        params.put("offset", dataTableParams.getStart());
        params.put("limit", dataTableParams.getLength());
        List<WithdrawRequest> requests = jdbcTemplate.query(sqlTotal, params, withdrawRequestRowMapper);
        Integer totalQuantity = jdbcTemplate.queryForObject(sqlCount, params, Integer.class);
        PagingData<List<WithdrawRequest>> result = new PagingData<>();
        result.setData(requests);
        result.setFiltered(totalQuantity);
        result.setTotal(totalQuantity);
        return result;
    }



}
