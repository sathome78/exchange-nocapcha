package me.exrates.service;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import me.exrates.model.PendingPayment;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface BlockchainService {

    PendingPayment createPaymentInvoice(CreditsOperation creditsOperation);

    PendingPayment findByInvoiceId(int invoiceId);

    String sendPaymentNotification(String address, String email,Locale locale, CreditsOperation creditsOperation);

    Optional<String> notCorresponds(Map<String,String> pretended,PendingPayment actual);

    String approveBlockchainTransaction(PendingPayment payment,Map<String,String> params);

    void provideOutputPayment(Payment payment, CreditsOperation creditsOperation);
}