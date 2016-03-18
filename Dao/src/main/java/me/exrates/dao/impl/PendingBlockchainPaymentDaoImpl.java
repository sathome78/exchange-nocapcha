package me.exrates.dao.impl;

import me.exrates.dao.PendingBlockchainPaymentDao;
import me.exrates.model.BlockchainPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class PendingBlockchainPaymentDaoImpl implements PendingBlockchainPaymentDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public BlockchainPayment create(BlockchainPayment blockchainPayment) {
        final String sql = "INSERT INTO PENDING_BLOCKCHAIN_PAYMENT (amount, invoice_id, address) VALUES (:amount, :invoiceId, :address)";
        final Map<String, Object> params = new HashMap<String,Object>() {
            {
                put("amount", blockchainPayment.getAmount());
                put("invoiceId", blockchainPayment.getInvoiceId());
                put("address", blockchainPayment.getAddress());
            }
        };
        return jdbcTemplate.update(sql, params) > 0 ? blockchainPayment : null;
    }

    @Override
    public BlockchainPayment findByInvoiceId(int invoiceId) {
        final String sql = "SELECT * FROM PENDING_BLOCKCHAIN_PAYMENT WHERE invoice_id = :invoiceId";
        final Map<String, Integer> params = Collections.singletonMap("invoiceId", invoiceId);
        try {
            return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(BlockchainPayment.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean delete(int invoiceId) {
        final String sql = "DELETE FROM PENDING_BLOCKCHAIN_PAYMENT WHERE invoice_id = :invoiceId";
        final Map<String, Integer> params = Collections.singletonMap("invoiceId", invoiceId);
        return jdbcTemplate.update(sql,params) > 0;
    }
}