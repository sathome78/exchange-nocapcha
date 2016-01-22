package me.exrates.dao.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yandex.money.api.methods.Token;
import me.exrates.dao.YandexMoneyMerchantDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class YandexMoneyMerchantDaoImpl implements YandexMoneyMerchantDao {

    @Autowired
    private DataSource dataSource;

    private static final String TABLE = "YANDEX_MONEY_MERCHANT";

    @Override
    public List<Token> getAllTokens() {
        final String sql = "SELECT * FROM " + TABLE;
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        return namedParameterJdbcTemplate.query(sql, (resultSet, i) ->
                new Token(resultSet.getString("access_token"), null));//// TODO: 1/21/16 null or ERROR.Undefined ?

    }

    @Override
    public boolean addAndMapTokenToUserID(Token token, int id) {
        final String sql = "INSERT "+TABLE+" (user_id, access_token, expiration_date) VALUES (:userId,:accessToken,:expDate)";
        Map<String,String> params = new HashMap<>();
        params.put("userId",String.valueOf(id));
        params.put("accessToken",token.accessToken);
        params.put("expDate",Date.valueOf(LocalDate.now().plusYears(3L)).toString());
        return new NamedParameterJdbcTemplate(dataSource)
                .update(sql, params) > 0;
    }

    @Override
    public Token getTokenByUserId(int id) {
        final String sql = "SELECT access_token FROM "+TABLE+" WHERE user_id=:userId";
        final Map<String, String> params = Maps.asMap(Sets.newHashSet("userId"), x -> String.valueOf(id));
        try {
            return new NamedParameterJdbcTemplate(dataSource)
                    .queryForObject(sql, params, (resultSet, i) -> {
                        return new Token(resultSet.getString("access_token"),null);
                    });//// TODO: 1/21/16 null or ERROR.Undefined ?
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean deleteTokenByUserId(int id) {
        final String sql = "DELETE FROM "+TABLE+" WHERE user_id=:userId";
        return new NamedParameterJdbcTemplate(dataSource)
                .update(sql,Maps.asMap(Sets.newHashSet("userId"), x -> String.valueOf(id))) > 0;
    }

    @Override
    public boolean updateTokenByUserId(int id,Token newToken) {
        final String sql = "UPDATE "+TABLE+" SET access_token=:accessToken where user_id=:userId";
        Map<String,String> params = new HashMap<>();
        params.put("accessToken",newToken.accessToken);
        params.put("userId",String.valueOf(id));
        return new NamedParameterJdbcTemplate(dataSource)
                .update(sql,params) > 0;
    }
}
