package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface EDRCService {

    PendingPayment createPaymentInvoice(CreditsOperation creditsOperation);

    PendingPayment findByInvoiceId(int invoiceId);

    boolean confirmPayment(final String xml, final String signature);

    int CONFIRMATIONS = 10;
}