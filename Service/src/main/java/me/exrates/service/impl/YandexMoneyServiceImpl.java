package me.exrates.service.impl;

import me.exrates.dao.UserDao;
import me.exrates.dao.YandexMoneyMerchantDao;
import me.exrates.model.User;
import me.exrates.service.UserService;
import me.exrates.service.YandexMoneyService;
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

    @Autowired
    private UserService userService;

    @Override
    public List<String> getAllTokens() {
        return yandexMoneyMerchantDao.getAllTokens();
    }

    @Override
    public String getTokenByUserEmail(String userEmail) {
        return yandexMoneyMerchantDao.getTokenByUserEmail(userEmail);
    }

    @Override
    public boolean addToken(String token, String email) {
        final int id = userService.getIdByEmail(email);
        return yandexMoneyMerchantDao.createToken(token,id);
    }

    @Override
    public boolean updateTokenByUserEmail(String newToken, String email) {
        return yandexMoneyMerchantDao.updateTokenByUserEmail(email,newToken);
    }

    @Override
    public boolean deleteTokenByUserEmail(String email) {
        return yandexMoneyMerchantDao.deleteTokenByUserEmail(email);
    }
}