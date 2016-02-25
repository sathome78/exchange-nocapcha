package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionService {

    Transaction provideTransaction(CreditsOperation creditsOperation);

    List<Transaction> findAllByUserId(int id);
}