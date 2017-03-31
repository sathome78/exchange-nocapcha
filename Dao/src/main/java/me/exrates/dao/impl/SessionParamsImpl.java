package me.exrates.dao.impl;

import me.exrates.dao.SessionParamsDao;
import me.exrates.jdbc.OrderRowMapper;
import me.exrates.model.SessionParams;
import me.exrates.model.User;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by maks on 31.03.2017.
 */
@Repository
public class SessionParamsImpl implements SessionParamsDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private RowMapper<SessionParams> getSessionParamsRowMapper() {
        return (resultSet, i) -> {
            final SessionParams sessionParams = new SessionParams();
            sessionParams.setId(resultSet.getInt("id"));
            sessionParams.setUserId(resultSet.getInt("user_id"));
            sessionParams.setUserId(resultSet.getInt("session_time_seconds"));
            sessionParams.setUserId(resultSet.getInt("session_life_type_id"));
            return sessionParams;
        };
    }


    @Override
    public SessionParams getByUserEmail(String userEmail) {
        String sql = "SELECT * FROM SESSION_PARAMS AS SP " +
                "INNER JOIN USER AS U ON U.id = SP.user_id " +
                "WHERE U.email = :email";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("email", userEmail);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, getSessionParamsRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public SessionParams save(SessionParams sessionLifeType) {
        return null;
    }
}
