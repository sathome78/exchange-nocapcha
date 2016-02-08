package me.exrates.dao;

import me.exrates.model.CompanyTransaction;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface CompanyTransactionDao {
    CompanyTransaction create(CompanyTransaction companyTransaction);
}