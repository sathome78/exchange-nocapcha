package me.exrates.dao;

import me.exrates.model.Transaction;
import me.exrates.model.User;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionDao {
    boolean create(Transaction transaction);
    List<Transaction> findAllByUserId(int id);
}