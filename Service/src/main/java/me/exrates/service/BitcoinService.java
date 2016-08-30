package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;

import java.math.BigDecimal;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface BitcoinService {

    PendingPayment createInvoice(CreditsOperation operation);

    boolean provideTransaction(int id, String hash, BigDecimal amount);
}
