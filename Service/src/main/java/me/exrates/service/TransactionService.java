package me.exrates.service;

import me.exrates.model.Transaction;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionService {

    boolean create(Transaction transaction);
}