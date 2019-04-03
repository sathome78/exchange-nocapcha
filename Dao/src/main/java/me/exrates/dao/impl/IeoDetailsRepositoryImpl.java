package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.IeoDetailsRepository;
import me.exrates.model.IEODetails;
import me.exrates.model.User;
import me.exrates.model.enums.IEODetailsStatus;
import me.exrates.model.enums.UserRole;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;

@Repository
@Log4j
public class IeoDetailsRepositoryImpl implements IeoDetailsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public IeoDetailsRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public IEODetails save(IEODetails ieoDetails) {

        String sql = "INSERT INTO IEO_INFO"
                + " (currency_name, maker_id, rate, amount, available_amount, contributors, status, min_amount, max_amount_per_claim," +
                " max_amount_per_user, starts_at, terminates_at, created_by)"
                + " VALUES(:currency_name, :maker_id, :rate, :amount, :available_amount, :contributors, :status, :min_amount, :max_amount_per_claim,"
                + " :max_amount_per_user, :starts_at, :terminates_at, :created_by)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = getGeneralParams(ieoDetails);
        params.addValue("created_by", ieoDetails.getCreatedBy());
        try {
            jdbcTemplate.update(sql, params, keyHolder);
        } catch (DataAccessException e) {
            log.error("Failed to insert IEO Details ", e);
            throw new RuntimeException(String.format("Error insert ieo details for %s to DB", ieoDetails.getCurrencyName()));
        }
        ieoDetails.setId((Integer) keyHolder.getKey());
        return ieoDetails;
    }

    @Override
    public IEODetails update(IEODetails ieoDetails) {

        String sql = "UPDATE IEO_INFO SET currency_name = :currency_name, maker_id = :maker_id, rate = :rate, amount = :amount," +
                " contributors = :contributors, status = :status, min_amount = :min_amount, max_amount_per_claim = :max_amount_per_claim," +
                " max_amount_per_user = :max_amount_per_user, starts_at = :starts_at, terminates_at = :terminates_at;";

        MapSqlParameterSource params = getGeneralParams(ieoDetails);
        try {
            jdbcTemplate.update(sql, params);
        } catch (DataAccessException e) {
            log.error("Failed to insert IEO Details ", e);
            throw new RuntimeException(String.format("Error insert ieo details for %s to DB", ieoDetails.getCurrencyName()));
        }
        return ieoDetails;
    }

    @Override
    public Collection<IEODetails> findByCurrencyName(String currencyName) {
        String sql = "SELECT * FROM IEO_DETAILS WHERE currency_name = :currencyName";
        MapSqlParameterSource params = new MapSqlParameterSource("currencyName", currencyName);
        try {
            return jdbcTemplate.query(sql, params, ieoDetailsRowMapper());
        } catch (Exception e) {
            log.warn("Failed to find ieoInfos for currency name " + currencyName, e);
            throw e;
        }
    }

    @Override
    public IEODetails findOpenIeoByCurrencyName(String currencyName) {
        String sql = "SELECT * FROM IEO_DETAILS" +
                " WHERE currency_name = :currencyName AND starts_at < CURRENT_TIMESTAMP AND terminates_at >= CURRENT_TIMESTAMP";

        MapSqlParameterSource params = new MapSqlParameterSource("currencyName", currencyName);
        try {
            return jdbcTemplate.queryForObject(sql, params, ieoDetailsRowMapper());
        } catch (Exception e) {
            log.warn("Failed to find open ieoDetails for currency name " + currencyName, e);
            throw null;
        }
    }

    @Override
    public IEODetails findOne(int ieoId) {
        String sql = "SELECT * FROM IEO_DETAILS WHERE id = :ieoId";
        MapSqlParameterSource params = new MapSqlParameterSource("ieoId", ieoId);
        try {
            return jdbcTemplate.queryForObject(sql, params, ieoDetailsRowMapper());
        } catch (DataAccessException e) {
            log.warn("Failed to find ieo details by id: " + ieoId, e);
            return null;
        }
    }

    @Override
    public boolean updateAvailableAmount(int ieoId, BigDecimal availableAmount) {
        String sql = "UPDATE IEO_DETAILS SET available_amount = :availableAmount WHERE id = :ieoId";
        MapSqlParameterSource params = new MapSqlParameterSource("ieoId", ieoId)
                .addValue("availableAmount", availableAmount.doubleValue());
        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public BigDecimal getAvailableAmount(int ieoId) {
        String sql = "SELECT available_amount WHERE id = :ieoId";
        MapSqlParameterSource params = new MapSqlParameterSource("ieoId", ieoId);
        try {
            return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
        } catch (DataAccessException e) {
            log.warn("Failed to retrieve available amount for ieo with id: " + ieoId, e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Collection<IEODetails> findAll() {
        String sql = "SELECT * FROM IEO_DETAILS";
        try {
            return jdbcTemplate.query(sql, ieoDetailsRowMapper());
        } catch (DataAccessException e) {
            return Lists.newArrayList();
        }
    }

    @Override
    public Collection<IEODetails> findAllExceptForMaker(User user) {
        if (user.getRole() == UserRole.ICO_MARKET_MAKER) {
            String sql = "SELECT * FROM IEO_DETAILS WHERE maker_id = :makerId";
            MapSqlParameterSource params = new MapSqlParameterSource("makerId", user.getId());
            try {
                return jdbcTemplate.query(sql, params, ieoDetailsRowMapper());
            } catch (DataAccessException e) {
                return Lists.newArrayList();
            }
        }
        return findAll();
    }

    private RowMapper<IEODetails> ieoDetailsRowMapper() {
        return (rs, row) -> IEODetails.builder()
                .id(rs.getInt("id"))
                .currencyName(rs.getString("currency_name"))
                .makerId(rs.getInt("maker_id"))
                .amount(rs.getBigDecimal("rate"))
                .amount(rs.getBigDecimal("amount"))
                .availableAmount(rs.getBigDecimal("available_amount"))
                .contributors(rs.getInt("contributors"))
                .status(IEODetailsStatus.valueOf(rs.getString("status")))
                .minAmount(rs.getBigDecimal("min_amount"))
                .maxAmountPerClaim(rs.getBigDecimal("max_amount_per_claim"))
                .maxAmountPerUser(rs.getBigDecimal("max_amount_per_user"))
                .startDate(rs.getTimestamp("starts_at").toLocalDateTime())
                .endDate(rs.getTimestamp("terminates_at").toLocalDateTime())
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .createdBy(rs.getInt("created_by"))
                .build();
    }

    private MapSqlParameterSource getGeneralParams(IEODetails ieoDetails) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("currency_name", ieoDetails.getCurrencyName());
        params.addValue("maker_id", ieoDetails.getMakerId());
        params.addValue("rate", ieoDetails.getRate());
        params.addValue("amount", ieoDetails.getAmount());
        params.addValue("available_amount", ieoDetails.getAvailableAmount());
        params.addValue("contributors", ieoDetails.getContributors());
        params.addValue("status", ieoDetails.getStatus().name());
        params.addValue("min_amount", ieoDetails.getMinAmount());
        params.addValue("max_amount_per_claim", ieoDetails.getMaxAmountPerClaim());
        params.addValue("max_amount_per_user", ieoDetails.getMaxAmountPerUser());
        params.addValue("starts_at", ieoDetails.getStartDate());
        params.addValue("terminates_at", ieoDetails.getEndDate());
        params.addValue("version", ieoDetails.getVersion());
        return params;
    }
}

