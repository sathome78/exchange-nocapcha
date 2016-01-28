package me.exrates.dao.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yandex.money.api.methods.Token;
import me.exrates.dao.YandexMoneyMerchantDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

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
public final class YandexMoneyMerchantDaoImpl implements YandexMoneyMerchantDao {

    @Autowired
    private DataSource dataSource;

    private static final String YMONEY_TABLE = "YANDEX_MONEY_MERCHANT";

    @Override
    public List<Token> getAllTokens() {
        final String sql = "SELECT * FROM " + YMONEY_TABLE;
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        return namedParameterJdbcTemplate.query(sql, tokenRowMapper);
    }

    @Override
    public boolean createToken(Token token, int userId) {
        final String sql = "INSERT "+ YMONEY_TABLE +" (user_id, access_token, expiration_date) VALUES (:userId,:accessToken,:expDate)";
        Map<String,String> params = new HashMap<>();
        params.put("userId",String.valueOf(userId));
        params.put("accessToken",token.accessToken);
        params.put("expDate",Date.valueOf(LocalDate.now().plusYears(3L)).toString());
        return new NamedParameterJdbcTemplate(dataSource)
                .update(sql, params) > 0;
    }

    @Override
    public Token getTokenByUserEmail(String userEmail) {
        final String sql = "SELECT access_token FROM "+YMONEY_TABLE+" WHERE user_id IN " +
                "(SELECT id FROM USER WHERE email=:userEmail)";
        final Map<String, String> params = Maps.asMap(Sets.newHashSet("userEmail"), x -> userEmail);
        return new NamedParameterJdbcTemplate(dataSource)
                .queryForObject(sql, params, tokenRowMapper);
    }

    @Override
    public boolean deleteTokenByUserEmail(String userEmail) {
        final String sql = "DELETE FROM "+ YMONEY_TABLE +" WHERE user_id IN " +
                "(SELECT id FROM USER WHERE email=:userEmail)";
        return new NamedParameterJdbcTemplate(dataSource)
                .update(sql,Maps.asMap(Sets.newHashSet("userEmail"), x -> userEmail)) > 0;
    }

    @Override
    public boolean updateTokenByUserEmail(String userEmail, Token newToken) {
        final String sql = "UPDATE "+ YMONEY_TABLE +" SET access_token=:accessToken where user_id IN " +
                "(SELECT id FROM USER WHERE email=:userEmail)";
        Map<String,String> params = new HashMap<>();
        params.put("accessToken",newToken.accessToken);
        params.put("userEmail",userEmail);
        return new NamedParameterJdbcTemplate(dataSource)
                .update(sql,params) > 0;
    }
}