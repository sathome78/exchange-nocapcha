package me.exrates.service;

import me.exrates.model.BTCTransaction;
import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface BitcoinService {

    PendingPayment createInvoice(CreditsOperation operation);

    boolean provideTransaction(int id, String hash, BigDecimal amount);

    Map<Transaction, BTCTransaction> getBitcoinTransactions();
}
