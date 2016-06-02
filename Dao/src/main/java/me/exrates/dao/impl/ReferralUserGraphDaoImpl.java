package me.exrates.dao.impl;

import me.exrates.dao.ReferralUserGraphDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class ReferralUserGraphDaoImpl implements ReferralUserGraphDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public ReferralUserGraphDaoImpl(final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(final int child, final int parent) {
        final String sql = "INSERT INTO REFERRAL_USER_GRAPH (child, parent) VALUES (:child, :parent)";
        final Map<String, Integer> params = new HashMap<>();
        params.put("child", child);
        params.put("parent", parent);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public Integer getParent(final Integer child) {
        final String sql = "SELECT parent FROM REFERRAL_USER_GRAPH WHERE child = :child";
        try {
            return jdbcTemplate.queryForObject(sql, singletonMap("child", child), Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
