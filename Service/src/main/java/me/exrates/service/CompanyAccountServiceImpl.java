package me.exrates.service;

import me.exrates.dao.CompanyAccountDao;
import me.exrates.model.CompanyAccount;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public class CompanyAccountServiceImpl implements CompanyAccountService{

    @Autowired
    private CompanyAccountDao companyAccountDao;

    @Override
    public boolean create(CompanyAccount companyAccount) {
        return companyAccountDao.create(companyAccount);
    }
}