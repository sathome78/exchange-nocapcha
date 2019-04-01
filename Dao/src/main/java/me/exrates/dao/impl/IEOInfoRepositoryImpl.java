package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.IEOInfoRepository;
import me.exrates.model.Currency;
import me.exrates.model.IEOInfo;
import me.exrates.model.enums.IEOStatusEnum;
import me.exrates.model.ngExceptions.NgDashboardException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Log4j
public class IEOInfoRepositoryImpl implements IEOInfoRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public IEOInfoRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public IEOInfo createInfo(IEOInfo ieoInfo) {

        String sql = "INSERT INTO IEO_INFO"
                + " (currency_id, user_id, rate, amount, contributors, started, status, total_limit, buy_limit, version)"
                + " VALUES(:currency_id, :user_id, :rate, :amount, :contributors, :started, :status, :total_limit, :buy_limit, :version)";

        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("user_id", ieoInfo.getUserId());
        params.addValue("currency_id", ieoInfo.getCurrencyId());
        params.addValue("rate", ieoInfo.getRate());
        params.addValue("amount", ieoInfo.getAmount());
        params.addValue("contributors", ieoInfo.getContibutors());
        params.addValue("started", ieoInfo.getStarted());
        params.addValue("status", ieoInfo.getStatus());
        params.addValue("total_limit", ieoInfo.getTotalLimit());
        params.addValue("total_limit", ieoInfo.getTotalLimit());
        params.addValue("buy_limit", ieoInfo.getBuyLimit());
        params.addValue("version", ieoInfo.getVersion());
        try {
            jdbcTemplate.update(sql, params);
        } catch (DataAccessException e) {
            log.error("Failed to insert IEO INFO ", e);
            throw new NgDashboardException("Error insert ieoinfo to DB");
        }
        return ieoInfo;
    }

    @Override
    public IEOInfo findByCurrencyName(String currencyName) {

        String sql = "SELECT ii.currency_id, ii.user_id, ii.rate, ii.amount, ii.contributors, ii.started, ii.status," +
                " ii.total_limit, ii.buy_limit, ii.version" +
                " FROM IEO_INFO ii INNER JOIN CURRENCY c ON c.id = ii.currency_id " +
                " WHERE c.name = :currencyName";

        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("currencyName", currencyName);
            }
        };
        try {
            return jdbcTemplate.queryForObject(sql, params, (rs, row) -> {
                IEOInfo ieoInfo = new IEOInfo();
                ieoInfo.setCurrencyId(rs.getInt("currency_id"));
                ieoInfo.setUserId(rs.getInt("user_id"));
                ieoInfo.setRate(rs.getBigDecimal("rate"));
                ieoInfo.setAmount(rs.getBigDecimal("amount"));
                ieoInfo.setAmount(rs.getBigDecimal("amount"));
                ieoInfo.setContibutors(rs.getString("contributors"));
                ieoInfo.setStarted(rs.getDate("started"));
                ieoInfo.setStatus(IEOStatusEnum.valueOf(rs.getString("status")));

                return  ieoInfo;
            });
        } catch (Exception e) {
            log.warn("Failed to find ieoInfo for currency name " + currencyName, e);
            throw e;
        }
    }
}
