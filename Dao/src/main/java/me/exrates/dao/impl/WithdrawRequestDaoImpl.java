package me.exrates.dao.impl;

import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.MerchantImage;
import me.exrates.model.Transaction;
import me.exrates.model.WithdrawRequest;
import me.exrates.model.enums.WithdrawalRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static java.util.Optional.*;


/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
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
        request.setStatus(WithdrawalRequestStatus.convert(resultSet.getInt("status")));
        return request;
    };

    private final static String SELECT_ALL_REQUESTS =
            " SELECT WITHDRAW_REQUEST.acceptance, WITHDRAW_REQUEST.wallet, WITHDRAW_REQUEST.processed_by, " +
                    "WITHDRAW_REQUEST.merchant_image_id, WITHDRAW_REQUEST.status, MERCHANT_IMAGE.image_name, " +
                    "USER.id, USER.email as user_email,(SELECT EMAIL from USER WHERE id = WITHDRAW_REQUEST.processed_by) as admin_email, " +
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
                    "LEFT JOIN MERCHANT_IMAGE ON WITHDRAW_REQUEST.merchant_image_id = MERCHANT_IMAGE.id";

    @Override
    public void create(WithdrawRequest withdrawRequest) {
        final String sql = "INSERT INTO WITHDRAW_REQUEST (transaction_id,wallet, merchant_image_id) VALUES (:id,:wallet, :merchant_image_id)";
        final Map<String, Object> params = new HashMap<String,Object>(){
            {
                put("id", withdrawRequest
                        .getTransaction()
                        .getId());
                put("wallet", withdrawRequest.getWallet());
                put("merchant_image_id", withdrawRequest.getMerchantImage().getId() == 0 ? null : withdrawRequest.getMerchantImage().getId());
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
}
