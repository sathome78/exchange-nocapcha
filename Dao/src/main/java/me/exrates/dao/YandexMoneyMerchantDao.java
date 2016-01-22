package me.exrates.dao;


import com.yandex.money.api.methods.Token;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface YandexMoneyMerchantDao {
    List<Token> getAllTokens();
    Token getTokenByUserId(int id);
    boolean addAndMapTokenToUserID(Token token, int id);
    boolean deleteTokenByUserId(int id);
    boolean updateTokenByUserId(int id,Token newToken);
}
