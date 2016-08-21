package me.exrates.dao.impl;

import me.exrates.dao.InvoiceRequestDao;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.Transaction;
import me.exrates.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Collections.singletonMap;
import static java.util.Optional.empty;
import static java.util.Optional.of;


/**
 * Created by ogolv on 26.07.2016.
 */
@Repository
public class InvoiceRequestDaoImpl implements InvoiceRequestDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final static RowMapper<InvoiceRequest> invoiceRequestRowMapper = (resultSet, i) -> {
        InvoiceRequest invoiceRequest = new InvoiceRequest();
        Transaction transaction = TransactionDaoImpl.transactionRowMapper.mapRow(resultSet, i);
        invoiceRequest.setTransaction(transaction);
        invoiceRequest.setUserEmail(resultSet.getString("user_email"));
        invoiceRequest.setUserId(resultSet.getInt("user_id"));
        invoiceRequest.setAcceptanceUserEmail(resultSet.getString("acceptance_user_email"));
        invoiceRequest.setAcceptanceUserId(resultSet.getInt("acceptance_id"));
        Timestamp acceptanceTimeResult = resultSet.getTimestamp("acceptance_time");
        LocalDateTime acceptanceTime = acceptanceTimeResult == null ? null : acceptanceTimeResult.toLocalDateTime();
        invoiceRequest.setAcceptanceTime(acceptanceTime);
        return invoiceRequest;
    };

    private static final String SELECT_ALL = "SELECT inv.acceptance_time, user.id AS user_id, user.email AS user_email, " +
            "adm.id AS acceptance_id, adm.email AS acceptance_user_email, " +
            "TRANSACTION.id, TRANSACTION.amount, TRANSACTION.commission_amount, TRANSACTION.datetime, " +
            "                    TRANSACTION.operation_type_id,TRANSACTION.provided,TRANSACTION.confirmation, WALLET.id, WALLET.active_balance, " +
            "                    WALLET.reserved_balance, WALLET.currency_id, COMPANY_WALLET.id, COMPANY_WALLET.balance, " +
            "                    COMPANY_WALLET.commission_balance, COMMISSION.id, COMMISSION.date, COMMISSION.value, " +
            "                    CURRENCY.id, CURRENCY.description, CURRENCY.name, MERCHANT.id,MERCHANT.name,MERCHANT.description " +
            "                    FROM INVOICE_REQUEST AS inv " +
            "    INNER JOIN TRANSACTION ON inv.transaction_id = TRANSACTION.id " +
            "    INNER JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id " +
            "    INNER JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id " +
            "    INNER JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id " +
            "    INNER JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id " +
            "    INNER JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id " +
            "    INNER JOIN USER AS user ON inv.user_id = user.id " +
            "    LEFT JOIN USER AS adm ON inv.acceptance_user_id = adm.id";


    @Override
    public void create(InvoiceRequest invoiceRequest, User user) {
        final String sql = "INSERT into INVOICE_REQUEST (transaction_id, user_id) values (:transaction_id, :user_id)";
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("transaction_id", invoiceRequest.getTransaction().getId());
                put("user_id", user.getId());
            }
        };
        jdbcTemplate.update(sql, params);


    }

    @Override
    public void delete(InvoiceRequest invoiceRequest) {
        final String sql = "DELETE FROM INVOICE_REQUEST WHERE transaction_id = :id";
        final Map<String, Integer> params = singletonMap("id", invoiceRequest
                .getTransaction()
                .getId());
        jdbcTemplate.update(sql, params);

    }

    @Override
    public void setAcceptance(InvoiceRequest invoiceRequest) {
        final String sql = "UPDATE INVOICE_REQUEST SET acceptance_user_id = (SELECT id FROM USER WHERE email=:email), acceptance_time = NOW() " +
                "WHERE transaction_id = :transaction_id";
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("transaction_id", invoiceRequest.getTransaction().getId());
                put("email", invoiceRequest.getAcceptanceUserEmail());
            }
        };
        jdbcTemplate.update(sql, params);

    }

    @Override
    public Optional<InvoiceRequest> findById(int id) {
        final String sql = SELECT_ALL + " WHERE inv.transaction_id = :id";
        try {
            return of(jdbcTemplate
                    .queryForObject(sql,
                            singletonMap("id", id),
                            invoiceRequestRowMapper)
            );
        } catch (EmptyResultDataAccessException e) {
            return empty();
        }
    }

    @Override
    public List<InvoiceRequest> findAll() {
        final String sql = SELECT_ALL + " ORDER BY acceptance_time IS NULL DESC, acceptance_time DESC";
        return jdbcTemplate.query(sql, invoiceRequestRowMapper);
    }
}
