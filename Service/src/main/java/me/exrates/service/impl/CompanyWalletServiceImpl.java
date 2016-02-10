package me.exrates.service.impl;

import me.exrates.dao.CompanyWalletDao;
import me.exrates.model.CompanyWallet;
import me.exrates.service.CompanyWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class CompanyWalletServiceImpl implements CompanyWalletService {

    @Autowired
    private CompanyWalletDao companyWalletDao;

    @Override
    public CompanyWallet create(CompanyWallet companyWallet) {
        return companyWalletDao.create(companyWallet);
    }

    @Override
    public CompanyWallet findByCurrencyId(int currencyId) {
        return companyWalletDao.findByCurrencyId(currencyId);
    }
}