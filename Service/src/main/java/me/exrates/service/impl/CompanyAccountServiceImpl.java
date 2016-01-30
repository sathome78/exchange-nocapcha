package me.exrates.service.impl;

import me.exrates.dao.CompanyAccountDao;
import me.exrates.model.CompanyAccount;
import me.exrates.service.CompanyAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class CompanyAccountServiceImpl implements CompanyAccountService {

    @Autowired
    private CompanyAccountDao companyAccountDao;

    @Override
    public boolean create(CompanyAccount companyAccount) {
        return companyAccountDao.create(companyAccount);
    }
}