package me.exrates.jdbc;

import com.yandex.money.api.methods.Token;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */

public final class TokenRowMapper {

    /**
     * Maps database record to object of class {@link com.yandex.money.api.methods.Token}
     * @param null -  error object
     */
    public static final RowMapper<Token> tokenRowMapper = (resultSet, i) ->
            new Token(resultSet.getString("access_token"),null);
}