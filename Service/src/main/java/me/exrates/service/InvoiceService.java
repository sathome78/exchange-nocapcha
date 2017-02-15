package me.exrates.service;

import me.exrates.model.InvoiceBank;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.Transaction;
import me.exrates.model.enums.InvoiceRequestStatusEnum;
import me.exrates.model.enums.UserActionOnInvoiceEnum;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.InvoiceData;
import me.exrates.service.exception.invoice.IllegalInvoiceRequestStatusException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public interface InvoiceService {

  Transaction createPaymentInvoice(InvoiceData invoiceData);

  void acceptInvoiceAndProvideTransaction(int invoiceId, int transactionId, String acceptanceUserEmail) throws Exception;

  void declineInvoice(int invoiceId, int transactionId, String acceptanceUserEmail) throws Exception;

  Integer clearExpiredInvoices(Integer intervalHours) throws Exception;

  List<InvoiceRequest> findAllInvoiceRequests();

  List<InvoiceBank> findBanksForCurrency(Integer currencyId);

  InvoiceBank findBankById(Integer bankId);

  Optional<InvoiceRequest> findRequestById(Integer transactionId);

  Integer getInvoiceRequestStatusByInvoiceId(Integer invoiceId);

  Optional<InvoiceRequest> findRequestByIdAndBlock(Integer transactionId);

  List<InvoiceRequest> findAllByStatus(List<Integer> invoiceRequestStatusIdList);

  List<InvoiceRequest> findAllRequestsForUser(String userEmail);

  void userActionOnInvoice(
      InvoiceConfirmData invoiceConfirmData,
      UserActionOnInvoiceEnum userActionOnInvoiceEnum) throws IllegalInvoiceRequestStatusException, InvoiceNotFoundException;
}
