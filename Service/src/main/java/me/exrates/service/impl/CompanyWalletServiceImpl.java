package me.exrates.service.impl;

import me.exrates.dao.CompanyWalletDao;
import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.CurrencyService;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.WalletPersistException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class CompanyWalletServiceImpl implements CompanyWalletService {

    @Autowired
    private CompanyWalletDao companyWalletDao;
    
    @Autowired
    private CurrencyService currencyService;

    @Override
    public CompanyWallet create(Currency currency) {
        return companyWalletDao.create(currency);
    }

    @Override
    @Transactional(readOnly = true)
	public List<CompanyWallet> getCompanyWallets() {
    	List<CompanyWallet> compWalletList = new ArrayList<CompanyWallet>();
    	List<Currency> currList = currencyService.getAllCurrencies();
    	for(Currency c : currList) {
    		CompanyWallet cw = this.findByCurrency(c);
    		if(cw != null) {
    			compWalletList.add(cw);
    		}
    	}
    	return compWalletList;
	}

	@Override
    @Transactional(readOnly = true)
    public CompanyWallet findByCurrency(Currency currency) {
        return companyWalletDao.findByCurrencyId(currency);
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void deposit(CompanyWallet companyWallet, BigDecimal amount, BigDecimal commissionAmount) {
        final BigDecimal newBalance = companyWallet.getBalance().add(amount);
        final BigDecimal newCommissionBalance = companyWallet.getCommissionBalance().add(commissionAmount);
        System.out.println(newBalance + " NEW BALANCE");
        System.out.println(newCommissionBalance);
        companyWallet.setBalance(newBalance);
        companyWallet.setCommissionBalance(newCommissionBalance);
        if (!companyWalletDao.update(companyWallet)) {
            throw new WalletPersistException("Failed deposit on company wallet " + companyWallet.toString());
        }
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void withdraw(CompanyWallet companyWallet, BigDecimal amount, BigDecimal commissionAmount) {
        final BigDecimal newBalance = companyWallet.getBalance().subtract(amount);
        final BigDecimal newCommissionBalance = companyWallet.getCommissionBalance().add(commissionAmount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new NotEnoughUserWalletMoneyException("POTENTIAL HACKING! Not enough money on Company Account for operation!" + companyWallet.toString());
        }
        companyWallet.setBalance(newBalance);
        companyWallet.setCommissionBalance(newCommissionBalance);
        if (!companyWalletDao.update(companyWallet)) {
            throw new WalletPersistException("Failed withdraw on company wallet " + companyWallet.toString());
        }
    }
}