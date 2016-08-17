package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface EDCService {

    String createInvoice(CreditsOperation operation) throws Exception;

    void submitTransactionsForProcessing(String list);
}
