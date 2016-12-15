package me.exrates.dao.impl;

import me.exrates.dao.StockExchangeDao;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by OLEG on 14.12.2016.
 */
@Repository
public class StockExchangeDaoImpl implements StockExchangeDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void saveStockExchangeRate(StockExchangeRate stockExchangeRate) {
        String sql = "INSERT INTO STOCK_EXRATE(currency_pair_id, stock_exchange_id, exrate) VALUES(:currency_pair_id, :stock_exchange_id, :exrate)";
        Map<String, Number> params = new HashMap<>();
        params.put("currency_pair_id", stockExchangeRate.getCurrencyPair().getId());
        params.put("stock_exchange_id", stockExchangeRate.getStockExchange().getId());
        params.put("exrate", stockExchangeRate.getExrate());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public StockExchange getStockExchangeByName(String name) {
        String sql = "SELECT id, name, link FROM STOCK_EXCHANGE WHERE name = :name";
        Map<String, String> params = Collections.singletonMap("name", name);
        return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(StockExchange.class));
    }






}
