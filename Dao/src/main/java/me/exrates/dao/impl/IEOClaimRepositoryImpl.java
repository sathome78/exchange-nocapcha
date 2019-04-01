package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.IEOClaimRepository;
import me.exrates.model.IEOClaim;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Log4j
public class IEOClaimRepositoryImpl implements IEOClaimRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public IEOClaimRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public IEOClaim create(IEOClaim ieoClaim) {

        final String sql = "INSERT INTO IEO_CLAIM (currency_name, maker_id, user_id, amount) " +
                "VALUES (:currency_name, :maker_id, :user_id, :amount)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("currency_name", ieoClaim.getCurrencyName())
                .addValue("maker_id", ieoClaim.getMakerId())
                .addValue("user_id", ieoClaim.getUserId())
                .addValue("amount", ieoClaim.getAmount());
        if (jdbcTemplate.update(sql, params, keyHolder) > 0) {
            ieoClaim.setId(keyHolder.getKey().intValue());
            return ieoClaim;
        }
        return null;
    }

    @Override
    public boolean updateStateIEOClaim(int id, IEOClaim.IEOClaimStateEnum state) {
        String sql = "UPDATE IEO_CLAIM SET state = :state WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("state", state.name());
        params.put("id", id);
        return jdbcTemplate.update(sql, params) > 0;
    }

}
