package me.exrates.service.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
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
        Transaction transaction = new Transaction();
        transaction.setAmount(creditsOperation.getAmount());
        transaction.setCommissionAmount(creditsOperation.getCommissionAmount());
        transaction.setCommission(creditsOperation.getCommission());
        transaction.setCompanyWallet(creditsOperation.getCompanyWallet());
        transaction.setUserWallet(creditsOperation.getUserWallet());
        transaction.setCurrency(creditsOperation.getCurrency());
        transaction.setDatetime(LocalDateTime.now());
        transaction.setMerchant(creditsOperation.getMerchant());
        transaction.setOperationType(creditsOperation.getOperationType());
        switch (creditsOperation.getOperationType()) {
            case INPUT :
                walletService.depositActiveBalance(creditsOperation.getUserWallet(),creditsOperation.getAmount());
                companyWalletService.deposit(creditsOperation.getCompanyWallet(),creditsOperation.getAmount(),
                        creditsOperation.getCommissionAmount());
                break;
            case OUTPUT:
                walletService.withdrawActiveBalance(creditsOperation.getUserWallet(),creditsOperation.getAmount());
                companyWalletService.withdraw(creditsOperation.getCompanyWallet(),creditsOperation.getAmount(),
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
        return transactionDao.findAllByUserWallets(userWalletsIds);
    }
}