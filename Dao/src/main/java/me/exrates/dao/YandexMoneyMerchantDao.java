package me.exrates.dao;


import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface YandexMoneyMerchantDao {
    List<String> getAllTokens();
    String getTokenByUserEmail(String email);
    boolean createToken(String token, int userId);
    boolean deleteTokenByUserEmail(String userEmail);
    boolean updateTokenByUserEmail(String userEmail, String newToken);
}
