package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.BitcoinTransactionService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.IllegalTransactionProvidedStatusException;
import me.exrates.service.exception.invoice.IllegalInvoiceAmountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by OLEG on 17.03.2017.
 */
@Service
@Log4j2
public class BitcoinTransactionServiceImpl implements BitcoinTransactionService {
  
  @Autowired
  private TransactionService transactionService;
  
  @Autowired
  private UserService userService;
  
  
  
  @Override
  @Transactional(readOnly = true)
  public boolean existsPendingPaymentWithStatusAndAddress(InvoiceStatus beginStatus, String address) {
    /*
    TODO REFILL
    return paymentDao.existsPendingPaymentWithAddressAndStatus(address, Collections.singletonList(beginStatus.getCode()));*/
    return false;
  }
  
  
  @Override
  @Transactional
  public RefillStatusEnum markStartConfirmationProcessing(String address, String txHash, BigDecimal factAmount) throws IllegalInvoiceAmountException {
    /*
    TODO REFILL
    if (factAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalInvoiceAmountException(factAmount.toString());
    }
    InvoiceStatus beginStatus = PendingPaymentStatusEnum.getBeginState();
    PendingPaymentStatusDto pendingPaymentStatusDto = paymentDao.setStatusAndHashByAddressAndStatus(
            address,
            beginStatus.getCode(),
            beginStatus.nextState(BCH_EXAMINE).getCode(),
            txHash)
            .orElseThrow(() -> new InvoiceNotFoundException(address));
    Integer invoiceId = pendingPaymentStatusDto.getInvoiceId();
    PendingPayment pendingPayment = paymentDao.findByInvoiceId(invoiceId).orElseThrow(() -> new InvoiceNotFoundException(address));
    Transaction transaction = pendingPayment.getTransaction();
    updateFactAmountForPendingPayment(transaction, factAmount);
    changeTransactionConfidenceForPendingPayment(invoiceId, 0);
    return pendingPaymentStatusDto;*/
    return null;
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
    /*
    TODO REFILL
    if (factAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalInvoiceAmountException(factAmount.toString());
    }
    PendingPayment pendingPayment = paymentDao.findByIdAndBlock(pendingPaymentId)
            .orElseThrow(() -> new InvoiceNotFoundException(pendingPaymentId.toString()));
    InvoiceActionTypeEnum action = acceptanceUserEmail == null ? ACCEPT_AUTO : ACCEPT_MANUAL;
    if (action == ACCEPT_AUTO && !hash.equals(pendingPayment.getHash())) {
      throw new InvoiceUnexpectedHashException(String.format("hash stored in invoice: %s actual get from BCH: %s", pendingPayment.getHash(), hash));
    }
    InvoiceOperationPermission permission = userService.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(pendingPayment.getUserId(),
        pendingPayment.getTransaction().getCurrency().getId(), InvoiceOperationDirection.REFILL);
    InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue = InvoiceActionTypeEnum.InvoiceActionParamsValue.builder()
        .authorisedUserIsHolder(true)
        .permittedOperation(permission)
        .build();
    InvoiceStatus newStatus = action == ACCEPT_AUTO ? pendingPayment.getPendingPaymentStatus().nextState(action) :
            pendingPayment.getPendingPaymentStatus().nextState(action, paramsValue);
    pendingPayment.setPendingPaymentStatus(newStatus);
    Transaction transaction = pendingPayment.getTransaction();
    if (transaction.getOperationType() != OperationType.INPUT) {
      throw new IllegalOperationTypeException("for transaction id = " + pendingPaymentId);
    }
    if (transaction.isProvided()) {
      throw new IllegalTransactionProvidedStatusException("for transaction id = " + transaction.getId());
    }
    updateFactAmountForPendingPayment(transaction, factAmount);
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
    *//**//*
    companyWalletService.deposit(transaction.getCompanyWallet(), transaction.getAmount(),
            transaction.getCommissionAmount());
    *//**//*
    pendingPayment.setAcceptanceUserEmail(acceptanceUserEmail);
    pendingPayment.setPendingPaymentStatus(newStatus);
    pendingPayment.setHash(hash);
    paymentDao.updateAcceptanceStatus(pendingPayment);
    *//**//*
    notificationService.notifyUser(pendingPayment.getUserId(), NotificationEvent.IN_OUT, "paymentRequest.accepted.title",
            "paymentRequest.accepted.message", new Integer[]{pendingPaymentId});*/
  }
  
  private void updateFactAmountForPendingPayment(Transaction transaction, BigDecimal factAmount) {
    BigDecimal amountByInvoice = BigDecimalProcessing.doAction(transaction.getAmount(), transaction.getCommissionAmount(), ActionType.ADD);
    if (amountByInvoice.compareTo(factAmount) != 0) {
      transaction.setAmount(factAmount);
      transactionService.updateTransactionAmount(transaction);
    }
  }
  
  @Override
  public List<PendingPayment> findUnconfirmedBtcPayments() {
    /*
    TODO REFILL
    return paymentDao.findAllUnconfirmedPayments();*/
    return null;
  }
  
  
  @Override
  public List<PendingPayment> findUnpaidBtcPayments() {
    /*
    TODO REFILL
    return paymentDao.findUnpaidBtcPayments();*/
    return null;
  }
  
  @Override
  public void updatePendingPaymentHash(Integer txId, String hash) {

    /*
    TODO REFILL
    paymentDao.updateBtcHash(txId, hash);*/
  }
  
}
