package me.exrates.dao.impl;

import java.util.*;

import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.PendingPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class PendingPaymentDaoImpl implements PendingPaymentDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void create(final PendingPayment pendingPayment) {
        final String sql = "INSERT INTO PENDING_PAYMENT " +
            "(invoice_id, transaction_hash, address) VALUES (:invoiceId, :transactionHash,:address)";
        final Map<String, Object> params = new HashMap<String,Object>() {
            {
                put("invoiceId", pendingPayment.getInvoiceId());
                put("transactionHash", pendingPayment.getTransactionHash());
                put("address", pendingPayment.getAddress()
                    .orElse(null));
            }
        };
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<PendingPayment> findAllByHash(String hash) {
        final String sql = "SELECT * FROM PENDING_PAYMENT WHERE transaction_hash = :hash";
        final Map<String, String> params = Collections.singletonMap("hash", hash);
        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(PendingPayment.class));
    }

    @Override
    public Optional<PendingPayment> findByInvoiceId(final int invoiceId) {
        final String sql = "SELECT * FROM PENDING_PAYMENT WHERE invoice_id = :invoiceId";
        final Map<String, Integer> params = Collections.singletonMap("invoiceId", invoiceId);
        try {
            return Optional.of(
                jdbcTemplate.queryForObject(sql, params,
                new BeanPropertyRowMapper<>(PendingPayment.class))
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<PendingPayment> findByAddress(final String address) {
        final String sql = "SELECT * FROM PENDING_PAYMENT WHERE address= :address";
        final Map<String, String> params = Collections.singletonMap("address", address);
        try {
            return Optional.of(
                jdbcTemplate.queryForObject(sql, params,
                new BeanPropertyRowMapper<>(PendingPayment.class))
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(final int invoiceId) {
        final String sql = "DELETE FROM PENDING_PAYMENT WHERE invoice_id = :invoiceId";
        final Map<String, Integer> params = Collections.singletonMap("invoiceId", invoiceId);
        jdbcTemplate.update(sql,params);
    }
}