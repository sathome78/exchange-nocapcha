package me.exrates.dao.impl;


import me.exrates.dao.YandexMoneyMerchantDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.exrates.jdbc.TokenRowMapper.tokenRowMapper;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public final class YandexMoneyMerchantDaoImpl implements YandexMoneyMerchantDao {

    @Autowired
    private DataSource dataSource;

    private static final String YMONEY_TABLE = "YANDEX_MONEY_MERCHANT";

    @Override
    public List<String> getAllTokens() {
        final String sql = "SELECT * FROM " + YMONEY_TABLE;
        return new NamedParameterJdbcTemplate(dataSource).query(sql, tokenRowMapper);
    }

    @Override
    public boolean createToken(String token, int userId) {
        final String sql = "INSERT "+ YMONEY_TABLE +" (user_id, access_token, expiration_date) VALUES (:userId,:accessToken,:expDate)";
        final Map<String,String> params = new HashMap<>();
        params.put("userId",String.valueOf(userId));
        params.put("accessToken",token);
        params.put("expDate",Date.valueOf(LocalDate.now().plusYears(3L)).toString());
        return new NamedParameterJdbcTemplate(dataSource)
                .update(sql, params) > 0;
    }

    @Override
    public String getTokenByUserEmail(String userEmail) {
        final String sql = "SELECT access_token FROM " + YMONEY_TABLE + " WHERE user_id IN " +
                "(SELECT id FROM USER WHERE email=:userEmail)";
        final Map<String, String> params = new HashMap<>();
        params.put("userEmail", userEmail);
        try {
            return new NamedParameterJdbcTemplate(dataSource)
                    .queryForObject(sql, params, tokenRowMapper);

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean deleteTokenByUserEmail(String userEmail) {
        final String sql = "DELETE FROM "+ YMONEY_TABLE +" WHERE user_id IN " +
                "(SELECT id FROM USER WHERE email=:userEmail)";
        final Map<String,String> params = new HashMap<>();
        params.put("userEmail",userEmail);
        return new NamedParameterJdbcTemplate(dataSource)
                .update(sql,params) > 0;
    }

    @Override
    public boolean updateTokenByUserEmail(String userEmail, String newToken) {
        final String sql = "UPDATE "+ YMONEY_TABLE +" SET access_token=:accessToken where user_id IN " +
                "(SELECT id FROM USER WHERE email=:userEmail)";
        final Map<String,String> params = new HashMap<>();
        params.put("accessToken",newToken);
        params.put("userEmail",userEmail);
        return new NamedParameterJdbcTemplate(dataSource)
                .update(sql,params) > 0;
    }
}