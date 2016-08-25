package me.exrates.dao.impl;

import me.exrates.dao.EDCAccountDao;
import me.exrates.model.EDCAccount;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class EDCAccountDaoImpl implements EDCAccountDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public EDCAccountDaoImpl(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public EDCAccount findByTransactionId(int id) {
        final String sql = "SELECT * FROM EDC_TEMP_ACCOUNT WHERE transaction_id = :transactionId";
        final Map<String, Integer> params = singletonMap("transactionId", id);
        return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(EDCAccount.class));
    }

    @Override
    public void deleteByTransactionId(int id) {
        final String sql = "DELETE FROM EDC_TEMP_ACCOUNT WHERE transaction_id = :transactionId";
        final Map<String, Integer> params = singletonMap("transactionId", id);
        try {
            jdbcTemplate.update(sql, params);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void create(EDCAccount edcAccount) {
        final String sql = "INSERT INTO EDC_TEMP_ACCOUNT (transaction_id, wif_priv_key, pub_key, brain_priv_key) VALUES (:transactionId, :wifPrivKey, :pubKey, :brainPrivKey)";
        final Map<String, Object> params = new HashMap<>();
        params.put("transactionId", edcAccount.getTransactionId());
        params.put("wifPrivKey", edcAccount.getWifPrivKey());
        params.put("pubKey", edcAccount.getPubKey());
        params.put("brainPrivKey", edcAccount.getBrainPrivKey());
        jdbcTemplate.update(sql, params);
    }
}
