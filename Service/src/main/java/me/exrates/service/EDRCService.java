package me.exrates.service;

import me.exrates.model.PendingPayment;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface EDRCService {

    PendingPayment createPaymentInvoice(CreditsOperation creditsOperation);

    PendingPayment findByInvoiceId(int invoiceId);

    void provideOutputPayment(Payment payment, CreditsOperation creditsOperation);

    boolean verifyPayment(final String response);
}