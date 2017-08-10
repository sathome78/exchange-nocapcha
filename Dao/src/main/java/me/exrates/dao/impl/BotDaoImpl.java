package me.exrates.dao.impl;

import me.exrates.dao.BotDao;
import me.exrates.model.BotLaunchSettings;
import me.exrates.model.BotTradingSettings;
import me.exrates.model.BotTrader;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.PriceGrowthDirection;
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
        String sql = "UPDATE BOT_TRADER SET is_enabled = :is_enabled, " +
                "order_accept_timeout = :accept_timeout " +
                "WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", botTrader.getId());
        params.put("is_enabled", botTrader.getIsEnabled());
        params.put("accept_timeout", botTrader.getAcceptDelayInSeconds());
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public Optional<BotTradingSettings> retrieveBotSettingsForCurrencyPairAndOrderType(int botId, int currencyPairId, int orderTypeId) {
        String sql = "SELECT BTS.id AS trading_id, BLCH.id AS launch_id, BLCH.bot_trader_id, BLCH.currency_pair_id, BTS.order_type_id, BLCH.is_enabled, " +
                "BLCH.launch_interval_minutes, BLCH.create_timeout_seconds, BLCH.quantity_per_sequence, " +
                "BTS.max_amount, BTS.min_amount, BTS.max_price, BTS.min_price, BTS.price_step, BTS.price_growth_direction " +
                "FROM BOT_LAUNCH_SETTINGS BLCH " +
                "JOIN BOT_TRADING_SETTINGS BTS ON BLCH.id = BTS.bot_launch_settings_id " +
                "WHERE BLCH.bot_trader_id = :bot_id AND BLCH.currency_pair_id = :currency_pair_id AND BTS.order_type_id = :order_type_id";
        Map<String, Integer> params = new HashMap<>();
        params.put("bot_id", botId);
        params.put("currency_pair_id", currencyPairId);
        params.put("order_type_id", orderTypeId);

        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                BotLaunchSettings launchSettings = new BotLaunchSettings();
                launchSettings.setId(rs.getInt("launch_id"));
                launchSettings.setBotId(rs.getInt("bot_trader_id"));
                launchSettings.setCurrencyPairId(rs.getInt("currency_pair_id"));
                launchSettings.setIsEnabledForPair(rs.getBoolean("is_enabled"));
                launchSettings.setLaunchIntervalInMinutes(rs.getInt("launch_interval_minutes"));
                launchSettings.setCreateTimeoutInSeconds(rs.getInt("create_timeout_seconds"));
                launchSettings.setQuantityPerSequence(rs.getInt("quantity_per_sequence"));

                BotTradingSettings tradingSettings = new BotTradingSettings();
                tradingSettings.setId(rs.getInt("trading_id"));
                tradingSettings.setBotLaunchSettings(launchSettings);
                tradingSettings.setOrderType(OrderType.convert(rs.getInt("order_type_id")));
                tradingSettings.setMaxAmount(rs.getBigDecimal("max_amount"));
                tradingSettings.setMinAmount(rs.getBigDecimal("min_amount"));
                tradingSettings.setMaxPrice(rs.getBigDecimal("max_price"));
                tradingSettings.setMinPrice(rs.getBigDecimal("min_price"));
                tradingSettings.setPriceStep(rs.getBigDecimal("price_step"));
                tradingSettings.setDirection(PriceGrowthDirection.valueOf(rs.getString("price_growth_direction")));
                return tradingSettings;
            }));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }



    @Override
    public void updatePriceGrowthDirection(int settingsId, PriceGrowthDirection direction) {
        String sql = "UPDATE BOT_TRADING_SETTINGS SET price_growth_direction = :direction WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", settingsId);
        params.put("direction", direction.name());
        namedParameterJdbcTemplate.update(sql, params);
    }

    @Override
    public void setEnabledForCurrencyPair(int botId, int currencyPairId, boolean isEnabled) {
        String sql = "UPDATE BOT_LAUNCH_SETTINGS SET is_enabled = :is_enabled WHERE bot_trader_id = :bot_id AND currency_pair_id = :currency_pair_id";
        Map<String, Object> params = new HashMap<>();
        params.put("bot_id", botId);
        params.put("currency_pair_id", currencyPairId);
        params.put("is_enabled", isEnabled);
        namedParameterJdbcTemplate.update(sql, params);
    }
}
