package me.exrates.dao;

import me.exrates.model.Transaction;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionDao {
    boolean create(Transaction transaction);

}