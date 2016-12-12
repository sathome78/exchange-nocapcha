package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;

import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface PerfectMoneyService {

    Map<String,String> getPerfectMoneyParams(Transaction transaction);

    void provideOutputPayment(Payment payment, CreditsOperation creditsOperation);

    Transaction preparePaymentTransactionRequest(CreditsOperation creditsOperation);

    boolean provideTransaction(int transaction);

    void invalidateTransaction(Transaction transaction);

    String computePaymentHash(Map<String, String> perfectMoneyParams);

}