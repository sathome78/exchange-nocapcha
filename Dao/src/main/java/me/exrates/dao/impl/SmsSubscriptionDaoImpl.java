package me.exrates.dao.impl;

import me.exrates.dao.SmsSubscriptionDao;
import me.exrates.model.dto.SmsSubscriptionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 09.10.2017.
 */
@Repository
public class SmsSubscriptionDaoImpl implements SmsSubscriptionDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public int create(SmsSubscriptionDto dto) {
        final String sql = "INSERT INTO SMS_SUBSCRIPTION (user_id, contact) VALUES" +
                "(:user_id, :contact)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", dto.getUserId());
        params.addValue("userAccount", dto.getContact());
        jdbcTemplate.update(sql, params, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public void update(SmsSubscriptionDto dto) {
        final String sql = " UPDATE SMS_SUBSCRIPTION " +
                " SET contact = :contact " +
                " WHERE id = :id ";
        Map<String, Object> params = new HashMap<>();
        params.put("id", dto.getId());
        params.put("userAccount", dto.getContact());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public SmsSubscriptionDto getByUserId(int userId) {
        final String sql = "SELECT * FROM SMS_SUBSCRIPTION " +
                "WHERE user_id = :user_id";
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        jdbcTemplate.update(sql, params);
        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            SmsSubscriptionDto dto = new SmsSubscriptionDto();
            dto.setUserId(rs.getInt("user_id"));
            dto.setContact(rs.getLong("userAccount"));
            dto.setId(rs.getInt("id"));
            return dto;
        });
    }
}
