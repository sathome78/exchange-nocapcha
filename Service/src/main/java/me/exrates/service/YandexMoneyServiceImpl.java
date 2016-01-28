package me.exrates.service;

import com.yandex.money.api.methods.Token;
import me.exrates.dao.YandexMoneyMerchantDao;
import me.exrates.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service("yandexMoneyService")
public class YandexMoneyServiceImpl implements YandexMoneyService {

    @Autowired
    private YandexMoneyMerchantDao yandexMoneyMerchantDao;

    @Override
    public List<Token> getAllTokens() {
        return yandexMoneyMerchantDao.getAllTokens();
    }

    @Override
    public Token getTokenByUserEmail(String userEmail) {
        return yandexMoneyMerchantDao.getTokenByUserEmail(userEmail);
    }

    @Override
    public boolean addToken(Token token, User user) {
        return yandexMoneyMerchantDao.createToken(token,user.getId());
    }

    @Override
    public boolean updateUserToken(Token newToken, User user) {
        return yandexMoneyMerchantDao.updateTokenByUserEmail(user.getEmail(),newToken);
    }

    @Override
    public boolean deleteUserToken(User user) {
        return yandexMoneyMerchantDao.deleteTokenByUserEmail(user.getEmail());
    }
}