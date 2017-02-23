package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.InvoiceRequestDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.*;
import me.exrates.model.dto.InvoiceUserDto;
import me.exrates.model.enums.*;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.InvoiceData;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.exception.FileLoadingException;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.IllegalTransactionProvidedStatusException;
import me.exrates.service.exception.invoice.IllegalInvoiceRequestStatusException;
import me.exrates.service.exception.invoice.InvoiceAcceptionException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.exrates.model.enums.InvoiceRequestStatusEnum.*;
import static me.exrates.model.enums.UserActionOnInvoiceEnum.REVOKE;
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

  @Autowired
  private UserFilesService userFilesService;

  @Autowired
  private MessageSource messageSource;

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
    /*id in invoice_request is the id of the corresponding transaction. So source_id equals transaction_id*/
    transactionService.setSourceId(transaction.getId(), transaction.getId());
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
  @Transactional
  public Integer clearExpiredInvoices(Integer intervalMinutes) throws Exception {
    List<Integer> invoiceRequestStatusIdList = InvoiceRequestStatusEnum.getMayExpireStatusList().stream()
        .map(e -> e.getCode())
        .collect(Collectors.toList());
    Optional<LocalDateTime> nowDate = invoiceRequestDao.getAndBlockByIntervalAndStatus(
        intervalMinutes,
        invoiceRequestStatusIdList);
    if (nowDate.isPresent()) {
      invoiceRequestDao.setExpiredByIntervalAndStatus(
          nowDate.get(),
          intervalMinutes,
          EXPIRED.getCode(),
          invoiceRequestStatusIdList);
      List<InvoiceUserDto> userForNotificationList = invoiceRequestDao.findInvoicesListByStatusChangedAtDate(EXPIRED.getCode(), nowDate.get());
      for (InvoiceUserDto invoice : userForNotificationList) {
        notificationService.notifyUser(invoice.getUserId(), NotificationEvent.IN_OUT, "merchants.invoice.expired.title",
            "merchants.invoice.expired.message", new Integer[]{invoice.getInvoiceId()});
      }
      return userForNotificationList.size();
    } else {
      return 0;
    }
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
    public List<ClientBank> findClientBanksForCurrency(Integer currencyId) {
        return invoiceRequestDao.findClientBanksForCurrency(currencyId);
    }

  @Override
  @Transactional(readOnly = true)
  public Optional<InvoiceRequest> findRequestById(Integer transactionId) {
    return invoiceRequestDao.findById(transactionId);
  }

  @Override
  @Transactional(readOnly = true)
  public Integer getInvoiceRequestStatusByInvoiceId(Integer invoiceId) {
    return invoiceRequestDao.getStatusById(invoiceId);
  }

  @Override
  @Transactional
  public Optional<InvoiceRequest> findRequestByIdAndBlock(Integer transactionId) {

    return invoiceRequestDao.findByIdAndBlock(transactionId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<InvoiceRequest> findAllByStatus(List<Integer> invoiceRequestStatusIdList) {
    return invoiceRequestDao.findByStatus(invoiceRequestStatusIdList);
  }

  @Override
  @Transactional
  public List<InvoiceRequest> findAllRequestsForUser(String userEmail) {
    return invoiceRequestDao.findAllForUser(userEmail);
  }

  @Override
  @Transactional
  public void userActionOnInvoice(
          InvoiceConfirmData invoiceConfirmData,
          UserActionOnInvoiceEnum userActionOnInvoiceEnum, Locale locale) throws IllegalInvoiceRequestStatusException, InvoiceNotFoundException {
    log.debug(invoiceConfirmData);
    Optional<InvoiceRequest> invoiceRequestResult = findRequestByIdAndBlock(invoiceConfirmData.getInvoiceId());
    if (!invoiceRequestResult.isPresent()) {
      throw new InvoiceNotFoundException(String.format("invoice id: %s", invoiceConfirmData.getInvoiceId()));
    }
    InvoiceRequest invoiceRequest = invoiceRequestResult.get();
    if (userActionOnInvoiceEnum == REVOKE) {
      if (!InvoiceRequestStatusEnum.revokeable(invoiceRequest)) {
        throw new IllegalInvoiceRequestStatusException(String.format("invoice id: %s status: %s demanded action: %s",
            invoiceRequest.getTransaction().getId(),
            invoiceRequest.getInvoiceRequestStatus(),
            userActionOnInvoiceEnum));
      }
      updateInvoiceRequestStatus(invoiceRequest.getTransaction().getId(), REVOKED_USER);
    } else {
      if (!InvoiceRequestStatusEnum.availableToConfirm(invoiceRequest)) {
        throw new IllegalInvoiceRequestStatusException(String.format("invoice id: %s status: %s demanded action: %s",
            invoiceRequest.getTransaction().getId(),
            invoiceRequest.getInvoiceRequestStatus(),
            userActionOnInvoiceEnum));
      }
      invoiceRequest.setPayerBankName(invoiceConfirmData.getPayerBankName());
      invoiceRequest.setPayerAccount(invoiceConfirmData.getUserAccount());
      invoiceRequest.setUserFullName(invoiceConfirmData.getUserFullName());
      invoiceRequest.setInvoiceRequestStatus(CONFIRMED_USER);
      invoiceRequest.setRemark(StringEscapeUtils.escapeHtml(invoiceConfirmData.getRemark()));
      invoiceRequest.setReceiptScanName(invoiceConfirmData.getReceiptScanName());
      updateConfirmationInfo(invoiceRequest);
      MultipartFile receiptScan = invoiceConfirmData.getReceiptScan();
      boolean emptyFile = receiptScan == null || receiptScan.isEmpty();
      if (StringUtils.isEmpty(invoiceRequest.getReceiptScanPath()) && emptyFile) {
          throw new FileLoadingException(messageSource.getMessage("merchants.invoice.error.fieldsNotField", null,
                  locale));
      }
      if (!emptyFile) {
        if (!userFilesService.checkFileValidity(receiptScan) || receiptScan.getSize() > 1048576L) {
          throw new FileLoadingException(messageSource.getMessage("merchants.errorUploadReceipt", null,
                  locale));
        }
        try {
          userFilesService.saveReceiptScan(invoiceRequest.getUserId(), invoiceRequest.getTransaction().getId(), receiptScan);
        } catch (IOException e) {
          throw new FileLoadingException(messageSource.getMessage("merchants.errorUploadReceipt", null,
                  locale));
        }
      }
    }
  }

  @Transactional
  private void updateConfirmationInfo(InvoiceRequest invoiceRequest) {
    invoiceRequestDao.updateConfirmationInfo(invoiceRequest);
  }

  @Transactional
  private void updateInvoiceRequestStatus(Integer invoiceRequestId, InvoiceRequestStatusEnum invoiceRequestStatus) {
    invoiceRequestDao.updateInvoiceRequestStatus(invoiceRequestId, invoiceRequestStatus);
  }

    @Override
    @Transactional
    public void updateReceiptScan(Integer invoiceId, String receiptScanPath) {
        invoiceRequestDao.updateReceiptScan(invoiceId, receiptScanPath);
    }


}
