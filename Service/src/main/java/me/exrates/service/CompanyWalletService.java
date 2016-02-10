package me.exrates.service;

import me.exrates.model.CompanyWallet;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface CompanyWalletService {
    CompanyWallet create(CompanyWallet companyWallet);
    CompanyWallet findByCurrencyId(int currencyId);
}