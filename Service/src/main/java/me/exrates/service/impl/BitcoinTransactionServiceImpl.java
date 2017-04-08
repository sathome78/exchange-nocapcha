package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;
import me.exrates.model.dto.onlineTableDto.PendingPaymentStatusDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.IllegalTransactionProvidedStatusException;
import me.exrates.service.exception.invoice.IllegalInvoiceAmountException;
import me.exrates.service.exception.invoice.InvoiceAcceptionException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.exception.invoice.InvoiceUnexpectedHashException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.ACCEPT_AUTO;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.ACCEPT_MANUAL;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.BCH_EXAMINE;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;

/**
 * Created by OLEG on 17.03.2017.
 */
@Log4j2
@Service
public class BitcoinTransactionServiceImpl implements BitcoinTransactionService {
  
  @Autowired
  private PendingPaymentDao paymentDao;
  @Autowired
  private TransactionService transactionService;
  
  @Autowired
  private WalletDao walletDao;
  
  @Autowired
  private CompanyWalletService companyWalletService;
  
  @Autowired
  private NotificationService notificationService;
  
  @Autowired
  private UserService userService;
  
  
  
  @Override
  @Transactional(readOnly = true)
  public boolean existsPendingPaymentWithStatusAndAddress(InvoiceStatus beginStatus, String address) {
    return paymentDao.existsPendingPaymentWithAddressAndStatus(address, Collections.singletonList(beginStatus.getCode()));
  }
  
  
  @Override
  @Transactional
  public PendingPaymentStatusDto markStartConfirmationProcessing(String address, String txHash){
    InvoiceStatus beginStatus = PendingPaymentStatusEnum.getBeginState();
    PendingPaymentStatusDto pendingPayment = paymentDao.setStatusAndHashByAddressAndStatus(
            address,
            beginStatus.getCode(),
            beginStatus.nextState(BCH_EXAMINE).getCode(),
            txHash)
            .orElseThrow(() -> new InvoiceNotFoundException(address));
    Integer invoiceId = pendingPayment.getInvoiceId();
    changeTransactionConfidenceForPendingPayment(invoiceId, 0);
    return pendingPayment;
  }
  
  @Override
  @Transactional
  public void changeTransactionConfidenceForPendingPayment(
          Integer invoiceId,
          int confidenceLevel) {
    transactionService.updateTransactionConfirmation(invoiceId, confidenceLevel);
  }
  
  @Override
  @Transactional
  public void provideBtcTransaction(Integer pendingPaymentId, String hash, BigDecimal factAmount, String acceptanceUserEmail) throws IllegalInvoiceAmountException, IllegalOperationTypeException, IllegalTransactionProvidedStatusException {
    if (factAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalInvoiceAmountException(factAmount.toString());
    }
    PendingPayment pendingPayment = paymentDao.findByIdAndBlock(pendingPaymentId)
            .orElseThrow(() -> new InvoiceNotFoundException(pendingPaymentId.toString()));
    InvoiceActionTypeEnum action = acceptanceUserEmail == null ? ACCEPT_AUTO : ACCEPT_MANUAL;
    if (action == ACCEPT_AUTO && !hash.equals(pendingPayment.getHash())) {
      throw new InvoiceUnexpectedHashException(String.format("hash stored in invoice: %s actual get from BCH: %s", pendingPayment.getHash(), hash));
    }
    InvoiceStatus newStatus = action == ACCEPT_AUTO ? pendingPayment.getPendingPaymentStatus().nextState(action) :
            pendingPayment.getPendingPaymentStatus().nextState(action, true,
                    userService.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(pendingPayment.getUserId(),
                            pendingPayment.getTransaction().getCurrency().getId(), InvoiceOperationDirection.REFILL));
    pendingPayment.setPendingPaymentStatus(newStatus);
    Transaction transaction = pendingPayment.getTransaction();
    if (transaction.getOperationType() != OperationType.INPUT) {
      throw new IllegalOperationTypeException("for transaction id = " + pendingPaymentId);
    }
    if (transaction.isProvided()) {
      throw new IllegalTransactionProvidedStatusException("for transaction id = " + transaction.getId());
    }
    BigDecimal amountByInvoice = BigDecimalProcessing.doAction(transaction.getAmount(), transaction.getCommissionAmount(), ActionType.ADD);
    if (amountByInvoice.compareTo(factAmount) != 0) {
      transaction.setAmount(factAmount);
      transactionService.updateTransactionAmount(transaction);
    }
    WalletOperationData walletOperationData = new WalletOperationData();
    walletOperationData.setOperationType(transaction.getOperationType());
    walletOperationData.setWalletId(transaction.getUserWallet().getId());
    walletOperationData.setAmount(transaction.getAmount());
    walletOperationData.setBalanceType(ACTIVE);
    walletOperationData.setCommission(transaction.getCommission());
    walletOperationData.setCommissionAmount(transaction.getCommissionAmount());
    walletOperationData.setSourceType(transaction.getSourceType());
    walletOperationData.setSourceId(pendingPaymentId);
    walletOperationData.setTransaction(transaction);
    WalletTransferStatus walletTransferStatus = walletDao.walletBalanceChange(walletOperationData);
    if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
      String message = "error while accepting pendingPayment id (invoice_id) = " + pendingPaymentId;
      log.error("\n\t" + message);
      throw new InvoiceAcceptionException(message);
    }
    /**/
    companyWalletService.deposit(transaction.getCompanyWallet(), transaction.getAmount(),
            transaction.getCommissionAmount());
    /**/
    pendingPayment.setAcceptanceUserEmail(acceptanceUserEmail);
    pendingPayment.setPendingPaymentStatus(newStatus);
    pendingPayment.setHash(hash);
    paymentDao.updateAcceptanceStatus(pendingPayment);
    /**/
    notificationService.notifyUser(pendingPayment.getUserId(), NotificationEvent.IN_OUT, "paymentRequest.accepted.title",
            "paymentRequest.accepted.message", new Integer[]{pendingPaymentId});
  }
  
  @Override
  public List<PendingPayment> findUnconfirmedBtcPayments() {
    return paymentDao.findAllUnconfirmedPayments();
  }
  
  
  @Override
  public List<PendingPayment> findUnpaidBtcPayments() {
    return paymentDao.findUnpaidBtcPayments();
  }
  
  @Override
  public void updatePendingPaymentHash(Integer txId, String hash) {
    paymentDao.updateBtcHash(txId, hash);
  }
  
}
