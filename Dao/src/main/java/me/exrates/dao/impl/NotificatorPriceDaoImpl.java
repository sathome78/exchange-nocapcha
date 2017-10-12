package me.exrates.dao.impl;

import me.exrates.dao.NotificatorPriceDao;
import me.exrates.model.dto.NotificationPayEventEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Created by Maks on 09.10.2017.
 */
@Repository
public class NotificatorPriceDaoImpl implements NotificatorPriceDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public BigDecimal getMessagePrice(int notificatorId, int roleId, NotificationPayEventEnum payEventEnum) {
        final String sql = "SELECT price FROM NOTIFICATION_PRICE " +
                "WHERE notificator_id = :notificatorId AND role_id = :roleId AND pay_event = :payEvent";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("notificator_id", notificatorId);
        params.addValue("role_id", roleId);
        params.addValue("pay_event", payEventEnum.name());
        return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }
}
