package me.exrates.dao.impl;

import me.exrates.dao.TemporalTokenDao;
import me.exrates.model.TemporalToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
@Repository
public class TemporalTokenDaoImpl implements TemporalTokenDao {

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public boolean updateTemporalToken(TemporalToken token) {
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("value", token.getValue());
        namedParameters.put("user_id", String.valueOf(token.getUserId()));
        namedParameters.put("id", String.valueOf(token.getId()));
        return namedParameterJdbcTemplate.update(UPDATE_TEMPORAL_TOKEN, namedParameters) > 0;
    }
}
