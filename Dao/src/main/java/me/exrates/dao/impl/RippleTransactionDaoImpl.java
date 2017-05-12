package me.exrates.dao.impl;


import lombok.extern.log4j.Log4j2;
import me.exrates.dao.RippleTransactionDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.NotificationOption;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.RippleTransaction;
import me.exrates.model.enums.*;
import me.exrates.model.util.BigDecimalProcessing;
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
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maks on 11.05.2017.
 */
@Log4j2
@Repository
public class RippleTransactionDaoImpl implements RippleTransactionDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<RippleTransaction> rippleTransactionRowMapper = (resultSet, row) -> {
        RippleTransaction rippleTransaction = new RippleTransaction();
        rippleTransaction.setId(resultSet.getInt("id"));
        rippleTransaction.setUserId(resultSet.getInt("user_id"));
        rippleTransaction.setBlop(resultSet.getString("blop"));
        rippleTransaction.setAmount(resultSet.getBigDecimal("amount"));
        rippleTransaction.setDestinationAddress(resultSet.getString("destination_address"));
        rippleTransaction.setIssuerAddress(resultSet.getString("issuer_address"));
        rippleTransaction.setIssuerSecret(resultSet.getString("issuer_secret"));
        rippleTransaction.setDateCreation(resultSet.getTimestamp("date_creation").toLocalDateTime());
        Timestamp lastModif = resultSet.getTimestamp("date_last_modification");
        rippleTransaction.setDateCreation(lastModif == null ? null : lastModif.toLocalDateTime());
        rippleTransaction.setTxHash(resultSet.getString("tx_hash"));
        rippleTransaction.setStatus(RippleTransactionStatus.valueOf(resultSet.getString("tx_status")));
        rippleTransaction.setType(RippleTransactionType.valueOf(resultSet.getString("tx_type")));
        rippleTransaction.setTransactionId(resultSet.getInt("transaction_id"));
        return rippleTransaction;
    };

    @Override
    public int createRippleTransaction(RippleTransaction rippleTransaction) {
        String sql = "INSERT INTO RIPPLE_TRANSACTION " +
                "  (user_id, blop, amount, destination_address, issuer_address, issuer_secret, tx_status, tx_type)" +
                "  VALUES " +
                "  (:user_id, :blop, :amount, :destination_address, :issuer_address, :issuer_secret, :tx_status, :tx_type)";
        log.debug(rippleTransaction);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", rippleTransaction.getUserId())
                .addValue("blop", rippleTransaction.getBlop())
                .addValue("amount", rippleTransaction.getAmount())
                .addValue("destination_address", rippleTransaction.getDestinationAddress())
                .addValue("issuer_address", rippleTransaction.getIssuerAddress())
                .addValue("issuer_secret", rippleTransaction.getIssuerSecret())
                .addValue("tx_status", rippleTransaction.getStatus().name())
                .addValue("tx_type", rippleTransaction.getType().name());
        int result = namedParameterJdbcTemplate.update(sql, parameters, keyHolder);
        int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    @Override
    public boolean updateRippleTransaction(RippleTransaction rippleTransaction) {
        String sql = "UPDATE RIPPLE_TRANSACTION RT SET " +
                "tx_hash = :tx_hash,  tx_status = :tx_status, transaction_id = :tx_id WHERE RT.id = :id ";
        log.debug(rippleTransaction);
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", rippleTransaction.getId())
                .addValue("tx_hash", rippleTransaction.getTxHash())
                .addValue("tx_status", rippleTransaction.getStatus())
                .addValue("transaction_id", rippleTransaction.getTransactionId());
        return namedParameterJdbcTemplate.update(sql, parameters) > 0;
    }

    @Override
    public RippleTransaction getTransactionByHash(String hash, boolean forUpdate) {
        String sql = "SELECT * FROM RIPPLE_TRANSACTION RT WHERE RT.tx_hash = :hash ";
        if (forUpdate) {
            sql = sql.concat(" FOR UPDATE ");
        }
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("hash", hash);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, rippleTransactionRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public RippleTransaction getTransactionById(int id) {
        String sql = "SELECT * FROM RIPPLE_TRANSACTION RT WHERE RT.id = :id";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("id", id);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, rippleTransactionRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<RippleTransaction> getTransactionsByStatus(RippleTransactionStatus status) {
        String sql = "SELECT * FROM RIPPLE_TRANSACTION RT WHERE RT.tx_status = :status";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("status", status.name());
        try {
            return namedParameterJdbcTemplate.query(sql, namedParameters, rippleTransactionRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return Collections.emptyList();
        }
    }
}
