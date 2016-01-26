package me.exrates.dao;


import com.yandex.money.api.methods.Token;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface YandexMoneyMerchantDao {
    List<Token> getAllTokens();
    Token getTokenByUserEmail(String email);
    boolean createToken(Token token, int userId);
    boolean deleteTokenByUserEmail(String userEmail);
    boolean updateTokenByUserEmail(String userEmail, Token newToken);
}
