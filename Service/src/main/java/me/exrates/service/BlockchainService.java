package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;
import me.exrates.service.merchantStrategy.IMerchantService;

import java.util.Map;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface BlockchainService extends IMerchantService {

    PendingPayment createPaymentInvoice(CreditsOperation creditsOperation);

    PendingPayment findByInvoiceId(int invoiceId);

    Optional<String> notCorresponds(Map<String,String> pretended,PendingPayment actual);

    String approveBlockchainTransaction(PendingPayment payment,Map<String,String> params);

    int CONFIRMATIONS = 4;
}
