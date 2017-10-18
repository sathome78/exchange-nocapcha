package me.exrates.dao.impl;

import me.exrates.dao.NotificatorPriceDao;
import me.exrates.model.dto.NotificationPayEventEnum;
import me.exrates.model.dto.NotificatorTotalPriceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Maks on 09.10.2017.
 */
@Repository
public class NotificatorPriceDaoImpl implements NotificatorPriceDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public BigDecimal getFeeMessagePrice(int notificatorId, int roleId) {
        final String sql = "SELECT message_price FROM NOTIFICATION_PRICE " +
                "WHERE notificator_id = :notificator_id AND role_id = :role_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("notificator_id", notificatorId);
        params.addValue("role_id", roleId);
        return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }

    @Override
    public NotificatorTotalPriceDto getPrices(int notificatorId, int roleId) {
        final String sql = "SELECT * FROM NOTIFICATION_PRICE " +
                " WHERE notificator_id = :notificator_id AND role_id = :role_id ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("notificator_id", notificatorId);
        params.addValue("role_id", roleId);
        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            NotificatorTotalPriceDto notificatorTotalPriceDto = new NotificatorTotalPriceDto();
            notificatorTotalPriceDto.setLookupPrice(rs.getBigDecimal("lookup_price").toPlainString());
            notificatorTotalPriceDto.setMessagePrice(rs.getBigDecimal("message_price").toPlainString());
            notificatorTotalPriceDto.setSubscriptionPrice(rs.getBigDecimal("subscribe_price").toPlainString());
            return notificatorTotalPriceDto;
        });
    }

    @Override
    public BigDecimal getSubscriptionPrice(int notificatorId, int roleId) {
        final String sql = "SELECT subscribe_price FROM NOTIFICATION_PRICE " +
                "WHERE notificator_id = :notificator_id AND role_id = :role_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("notificator_id", notificatorId);
        params.addValue("role_id", roleId);
        return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }

    @Override
    public BigDecimal getLookUpPrice(int notificatorId, int roleId) {
        final String sql = "SELECT lookup_price FROM NOTIFICATION_PRICE " +
                "WHERE notificator_id = :notificator_id AND role_id = :role_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("notificator_id", notificatorId);
        params.addValue("role_id", roleId);
        return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }
}
