package me.exrates.dao.impl;

import me.exrates.dao.UserSettingsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
public class UserSettingsDaoImpl implements UserSettingsDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int addCallBackUrl(final int userId, final String callbackURL) {
        String addCallbackQuery = "INSERT INTO CALLBACK_SETTINGS VALUES(:userId,:callbackUrl)";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userId", userId);
        queryParams.put("callbackURL", callbackURL);
        return namedParameterJdbcTemplate.update(addCallbackQuery, queryParams);
    }

    public int updateCallbackURL(final int userId, final String callbackURL) {
        String updateCallbackQuery = "UPDATE CALLBACK_SETTINGS SET CALLBACK_URL=:callbackUrl WHERE USER_ID=:userId";

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userId", userId);
        queryParams.put("callbackURL", callbackURL);

        return namedParameterJdbcTemplate.update(updateCallbackQuery, queryParams);
    }

    @Override
    public String getCallBackURLByUserId(final int userId) {
        String getCallbackURL = "SELECT CALLBACK_URL FROM CALLBACK_SETTINGS WHERE USER_ID=:userId";
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("userId", userId);

        List<String> result = namedParameterJdbcTemplate.query(getCallbackURL, queryParams, (resultSet, i) -> resultSet.getString("CALLBACK_URL"));
        return result.size() > 0 ? result.get(0) : null;
    }
}
