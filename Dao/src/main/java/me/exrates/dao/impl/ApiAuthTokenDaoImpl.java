package me.exrates.dao.impl;

import me.exrates.dao.ApiAuthTokenDao;
import me.exrates.model.ApiAuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Created by OLEG on 04.11.2016.
 */
@Repository
public class ApiAuthTokenDaoImpl implements ApiAuthTokenDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public long createToken(ApiAuthToken token) {
        String sql = "INSERT INTO API_AUTH_TOKEN(username, value) VALUES(:username, :value)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", token.getUsername())
                .addValue("value", token.getValue());
        int result = jdbcTemplate.update(sql, params, keyHolder);
        long id = keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    @Override
    public Optional<ApiAuthToken> retrieveTokenById(Long id) {
        String sql = "SELECT id, username, value, last_request FROM API_AUTH_TOKEN WHERE id = :id";
        Map<String, Long> params = Collections.singletonMap("id", id);
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, params, (rs, row) -> {
                ApiAuthToken token = new ApiAuthToken();
                token.setId(rs.getLong("id"));
                token.setUsername(rs.getString("username"));
                token.setValue(rs.getString("value"));
                token.seLastRequest(rs.getTimestamp("last_request").toLocalDateTime());
                return token;
            }));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean prolongToken(Long id) {
        String sql = "UPDATE API_AUTH_TOKEN SET last_request = CURRENT_TIMESTAMP WHERE id = :id";
        Map<String, Object> params = Collections.singletonMap("id", id);
        return jdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public  boolean deleteExpiredToken(Long id) {
        String sql = "DELETE FROM API_AUTH_TOKEN WHERE id = :id";
        Map<String, Object> params = Collections.singletonMap("id", id);
        return jdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public int deleteAllExpired(long tokenDuration) {
        String sql = "DELETE FROM API_AUTH_TOKEN WHERE DATE_ADD(last_request,INTERVAL :duration SECOND) < now()";
        return jdbcTemplate.update(sql, Collections.singletonMap("duration", tokenDuration));
    }


}
