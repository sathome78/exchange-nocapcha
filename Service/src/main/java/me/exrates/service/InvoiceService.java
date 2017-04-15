package me.exrates.service;

import me.exrates.model.ClientBank;
import me.exrates.model.InvoiceBank;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.Transaction;
import me.exrates.model.dto.InvoiceRequestFlatForReportDto;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.InvoiceData;
import me.exrates.service.exception.invoice.IllegalInvoiceStatusException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.merchantStrategy.IMerchantService;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


public interface InvoiceService extends IMerchantService {

  Transaction createPaymentInvoice(InvoiceData invoiceData);

  void acceptInvoiceAndProvideTransaction(Integer invoiceId, String acceptanceUserEmail, BigDecimal actualPaymentSum) throws Exception;

  void declineInvoice(Integer invoiceId, Integer transactionId, String acceptanceUserEmail, String comment) throws Exception;

  List<InvoiceRequest> findAllInvoiceRequests();

  InvoiceBank findBankById(Integer bankId);

  Optional<InvoiceRequest> findRequestById(Integer transactionId);

  Integer getInvoiceRequestStatusByInvoiceId(Integer invoiceId);

  Optional<InvoiceRequest> findRequestByIdAndBlock(Integer transactionId);

  List<InvoiceRequest> findAllByStatus(List<Integer> invoiceRequestStatusIdList);

  List<InvoiceRequest> findAllRequestsForUser(String userEmail);

  void userActionOnInvoice(
      InvoiceConfirmData invoiceConfirmData,
      InvoiceActionTypeEnum userActionOnInvoiceEnum, Locale locale) throws IllegalInvoiceStatusException, InvoiceNotFoundException;

  void updateReceiptScan(Integer invoiceId, String receiptScanPath);

  List<InvoiceRequestFlatForReportDto> getByDateIntervalAndRoleAndCurrency(String startDate, String endDate, List<Integer> roleIdList, List<Integer> currencyList);

  List<InvoiceRequest> findAllInvoiceRequestsByCurrencyPermittedForUser(Integer requesterUserId);

  List<InvoiceRequest> findAllByStatusAndByCurrencyPermittedForUser(List<Integer> invoiceRequestStatusIdList, Integer requesterUserId);
}
