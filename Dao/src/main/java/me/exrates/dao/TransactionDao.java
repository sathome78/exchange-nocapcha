package me.exrates.dao;

import me.exrates.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionDao {

    Transaction create(Transaction transaction);

    Transaction findById(int id);

    boolean provide(int id);

    boolean delete(int id);

    void updateTransactionAmount(int transactionId, BigDecimal amount, BigDecimal commission);

    void updateTransactionConfirmations(int transactionId, int confirmations);

    List<Transaction> findAllByUserWallets(List<Integer> walletIds);
}