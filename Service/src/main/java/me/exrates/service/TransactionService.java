package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionService {

    Transaction createTransactionRequest(CreditsOperation creditsOperation);

    Transaction findById(int id);

    void updateTransactionAmount(Transaction transaction, BigDecimal amount);

    void updateTransactionConfirmation(int transactionId, int confirmations);

    void provideTransaction(Transaction transaction);

    void invalidateTransaction(Transaction transaction);

    List<Transaction> findAllByUserWallets(List<Integer> userWalletsIds);
    
    List<OperationViewDto> showMyOperationHistory(String email, Locale locale);

    List<OperationViewDto> showUserOperationHistory(int id, Locale locale);
}