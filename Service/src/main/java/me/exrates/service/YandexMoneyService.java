package me.exrates.service;

import com.yandex.money.api.methods.Token;
import me.exrates.model.User;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface YandexMoneyService {
    List<Token> getAllTokens();
    Token getTokenByUser(User user);
    boolean addToken(Token token, User user);
    boolean updateUserToken(Token newToken, User user);
    boolean deleteTokenByUser(User user);
}
