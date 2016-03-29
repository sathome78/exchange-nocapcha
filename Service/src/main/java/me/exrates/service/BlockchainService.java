package me.exrates.service;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import me.exrates.model.BlockchainPayment;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface BlockchainService {

    BlockchainPayment createPaymentInvoice(CreditsOperation creditsOperation);

    BlockchainPayment findByInvoiceId(int invoiceId);

    String sendPaymentNotification(BlockchainPayment payment, String email,Locale locale);

    Optional<String> notCorresponds(Map<String,String> pretended,BlockchainPayment actual);

    String approveBlockchainTransaction(BlockchainPayment payment,Map<String,String> params);

    void provideOutputPayment(Payment payment, CreditsOperation creditsOperation);
}