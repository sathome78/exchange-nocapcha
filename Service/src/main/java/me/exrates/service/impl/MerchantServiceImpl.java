package me.exrates.service.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class MerchantServiceImpl implements MerchantService{

    @Autowired
    private MerchantDao merchantDao;


    @Override
    public Merchant create(Merchant merchant) {
        return merchantDao.create(merchant);
    }

    @Override
    public List<Merchant> findAllByCurrency(Currency currency) {
        return merchantDao.findAllByCurrency(currency.getId());
    }
}