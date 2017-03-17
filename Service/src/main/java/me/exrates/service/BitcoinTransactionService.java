package me.exrates.service;

import me.exrates.model.dto.onlineTableDto.PendingPaymentStatusDto;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.IllegalTransactionProvidedStatusException;
import me.exrates.service.exception.invoice.IllegalInvoiceAmountException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Created by OLEG on 17.03.2017.
 */
public interface BitcoinTransactionService {
  @Transactional(readOnly = true)
  boolean existsPendingPaymentWithStatusAndAddress(InvoiceStatus beginStatus, String address);
  
  @Transactional
  PendingPaymentStatusDto markStartConfirmationProcessing(String address, String txHash);
  
  @Transactional
  void changeTransactionConfidenceForPendingPayment(
          Integer invoiceId,
          int confidenceLevel);
  
  @Transactional
  void provideBtcTransaction(Integer pendingPaymentId, String hash, BigDecimal factAmount, String acceptanceUserEmail) throws IllegalInvoiceAmountException, IllegalOperationTypeException, IllegalTransactionProvidedStatusException;
}
