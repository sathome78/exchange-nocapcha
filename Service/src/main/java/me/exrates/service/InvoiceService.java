package me.exrates.service;

import me.exrates.model.InvoiceBank;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.Transaction;
import me.exrates.model.vo.InvoiceData;

import java.util.List;
import java.util.Optional;


public interface InvoiceService {

  Transaction createPaymentInvoice(InvoiceData invoiceData);

  void acceptInvoiceAndProvideTransaction(int invoiceId, int transactionId, String acceptanceUserEmail) throws Exception;

  void declineInvoice(int invoiceId, int transactionId, String acceptanceUserEmail) throws Exception;

  List<InvoiceRequest> findAllInvoiceRequests();

  List<InvoiceBank> findBanksForCurrency(Integer currencyId);

  InvoiceBank findBankById(Integer bankId);

  Optional<InvoiceRequest> findRequestById(Integer transactionId);

  Optional<InvoiceRequest> findRequestByIdAndBlock(Integer transactionId);

  Optional<InvoiceRequest> findUnconfirmedRequestById(Integer transactionId);

  void updateConfirmationInfo(InvoiceRequest invoiceRequest);

  List<InvoiceRequest> findAllRequestsForUser(String userEmail);
}
