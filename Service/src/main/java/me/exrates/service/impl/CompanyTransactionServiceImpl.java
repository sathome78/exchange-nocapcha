package me.exrates.service.impl;

import me.exrates.dao.CompanyTransactionDao;
import me.exrates.model.CompanyTransaction;
import me.exrates.service.CompanyTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class CompanyTransactionServiceImpl implements CompanyTransactionService {

    @Autowired
    private CompanyTransactionDao companyTransactionDao;

    @Override
    public CompanyTransaction create(CompanyTransaction companyTransaction) {
        return companyTransactionDao.create(companyTransaction);
    }
}