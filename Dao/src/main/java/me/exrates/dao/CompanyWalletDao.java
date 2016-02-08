package me.exrates.dao;

import me.exrates.model.CompanyWallet;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface CompanyWalletDao {

    CompanyWallet create(CompanyWallet companyWallet);
    CompanyWallet findByCurrencyId(int currencyId);
}