package me.exrates.dao.impl;

import me.exrates.dao.BTCTransactionDao;
import me.exrates.model.BTCTransaction;
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
public class BTCTransactionDaoImpl implements BTCTransactionDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public BTCTransaction create(BTCTransaction btcTransaction) {
        final String sql = "INSERT INTO BTC_TRANSACTION (hash, amount, transaction_id) VALUES (:hash, :amount, :transactionId)";
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("hash", btcTransaction.getHash());
                put("amount", btcTransaction.getAmount());
                put("transactionId", btcTransaction.getTransactionId());
            }
        };
        return jdbcTemplate.update(sql, params) > 0 ? btcTransaction : null;

    }

    @Override
    public BTCTransaction findByTransactionId(int transactionId) {
        final String sql = "SELECT * FROM BTC_TRANSACTION WHERE transaction_id = :transactionId";
        final Map<String, Integer> params = Collections.singletonMap("transactionId", transactionId);
        try {
            return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(BTCTransaction.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean delete(int transactionId) {
        final String sql = "DELETE FROM BTC_TRANSACTION WHERE transaction_id = :transactionId";
        final Map<String, Integer> params = Collections.singletonMap("transactionId", transactionId);
        return jdbcTemplate.update(sql,params) > 0;
    }
}