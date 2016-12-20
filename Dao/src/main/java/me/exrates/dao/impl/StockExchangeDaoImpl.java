package me.exrates.dao.impl;

import me.exrates.dao.StockExchangeDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.dto.StockExchangeRateDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 14.12.2016.
 */
@Repository
public class StockExchangeDaoImpl implements StockExchangeDao {

    private static final Logger LOGGER = LogManager.getLogger(StockExchangeDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private final String SELECT_STOCK_EXCHANGE = "SELECT STOCK_EXCHANGE.id AS stock_exchange_id, " +
            "STOCK_EXCHANGE.name AS stock_exchange_name, STOCK_EXCHANGE.link, CURRENCY_PAIR.id, " +
            "CURRENCY_PAIR.currency1_id, CURRENCY_PAIR.currency2_id, CURRENCY_PAIR.name, " +
            "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
            "(select name from CURRENCY where id = currency2_id) as currency2_name " +
            " FROM STOCK_EXCHANGE " +
            "INNER JOIN STOCK_CURRENCY_PAIR ON STOCK_CURRENCY_PAIR.stock_exchange_id = STOCK_EXCHANGE.id " +
            "INNER JOIN CURRENCY_PAIR ON STOCK_CURRENCY_PAIR.currency_pair_id = CURRENCY_PAIR.id ";

    private final String CREATE_STOCK_EXRATE = "INSERT INTO STOCK_EXRATE(currency_pair_id, stock_exchange_id, price_buy, price_sell, price_low, price_high, volume) " +
            "VALUES(:currency_pair_id, :stock_exchange_id, :price_buy, :price_sell, :price_low, :price_high, :volume)";

    private final ResultSetExtractor<List<StockExchange>> stockExchangeResultSetExtractor = (resultSet -> {
        List<StockExchange> result = new ArrayList<>();
        StockExchange stockExchange = null;
        int lastStockExchangeId = 0;
        while (resultSet.next()) {
            int currentStockExchangeId = resultSet.getInt("stock_exchange_id");
            if (currentStockExchangeId != lastStockExchangeId) {
                lastStockExchangeId = currentStockExchangeId;
                stockExchange = new StockExchange();
                result.add(stockExchange);
                stockExchange.setId(currentStockExchangeId);
                stockExchange.setName(resultSet.getString("stock_exchange_name"));
                stockExchange.setLink(resultSet.getString("link"));
            }
            CurrencyPair currencyPair = CurrencyDaoImpl.currencyPairRowMapper.mapRow(resultSet, resultSet.getRow());
            if (stockExchange != null) {
                stockExchange.getAvailableCurrencyPairs().add(currencyPair);
            }
        }
        return result;
    });

    @Override
    public void saveStockExchangeStats(StockExchangeStats stockExchangeRate) {
        Map<String, Number> params = new HashMap<>();
        params.put("currency_pair_id", stockExchangeRate.getCurrencyPairId());
        params.put("stock_exchange_id", stockExchangeRate.getStockExchangeId());
        params.put("price_buy", stockExchangeRate.getPriceBuy());
        params.put("price_sell", stockExchangeRate.getPriceSell());
        params.put("price_low", stockExchangeRate.getPriceLow());
        params.put("price_high", stockExchangeRate.getPriceHigh());
        params.put("volume", stockExchangeRate.getVolume());
        jdbcTemplate.update(CREATE_STOCK_EXRATE, params);
    }

    @Override
    public void saveStockExchangeRates(List<StockExchangeStats> stockExchangeRates) {
        Map<String, Object>[] batchValues = stockExchangeRates.stream().map(stockExchangeRate -> {
            Map<String, Object> values = new HashMap<String, Object>() {{
                put("currency_pair_id", stockExchangeRate.getCurrencyPairId());
                put("stock_exchange_id", stockExchangeRate.getStockExchangeId());
                put("stock_exchange_id", stockExchangeRate.getStockExchangeId());
                put("price_buy", stockExchangeRate.getPriceBuy());
                put("price_sell", stockExchangeRate.getPriceSell());
                put("price_low", stockExchangeRate.getPriceLow());
                put("price_high", stockExchangeRate.getPriceHigh());
                put("volume", stockExchangeRate.getVolume());
            }};
            return values;
        }).collect(Collectors.toList()).toArray(new Map[stockExchangeRates.size()]);
        jdbcTemplate.batchUpdate(CREATE_STOCK_EXRATE, batchValues);
    }

    @Override
    public Optional<StockExchange> findStockExchangeByName(String name) {
        String sql = SELECT_STOCK_EXCHANGE +
                "WHERE STOCK_EXCHANGE.name = :name";
        Map<String, String> params = Collections.singletonMap("name", name);
        List<StockExchange> result =  jdbcTemplate.query(sql, params, stockExchangeResultSetExtractor);
        if (result.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(result.get(0));
    }

    @Override
    public List<StockExchange> findAll() {
        return jdbcTemplate.query(SELECT_STOCK_EXCHANGE, stockExchangeResultSetExtractor);
    }

    @Override
    public List<StockExchangeRateDto> getStockExchangeStatistics(List<Integer> currencyPairIds) {
        String currencyPairClause = currencyPairIds == null ? "" : " WHERE stock_1.currency_pair_id IN (:currency_pair_ids) ";
        String sql = "SELECT stock_1.currency_pair_id, stock_1.stock_exchange_id, " +
                "              CURRENCY_PAIR.name AS currency_pair_name, STOCK_EXCHANGE.name AS stock_exchange_name, " +
                "              stock_1.price_buy, stock_1.price_sell, stock_1.price_low, stock_1.price_high, stock_1.volume," +
                "              stock_1.date FROM STOCK_EXRATE AS stock_1 " +
                "              JOIN (SELECT currency_pair_id, stock_exchange_id, MAX(STOCK_EXRATE.date) AS date FROM STOCK_EXRATE " +
                "              GROUP BY currency_pair_id, stock_exchange_id) AS stock_2 " +
                "              ON stock_1.currency_pair_id = stock_2.currency_pair_id AND stock_1.stock_exchange_id = stock_2.stock_exchange_id " +
                "              AND stock_1.date = stock_2.date " +
                "              JOIN STOCK_EXCHANGE ON stock_1.stock_exchange_id = STOCK_EXCHANGE.id " +
                "              JOIN CURRENCY_PAIR ON stock_1.currency_pair_id = CURRENCY_PAIR.id " +
                currencyPairClause +
                "              ORDER BY stock_1.currency_pair_id, stock_1.stock_exchange_id;";
        Map<String, List<Integer>> params = currencyPairIds == null ? Collections.EMPTY_MAP :
                Collections.singletonMap("currency_pair_ids", currencyPairIds);


        return jdbcTemplate.query(sql, params, resultSet -> {
            List<StockExchangeRateDto> result = new ArrayList<>();
            StockExchangeRateDto dto = null;
            int lastCurrencyPairId = 0;
            while (resultSet.next()) {
                int currentCurrencyPairId = resultSet.getInt("currency_pair_id");
                if (currentCurrencyPairId != lastCurrencyPairId) {
                    lastCurrencyPairId = currentCurrencyPairId;
                    dto = new StockExchangeRateDto();
                    result.add(dto);
                    dto.setCurrencyPairName(resultSet.getString("currency_pair_name"));
                }
                if (dto != null) {
                    StockExchangeStats stockExchangeStats = new StockExchangeStats();
                    stockExchangeStats.setPriceBuy(resultSet.getBigDecimal("price_buy"));
                    stockExchangeStats.setPriceSell(resultSet.getBigDecimal("price_sell"));
                    stockExchangeStats.setPriceLow(resultSet.getBigDecimal("price_low"));
                    stockExchangeStats.setPriceHigh(resultSet.getBigDecimal("price_high"));
                    stockExchangeStats.setVolume(resultSet.getBigDecimal("volume"));
                    stockExchangeStats.setDate(resultSet.getTimestamp("date").toLocalDateTime());
                    dto.getExchangeStats().put(resultSet.getString("stock_exchange_name"), stockExchangeStats);
                }
            }
            LOGGER.debug(result);
            return result;
        });
    }







}
