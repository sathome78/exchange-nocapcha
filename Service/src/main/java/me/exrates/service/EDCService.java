package me.exrates.service;

import me.exrates.model.CreditsOperation;

import java.io.IOException;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface EDCService {

    String createInvoice(CreditsOperation operation) throws Exception;

    void submitTransactionsForProcessing(String list);

    String extractAccountId(final String account, final int invoiceId) throws IOException;

    void rescanUnusedAccounts();
}
