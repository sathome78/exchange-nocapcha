package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.InvoiceRequestDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.InvoiceBank;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.Transaction;
import me.exrates.model.enums.*;
import me.exrates.model.vo.InvoiceData;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.InvoiceService;
import me.exrates.service.NotificationService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.IllegalInvoiceRequestStatusException;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.IllegalTransactionProvidedStatusException;
import me.exrates.service.exception.InvoiceAcceptionException;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static me.exrates.model.enums.InvoiceRequestStatusEnum.*;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;


@Service
@Log4j2
public class InvoiceServiceImpl implements InvoiceService {

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private InvoiceRequestDao invoiceRequestDao;

  @Autowired
  private WalletDao walletDao;

  @Autowired
  private CompanyWalletService companyWalletService;

  @Override
  @Transactional
  public Transaction createPaymentInvoice(final InvoiceData invoiceData) {
    CreditsOperation creditsOperation = invoiceData.getCreditsOperation();
    final Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
    InvoiceRequest invoiceRequest = new InvoiceRequest();
    invoiceRequest.setTransaction(transaction);
    invoiceRequest.setUserEmail(creditsOperation.getUser().getEmail());
    InvoiceBank invoiceBank = new InvoiceBank();
    invoiceBank.setId(invoiceData.getBankId());
    invoiceRequest.setInvoiceBank(invoiceBank);
    invoiceRequest.setUserFullName(invoiceData.getUserFullName());
    invoiceRequest.setRemark(StringEscapeUtils.escapeHtml(invoiceData.getRemark()));
    invoiceRequest.setInvoiceRequestStatus(CREATED_USER);
    invoiceRequestDao.create(invoiceRequest, creditsOperation.getUser());
    return transaction;
  }

  @Override
  @Transactional
  public void acceptInvoiceAndProvideTransaction(int invoiceId, int transactionId, String acceptanceUserEmail) throws Exception {
    InvoiceRequest invoiceRequest = invoiceRequestDao.findByIdAndBlock(transactionId).get();
    if (invoiceRequest.getInvoiceRequestStatus() != InvoiceRequestStatusEnum.CONFIRMED_USER) {
      throw new IllegalInvoiceRequestStatusException("for transaction id = " + transactionId);
    }
    Transaction transaction = invoiceRequest.getTransaction();
    if (transaction.getOperationType() != OperationType.INPUT) {
      throw new IllegalOperationTypeException("for transaction id = " + transactionId);
    }
    if (transaction.isProvided()) {
      throw new IllegalTransactionProvidedStatusException("for transaction id = " + transactionId);
    }
    WalletOperationData walletOperationData = new WalletOperationData();
    walletOperationData.setOperationType(transaction.getOperationType());
    walletOperationData.setWalletId(transaction.getUserWallet().getId());
    walletOperationData.setAmount(transaction.getAmount());
    walletOperationData.setBalanceType(ACTIVE);
    walletOperationData.setCommission(transaction.getCommission());
    walletOperationData.setCommissionAmount(transaction.getCommissionAmount());
    walletOperationData.setSourceType(TransactionSourceType.INVOICE);
    walletOperationData.setSourceId(invoiceId);
    walletOperationData.setTransaction(transaction);
    WalletTransferStatus walletTransferStatus = walletDao.walletBalanceChange(walletOperationData);
    if (walletTransferStatus != WalletTransferStatus.SUCCESS) {
      String message = "error while accepting invoice id (transaction_id) = " + invoiceId;
      log.error("\n\t" + message);
      throw new InvoiceAcceptionException(message);
    }
    /**/
    companyWalletService.deposit(transaction.getCompanyWallet(), transaction.getAmount(),
        transaction.getCommissionAmount());
    /**/
    invoiceRequest.setAcceptanceUserEmail(acceptanceUserEmail);
    invoiceRequest.setInvoiceRequestStatus(ACCEPTED_ADMIN);
    invoiceRequestDao.updateAcceptanceStatus(invoiceRequest);
    /**/
    notificationService.notifyUser(invoiceRequest.getUserId(), NotificationEvent.IN_OUT, "merchants.invoice.accepted.title",
        "merchants.invoice.accepted.message", new Integer[]{invoiceId});
  }

  @Override
  @Transactional
  public void declineInvoice(int invoiceId, int transactionId, String acceptanceUserEmail) throws Exception {
    InvoiceRequest invoiceRequest = invoiceRequestDao.findByIdAndBlock(transactionId).get();
    if (invoiceRequest.getInvoiceRequestStatus() != InvoiceRequestStatusEnum.CONFIRMED_USER) {
      throw new IllegalInvoiceRequestStatusException("for transaction id = " + transactionId);
    }
    Transaction transaction = invoiceRequest.getTransaction();
    if (transaction.getOperationType() != OperationType.INPUT) {
      throw new IllegalOperationTypeException("for transaction id = " + transactionId);
    }
    if (transaction.isProvided()) {
      throw new IllegalTransactionProvidedStatusException("for transaction id = " + transactionId);
    }
    /**/
    invoiceRequest.setAcceptanceUserEmail(acceptanceUserEmail);
    invoiceRequest.setInvoiceRequestStatus(DECLINED_ADMIN);
    invoiceRequestDao.updateAcceptanceStatus(invoiceRequest);
    /**/
    notificationService.notifyUser(invoiceRequest.getUserId(), NotificationEvent.IN_OUT, "merchants.invoice.declined.title",
        "merchants.invoice.declined.message", new Integer[]{invoiceId});
  }

  @Override
  @Transactional(readOnly = true)
  public List<InvoiceRequest> findAllInvoiceRequests() {
    return invoiceRequestDao.findAll();
  }

  @Override
  @Transactional(readOnly = true)
  public List<InvoiceBank> findBanksForCurrency(Integer currencyId) {
    return invoiceRequestDao.findInvoiceBanksByCurrency(currencyId);
  }

  @Override
  @Transactional(readOnly = true)
  public InvoiceBank findBankById(Integer bankId) {
    return invoiceRequestDao.findBankById(bankId);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<InvoiceRequest> findRequestById(Integer transactionId) {
    return invoiceRequestDao.findById(transactionId);
  }

  @Override
  @Transactional
  public Optional<InvoiceRequest> findRequestByIdAndBlock(Integer transactionId) {
    return invoiceRequestDao.findByIdAndBlock(transactionId);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<InvoiceRequest> findUnconfirmedRequestById(Integer transactionId) {
    return invoiceRequestDao.findByIdAndNotConfirmed(transactionId);
  }

  @Override
  @Transactional
  public void updateConfirmationInfo(InvoiceRequest invoiceRequest) {
    invoiceRequestDao.updateConfirmationInfo(invoiceRequest);
  }

  @Override
  @Transactional
  public void updateInvoiceRequestStatus(Integer invoiceRequestId, InvoiceRequestStatusEnum invoiceRequestStatus) {
    invoiceRequestDao.updateInvoiceRequestStatus(invoiceRequestId, invoiceRequestStatus);
  }

  @Override
  public List<InvoiceRequest> findAllRequestsForUser(String userEmail) {
    return invoiceRequestDao.findAllForUser(userEmail);
  }
}
