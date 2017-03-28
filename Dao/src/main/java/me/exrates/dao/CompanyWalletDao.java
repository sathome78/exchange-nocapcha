package me.exrates.dao;

import java.math.BigDecimal;
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

    CompanyWallet findByWalletId(int walletId);

    boolean increaseCommissionBalanceById(Integer id, BigDecimal amount);
}