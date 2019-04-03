package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.IEOResultRepository;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEOResult;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Repository
@Log4j
public class IEOResultRepositoryImpl implements IEOResultRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public IEOResultRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public IEOResult create(IEOResult ieoResult, BigDecimal availableAmount) {

        final String sql = "INSERT INTO IEO_RESULT (claim_id, status, available_amount) " +
                "VALUES (:claim_id, :status, :availableAmount)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("claim_id", ieoResult.getClaimId())
                .addValue("status", ieoResult.getStatus().name())
                .addValue("available_amount", availableAmount);
        if (jdbcTemplate.update(sql, params) > 0) {
            return ieoResult;
        }
        return null;
    }

    @Override
    public BigDecimal getAvailableAmount(IEOClaim ieoClaim) {
        String sql = "SELECT MIN(available_amount) FROM IEO_RESULT WHERE "

        return null;
    }
}
