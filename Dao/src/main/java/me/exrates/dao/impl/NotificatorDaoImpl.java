package me.exrates.dao.impl;

import me.exrates.dao.NotificatorsDao;
import me.exrates.model.dto.Notificator;
import me.exrates.model.enums.NotificationPayTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 02.10.2017.
 */
@Repository
public class NotificatorDaoImpl implements NotificatorsDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static RowMapper<Notificator> notificatorRowMapper = (rs, idx) -> {
        Notificator notificator = new Notificator();
        notificator.setId(rs.getInt("id"));
        notificator.setBeanName(rs.getString("bean_name"));
        notificator.setPayTypeEnum(NotificationPayTypeEnum.valueOf(rs.getString("pay_type")));
        return notificator;
    };

    @Override
    public Notificator getById(int id) {
        String sql = "SELECT * FROM NOTIFICATOR WHERE id = :id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("id", id);
        }};
        return namedParameterJdbcTemplate.queryForObject(sql, params, notificatorRowMapper);
    }
}
