package me.exrates.dao.impl;

import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Maks on 02.10.2017.
 */
@Repository
public class NotificationUserSettingsDaoImpl implements NotificationUserSettingsDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static RowMapper<NotificationsUserSetting> notificationsUserSettingRowMapper = (rs, idx) -> {
        NotificationsUserSetting setting = new NotificationsUserSetting();
        setting.setId(rs.getInt("id"));
        setting.setUserId(rs.getInt("user_id"));
        setting.setNotificatorId(rs.getInt("notificator_id"));
        setting.setNotificationMessageEventEnum(NotificationMessageEventEnum.valueOf(rs.getString("event_name")));
        return setting;
    };



    @Override
    public Optional<NotificationsUserSetting> getByUserAndEvent(int userId, NotificationMessageEventEnum event) {
        String sql = "SELECT UN.* FROM USER_NOTIFICATION_MESSAGE_SETTINGS UN " +
                "WHERE UN.user_id = :user_id AND event_name = :event ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("event", event.name());
        }};
        return Optional.ofNullable(namedParameterJdbcTemplate.queryForObject(sql, params, notificationsUserSettingRowMapper));

    }

}
