package me.exrates.service;

import java.util.Map;
import java.util.Optional;
import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface BlockchainService {

    PendingPayment createPaymentInvoice(CreditsOperation creditsOperation);

    PendingPayment findByInvoiceId(int invoiceId);

    Optional<String> notCorresponds(Map<String,String> pretended,PendingPayment actual);

    String approveBlockchainTransaction(PendingPayment payment,Map<String,String> params);
}
