package me.exrates.dao.impl;

import me.exrates.dao.BotDao;
import me.exrates.model.BotTrader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class BotDaoImpl implements BotDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<BotTrader> retrieveBotTrader() {
        String sql = "SELECT id, user_id, is_enabled, order_accept_timeout FROM BOT_TRADER LIMIT 1";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                BotTrader botTrader = new BotTrader();
                botTrader.setId(rs.getInt("id"));
                botTrader.setUserId(rs.getInt("user_id"));
                botTrader.setIsEnabled(rs.getBoolean("is_enabled"));
                botTrader.setAcceptDelayInSeconds(rs.getInt("order_accept_timeout"));
                return botTrader;
            }));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void createBot(Integer userId) {
        String sql = "INSERT INTO BOT_TRADER (user_id) VALUES (:user_id)";
        namedParameterJdbcTemplate.update(sql, Collections.singletonMap("user_id", userId));
    }

    @Override
    public void updateBot(BotTrader botTrader) {
        String sql = "UPDATE BOT_TRADER SET is_enabled = :is_enabled, order_accept_timeout = :accept_timeout WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", botTrader.getId());
        params.put("is_enabled", botTrader.getIsEnabled());
        params.put("accept_timeout", botTrader.getAcceptDelayInSeconds());
        namedParameterJdbcTemplate.update(sql, params);
    }
}
