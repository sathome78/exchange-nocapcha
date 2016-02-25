package me.exrates.dao;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import me.exrates.model.User;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionDao {

    Transaction create(Transaction transaction);

    List<Transaction> findAllByUserId(int id);
}