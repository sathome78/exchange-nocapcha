package me.exrates.service;

import me.exrates.model.BlockchainPayment;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface EDRCService {

    BlockchainPayment createPaymentInvoice(CreditsOperation creditsOperation);

    BlockchainPayment findByInvoiceId(int invoiceId);

    void provideOutputPayment(Payment payment, CreditsOperation creditsOperation);
}