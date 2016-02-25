package me.exrates.dao.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.model.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.AbstractMap.SimpleEntry;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Stream.of;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class MerchantDaoImpl implements MerchantDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Merchant create(Merchant merchant) {
        final String sql = "INSERT INTO MERCHANT (description, name) VALUES (:description,:name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("description",merchant.getDescription());
        params.addValue("name",merchant.getName());
        if (jdbcTemplate.update(sql, params, keyHolder)>0) {
            merchant.setId(keyHolder.getKey().intValue());
            return merchant;
        }
        return null;
    }

    @Override
    public Merchant findById(int id) {
        final String sql = "SELECT * FROM MERCHANT WHERE id = :id";
        final Map<String, Integer> params = unmodifiableMap(of(
                new SimpleEntry<>("id", id))
                .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
        return jdbcTemplate.queryForObject(sql,params,new BeanPropertyRowMapper<>(Merchant.class));
    }


    @Override
    public List<Merchant> findAllByCurrency(int currencyId) {
        final String sql = "SELECT * FROM MERCHANT WHERE id in (SELECT merchant_id FROM MERCHANT_CURRENCY WHERE currency_id = :currencyId)";
        Map<String,Integer> params = new HashMap<>();
        params.put("currencyId",currencyId);
        try {
            return jdbcTemplate.query(sql, params, (resultSet, i) -> {
                Merchant merchant = new Merchant();
                merchant.setDescription(resultSet.getString("description"));
                merchant.setId(resultSet.getInt("id"));
                merchant.setName(resultSet.getString("name"));
                return merchant;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public BigDecimal getMinSum(int merchant, int currency) {
        final String sql = "SELECT min_sum FROM MERCHANT_CURRENCY WHERE merchant_id = :merchant AND currency_id = :currency";
        final Map<String, Integer> params = unmodifiableMap(of(
                new SimpleEntry<>("merchant", merchant),
                new SimpleEntry<>("currency",currency))
                .collect(toMap(SimpleEntry::getKey, SimpleEntry::getValue)));
        return jdbcTemplate.queryForObject(sql,params,BigDecimal.class);
    }
}