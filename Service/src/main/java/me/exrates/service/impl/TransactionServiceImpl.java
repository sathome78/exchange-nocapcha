package me.exrates.service.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.*;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.TransactionService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.TransactionPersistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionDao transactionDao;

    @Autowired
    private WalletService walletService;

    @Autowired
    private CompanyWalletService companyWalletService;

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public Transaction provideTransaction(CreditsOperation creditsOperation) {
        final Currency currency = creditsOperation.getCurrency();
        final User user = creditsOperation.getUser();

        CompanyWallet companyWallet = companyWalletService.findByCurrency(currency);
        companyWallet = companyWallet == null ? companyWalletService.create(currency) : companyWallet;

        Wallet userWallet = walletService.findByUserAndCurrency(user,currency);
        userWallet = userWallet == null ? walletService.create(user,currency) : userWallet;

        Transaction transaction = new Transaction();
        transaction.setAmount(creditsOperation.getAmount());
        transaction.setCommissionAmount(creditsOperation.getCommissionAmount());
        transaction.setCommission(creditsOperation.getCommission());
        transaction.setCompanyWallet(companyWallet);
        transaction.setUserWallet(userWallet);
        transaction.setCurrency(currency);
        transaction.setDatetime(LocalDateTime.now());
        transaction.setMerchant(creditsOperation.getMerchant());
        transaction.setOperationType(creditsOperation.getOperationType());
        switch (creditsOperation.getOperationType()) {
            case INPUT :
                walletService.depositActiveBalance(userWallet,creditsOperation.getAmount());
                companyWalletService.deposit(companyWallet,creditsOperation.getAmount(),
                        creditsOperation.getCommissionAmount());
                break;
            case OUTPUT:
                walletService.withdrawActiveBalance(userWallet,creditsOperation.getAmount());
                companyWalletService.withdraw(companyWallet,creditsOperation.getAmount(),
                        creditsOperation.getCommissionAmount());
                break;
        }
        transaction = transactionDao.create(transaction);
        if (transaction==null) {
            throw new TransactionPersistException("Failed to provide transaction ");
        }
        return transaction;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> findAllByUserWallets(List<Integer> userWalletsIds) {
        if (userWalletsIds.size()==0) {
            return null;
        }
        return transactionDao.findAllByUserWallets(userWalletsIds);
    }
}