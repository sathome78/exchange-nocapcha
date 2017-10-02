package me.exrates.dao.impl;

import me.exrates.dao.NotificationMessageLogDao;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * Created by Maks on 02.10.2017.
 */
@Repository
public class NotificationMessageLogDaoImpl implements NotificationMessageLogDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void saveLogNotification(String userEmail, BigDecimal payAmount, NotificationMessageEventEnum event, NotificationTypeEnum notificationTypeEnum) {
        final String sql = "INSERT INTO NOTIFICATION_LOG (email, type, event, payed_amount) " +
                "VALUES  (:email, :type, :event, :payed_amount)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", userEmail)
                .addValue("type", notificationTypeEnum.name())
                .addValue("event", event.name())
                .addValue("payed_amount", payAmount);
        namedParameterJdbcTemplate.update(sql, params);
    }
}
