package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.IEOClaimRepository;
import me.exrates.model.IEOClaim;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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

        final String sql = "INSERT INTO IEO_CLAIM (currency_name, maker_id, user_id, amount, rate, price_in_btc) " +
                "VALUES (:currency_name, :maker_id, :user_id, :amount, :rate, :price_in_btc)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("currency_name", ieoClaim.getCurrencyName())
                .addValue("maker_id", ieoClaim.getMakerId())
                .addValue("user_id", ieoClaim.getUserId())
                .addValue("rate", ieoClaim.getRate())
                .addValue("price_in_btc", ieoClaim.getPriceInBtc())
                .addValue("amount", ieoClaim.getAmount());
        if (jdbcTemplate.update(sql, params, keyHolder) > 0) {
            ieoClaim.setId(keyHolder.getKey().intValue());
            return ieoClaim;
        }
        return null;
    }

    @Override
    public Collection<IEOClaim> findUnprocessedIeoClaims() {

        final String sql = "SElECT * FROM IEO_CLAIM WHERE state = :state";
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("state", IEOClaim.IEOClaimStateEnum.created.name());
            }
        };
        return jdbcTemplate.query(sql, params, getAllFieldsRowMapper());
    }

    @Override
    public boolean updateStateIEOClaim(int id, IEOClaim.IEOClaimStateEnum state) {
        String sql = "UPDATE IEO_CLAIM SET state = :state WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("state", state.name());
        params.put("id", id);
        return jdbcTemplate.update(sql, params) > 0;
    }

    private RowMapper<IEOClaim> getAllFieldsRowMapper() {
        return (rs, row) -> {
            IEOClaim ieoClaim = new IEOClaim();
            ieoClaim.setId(rs.getInt("id"));
            ieoClaim.setCurrencyName(rs.getString("currencyName"));
            ieoClaim.setMakerId(rs.getInt("maker_id"));
            ieoClaim.setUserId(rs.getInt("user_id"));
            ieoClaim.setRate(rs.getBigDecimal("rate"));
            ieoClaim.setAmount(rs.getBigDecimal("amount"));
            ieoClaim.setPriceInBtc(rs.getBigDecimal("price_in_btc"));
            ieoClaim.setCreated(rs.getDate("created"));
            ieoClaim.setState(IEOClaim.IEOClaimStateEnum.valueOf(rs.getString("state")));
            return ieoClaim;
        };
    }
}
