package me.exrates.dao;

import java.util.List;

import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface CompanyWalletDao {

    CompanyWallet create(Currency currency);

    CompanyWallet findByCurrencyId(Currency currency);

    boolean update(CompanyWallet companyWallet);
}