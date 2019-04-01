package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.IEOResultRepository;
import me.exrates.model.IEOResult;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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
    public IEOResult create(IEOResult ieoResult) {

        final String sql = "INSERT INTO IEO_RESULT (claim_id, status) " +
                "VALUES (:claim_id, :status)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("claim_id", ieoResult.getClaimId())
                .addValue("status", ieoResult.getStatus().name());
        if (jdbcTemplate.update(sql, params, keyHolder) > 0) {
            ieoResult.setId(keyHolder.getKey().intValue());
            return ieoResult;
        }
        return null;
    }

    @Override
    public boolean updateStatus(int id, IEOResult.IEOResultStatus status) {
        String sql = "UPDATE IEO_RESULT SET status = :state WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("state", status.name());
        params.put("id", id);
        return jdbcTemplate.update(sql, params) > 0;
    }
}
