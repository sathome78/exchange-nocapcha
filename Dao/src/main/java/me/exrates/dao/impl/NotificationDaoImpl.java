package me.exrates.dao.impl;

import me.exrates.dao.NotificationDao;
import me.exrates.model.Notification;
import me.exrates.model.enums.NotificationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by OLEG on 09.11.2016.
 */
@Repository
public class NotificationDaoImpl implements NotificationDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public long createNotification(Notification notification) {
        String sql = "INSERT INTO NOTIFICATION (user_id, title, message, notification_event_id) " +
                " VALUES (:user_id, :title, :message, :notification_event_id) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", notification.getReceiverUserId())
                .addValue("title", notification.getTitle())
                .addValue("message", notification.getMessage())
                .addValue("notification_event_id", notification.getCause().getEventType());
        int result = jdbcTemplate.update(sql, params, keyHolder);
        long id = keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    @Override
    public List<Notification> findAllByUser(Integer userId) {
        String sql = "SELECT id, user_id, title, message, creation_time, notification_event_id, is_read " +
                " FROM NOTIFICATION " +
                " WHERE user_id = :user_id";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return jdbcTemplate.query(sql, params, (resultSet, row) -> {
            Notification notification = new Notification();
            notification.setId(resultSet.getLong("id"));
            notification.setReceiverUserId(resultSet.getInt("user_id"));
            notification.setTitle(resultSet.getString("title"));
            notification.setMessage(resultSet.getString("message"));
            notification.setCreationTime(resultSet.getTimestamp("creation_time").toLocalDateTime());
            notification.setCause(NotificationEvent.convert(resultSet.getInt("notification_event_id")));
            notification.setRead(resultSet.getBoolean("is_read"));
            return notification;
        });
    }

    @Override
    public boolean setRead(Long notificationId) {
        String sql = "UPDATE NOTIFICATION SET is_read = 1 WHERE id = :id";
        Map<String, Long> params = Collections.singletonMap("id", notificationId);
        return jdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public boolean remove(Long notificationId) {
        String sql = "DELETE FROM NOTIFICATION WHERE id = :id";
        Map<String, Long> params = Collections.singletonMap("id", notificationId);
        return jdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public int setReadAllByUser(Integer userId) {
        String sql = "UPDATE NOTIFICATION SET is_read = 1 WHERE user_id = :user_id";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return jdbcTemplate.update(sql, params);
    }

    @Override
    public int removeAllByUser(Integer userId) {
        String sql = "DELETE FROM NOTIFICATION WHERE user_id = :user_id";
        Map<String, Integer> params = Collections.singletonMap("user_id", userId);
        return jdbcTemplate.update(sql, params);
    }



}
