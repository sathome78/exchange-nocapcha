package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.InvoiceRequestDao;
import me.exrates.dao.WalletDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.InvoiceBank;
import me.exrates.model.InvoiceRequest;
import me.exrates.model.Transaction;
import me.exrates.model.dto.InvoiceRequestFlatForReportDto;
import me.exrates.model.dto.InvoiceUserDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.exceptions.UnsupportedUserInvoiceActionTypeException;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.InvoiceData;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.exception.FileLoadingException;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.IllegalTransactionProvidedStatusException;
import me.exrates.service.exception.NotApplicableException;
import me.exrates.service.exception.invoice.IllegalInvoiceStatusException;
import me.exrates.service.exception.invoice.InvoiceAcceptionException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.model.enums.UserCommentTopicEnum.INVOICE_DECLINE;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.*;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;
import static me.exrates.model.enums.invoice.InvoiceRequestStatusEnum.DECLINED_ADMIN;
import static me.exrates.model.enums.invoice.InvoiceRequestStatusEnum.EXPIRED;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;


@Service
@Log4j2
@PropertySource(value = {"classpath:/job.properties"})
public class InvoiceServiceImpl implements InvoiceService {

  @Value("${invoice.blockNotifyUsers}")
  private Boolean BLOCK_NOTIFYING;

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

  @Autowired
  private UserService userService;

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
    invoiceRequest.setInvoiceRequestStatus(InvoiceRequestStatusEnum.getBeginState());
    invoiceRequestDao.create(invoiceRequest, creditsOperation.getUser());
    /*id (transaction_id) in invoice_request is the id of the corresponding transaction. So source_id equals transaction_id*/
    transactionService.setSourceId(transaction.getId(), transaction.getId());
    return transaction;
  }

  @Override
  @Transactional
  public void acceptInvoiceAndProvideTransaction(Integer invoiceId, String acceptanceUserEmail, BigDecimal actualPaymentSum) throws Exception {
    updateAmountIfPositive(invoiceId, actualPaymentSum);
    InvoiceRequest invoiceRequest = invoiceRequestDao.findByIdAndBlock(invoiceId)
        .orElseThrow(() -> new InvoiceNotFoundException(invoiceId.toString()));
    Integer requesterAdminId = userService.getIdByEmail(acceptanceUserEmail);
    InvoiceOperationPermission permission = userService.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(
        requesterAdminId,
        invoiceRequest.getTransaction().getCurrency().getId(),
        WITHDRAW
    );
    InvoiceStatus newStatus = invoiceRequest.getInvoiceRequestStatus().nextState(ACCEPT_MANUAL, true, permission);
    invoiceRequest.setInvoiceRequestStatus(newStatus);
    Transaction transaction = invoiceRequest.getTransaction();
    if (transaction.getOperationType() != OperationType.INPUT) {
      throw new IllegalOperationTypeException("for transaction id = " + transaction.getId());
    }
    if (transaction.isProvided()) {
      throw new IllegalTransactionProvidedStatusException("for transaction id = " + transaction.getId());
    }
    WalletOperationData walletOperationData = new WalletOperationData();
    walletOperationData.setOperationType(transaction.getOperationType());
    walletOperationData.setWalletId(transaction.getUserWallet().getId());
    walletOperationData.setAmount(transaction.getAmount());
    walletOperationData.setBalanceType(ACTIVE);
    walletOperationData.setCommission(transaction.getCommission());
    walletOperationData.setCommissionAmount(transaction.getCommissionAmount());
    walletOperationData.setSourceType(transaction.getSourceType());
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
    invoiceRequest.setInvoiceRequestStatus(newStatus);
    invoiceRequestDao.updateAcceptanceStatus(invoiceRequest);
    /**/
    notificationService.notifyUser(invoiceRequest.getUserId(), NotificationEvent.IN_OUT, "merchants.invoice.accepted.title",
        "merchants.invoice.accepted.message", new Integer[]{invoiceId});
  }

  private void updateAmountIfPositive(int invoiceId, BigDecimal actualPaymentSum) {
    if (actualPaymentSum != null && actualPaymentSum.signum() > 0) {
      Transaction transaction = transactionService.findById(invoiceId);
      transactionService.updateTransactionAmount(transaction, actualPaymentSum);
    }
  }

  @Override
  @Transactional
  public void declineInvoice(Integer invoiceId, Integer transactionId, String acceptanceUserEmail, String comment) throws Exception {
    InvoiceRequest invoiceRequest = invoiceRequestDao.findByIdAndBlock(transactionId)
        .orElseThrow(() -> new InvoiceNotFoundException(transactionId.toString()));
    Integer requesterAdminId = userService.getIdByEmail(acceptanceUserEmail);
    InvoiceOperationPermission permission = userService.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(
        requesterAdminId,
        invoiceRequest.getTransaction().getCurrency().getId(),
        WITHDRAW
    );
    InvoiceStatus newStatus = invoiceRequest.getInvoiceRequestStatus().nextState(DECLINE, true, permission);
    invoiceRequest.setInvoiceRequestStatus(newStatus);
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
    Locale locale = new Locale(userService.getPreferedLang(invoiceRequest.getUserId()));
    String title = messageSource.getMessage("merchants.invoice.declined.title", new Integer[]{invoiceId}, locale);
    if (StringUtils.isEmpty(comment)) {
      comment = messageSource.getMessage("merchants.invoice.declined.message", new Integer[]{invoiceId}, locale);
    }
    userService.addUserComment(INVOICE_DECLINE, comment, invoiceRequest.getUserEmail(), false);
    notificationService.notifyUser(invoiceRequest.getUserId(), NotificationEvent.IN_OUT, title, comment);
  }

  @Override
  @Transactional
  public Integer clearExpiredInvoices(Integer intervalMinutes) throws Exception {
    List<Integer> invoiceRequestStatusIdList = InvoiceRequestStatusEnum.getAvailableForActionStatusesList(EXPIRE).stream()
        .map(InvoiceStatus::getCode)
        .collect(Collectors.toList());
    Optional<LocalDateTime> nowDate = invoiceRequestDao.getAndBlockByIntervalAndStatus(
        intervalMinutes,
        invoiceRequestStatusIdList);
    if (nowDate.isPresent()) {
      invoiceRequestDao.setNewStatusByDateIntervalAndStatus(
          nowDate.get(),
          intervalMinutes,
          EXPIRED.getCode(),
          invoiceRequestStatusIdList);
      List<InvoiceUserDto> userForNotificationList = invoiceRequestDao.findInvoicesListByStatusChangedAtDate(EXPIRED.getCode(), nowDate.get());
      if (!BLOCK_NOTIFYING) {
        for (InvoiceUserDto invoice : userForNotificationList) {
          notificationService.notifyUser(invoice.getUserId(), NotificationEvent.IN_OUT, "merchants.invoice.expired.title",
              "merchants.invoice.expired.message", new Integer[]{invoice.getInvoiceId()});
        }
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
  public InvoiceBank findBankById(Integer bankId) {
    return invoiceRequestDao.findBankById(bankId);
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
      InvoiceActionTypeEnum userActionOnInvoiceEnum, Locale locale) throws IllegalInvoiceStatusException, InvoiceNotFoundException {
    log.debug(invoiceConfirmData);
    Optional<InvoiceRequest> invoiceRequestResult = findRequestByIdAndBlock(invoiceConfirmData.getInvoiceId());
    if (!invoiceRequestResult.isPresent()) {
      throw new InvoiceNotFoundException(String.format("invoice id: %s", invoiceConfirmData.getInvoiceId()));
    }
    InvoiceRequest invoiceRequest = invoiceRequestResult.get();
    InvoiceStatus newStatus = invoiceRequest.getInvoiceRequestStatus().nextState(userActionOnInvoiceEnum);
    invoiceRequest.setInvoiceRequestStatus(newStatus);
    if (userActionOnInvoiceEnum == REVOKE) {
      updateInvoiceRequestStatus(invoiceRequest.getTransaction().getId(), newStatus);
    } else if (userActionOnInvoiceEnum == CONFIRM_USER) {
      invoiceRequest.setPayerBankName(invoiceConfirmData.getPayerBankName());
      invoiceRequest.setPayerAccount(invoiceConfirmData.getUserAccount());
      invoiceRequest.setUserFullName(invoiceConfirmData.getUserFullName());
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
    } else {
      throw new UnsupportedUserInvoiceActionTypeException(userActionOnInvoiceEnum.name());
    }
  }

  @Transactional
  private void updateConfirmationInfo(InvoiceRequest invoiceRequest) {
    invoiceRequestDao.updateConfirmationInfo(invoiceRequest);
  }

  @Transactional
  private void updateInvoiceRequestStatus(Integer invoiceRequestId, InvoiceStatus invoiceRequestStatus) {
    invoiceRequestDao.updateInvoiceRequestStatus(invoiceRequestId, (InvoiceRequestStatusEnum) invoiceRequestStatus);
  }

  @Override
  @Transactional
  public void updateReceiptScan(Integer invoiceId, String receiptScanPath) {
    invoiceRequestDao.updateReceiptScan(invoiceId, receiptScanPath);
  }

  @Override
  @Transactional(readOnly = true)
  public List<InvoiceRequestFlatForReportDto> getByDateIntervalAndRoleAndCurrency(String startDate, String endDate, List<Integer> roleIdList, List<Integer> currencyList) {
    return invoiceRequestDao.findAllByDateIntervalAndRoleAndCurrency(startDate, endDate, roleIdList, currencyList);
  }

  @Override
  @Transactional(readOnly = true)
  public List<InvoiceRequest> findAllInvoiceRequestsByCurrencyPermittedForUser(Integer requesterUserId) {
    return invoiceRequestDao.findByCurrencyPermittedForUser(requesterUserId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<InvoiceRequest> findAllByStatusAndByCurrencyPermittedForUser(
      List<Integer> invoiceRequestStatusIdList,
      Integer requesterUserId) {
    return invoiceRequestDao.findByStatusAndByCurrencyPermittedForUser(invoiceRequestStatusIdList, requesterUserId);
  }

  @Override
  @Transactional
  public void withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
    throw new NotApplicableException("for " + withdrawMerchantOperationDto);
  }

  @Override
  @Transactional
  public Map<String, String> refill(RefillRequestCreateDto request) {
    return new HashMap<String, String>() {{
      put("message", "");
    }};
  }
}
