package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import me.exrates.service.merchantStrategy.IMerchantService;

import java.io.IOException;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface EDCServiceNode {

  String createInvoice(CreditsOperation operation) throws Exception;

  void submitTransactionsForProcessing(String list);

  String extractAccountId(final String account, final int invoiceId) throws IOException;

  void rescanUnusedAccounts();

  void transferToMainAccount(String accountId, Transaction tx) throws IOException, InterruptedException;

  void transferFromMainAccount(String accountName, String amount) throws IOException, InterruptedException;
}
