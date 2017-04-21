package me.exrates.service.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.dao.RefillRequestDao;
import me.exrates.model.InvoiceBank;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.*;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.service.*;
import me.exrates.service.exception.FileLoadingException;
import me.exrates.service.exception.RefillRequestLimitForMerchantExceededException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.vo.ProfileData;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.*;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;
import static me.exrates.model.enums.invoice.InvoiceRequestStatusEnum.EXPIRED;

/**
 * created by ValkSam
 */

@Service
@PropertySource(value = {"classpath:/job.properties"})
public class RefillServiceImpl implements RefillService {

  @Value("${invoice.blockNotifyUsers}")
  private Boolean BLOCK_NOTIFYING;

  private static final Logger log = LogManager.getLogger("refill");

  @Autowired
  private MerchantDao merchantDao;

  @Autowired
  private CurrencyService currencyService;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private RefillRequestDao refillRequestDao;

  @Autowired
  private MerchantService merchantService;

  @Autowired
  private CompanyWalletService companyWalletService;

  @Autowired
  private WalletService walletService;

  @Autowired
  private UserService userService;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  TransactionDescription transactionDescription;

  @Autowired
  MerchantServiceContext merchantServiceContext;

  @Autowired
  private CommissionService commissionService;

  @Autowired
  private UserFilesService userFilesService;

  @Override
  @Transactional
  public Map<String, String> createRefillRequest(
      RefillRequestCreateDto request,
      Locale locale) {
    ProfileData profileData = new ProfileData(1000);
    Map<String, String> result = null;
    try {
      checkIfOperationLimitExceededForMerchantByUser(request);
      Integer requestId = createRefill(request);
      profileData.setTime1();
      request.setId(requestId);
      IMerchantService merchantService = merchantServiceContext.getMerchantService(request.getServiceBeanName());
      profileData.setTime2();
      result = merchantService.refill(request);
      profileData.setTime3();
    } finally {
      profileData.checkAndLog("slow create RefillRequest: " + request + " profile: " + profileData);
    }
    try {
      String notification = sendRefillNotificationAfterCreation(
          request,
          result.get("message"),
          locale);
      result.put("message", notification);
    } catch (MailException e) {
      log.error(e);
    }
    return result;
  }

  @Override
  @Transactional
  public void confirmRefillRequest(InvoiceConfirmData invoiceConfirmData, Locale locale) {
    Integer requestId = invoiceConfirmData.getInvoiceId();
    RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
    RefillStatusEnum currentStatus = refillRequest.getStatus();
    InvoiceActionTypeEnum action = CONFIRM_USER;
    RefillStatusEnum newStatus = (RefillStatusEnum) currentStatus.nextState(action);
    /**/
    MultipartFile receiptScan = invoiceConfirmData.getReceiptScan();
    boolean emptyFile = receiptScan == null || receiptScan.isEmpty();
    if (emptyFile) {
      throw new FileLoadingException(messageSource.getMessage("refill.receiptScan.absent", null, locale));
    }
    if (!userFilesService.checkFileValidity(receiptScan) || receiptScan.getSize() > 1048576L) {
      throw new FileLoadingException(messageSource.getMessage("merchants.errorUploadReceipt", null, locale));
    }
    try {
      String scanPath = userFilesService.saveReceiptScan(refillRequest.getUserId(), refillRequest.getId(), receiptScan);
      invoiceConfirmData.setReceiptScanPath(scanPath);
    } catch (IOException e) {
      throw new FileLoadingException(messageSource.getMessage("merchants.errorUploadReceipt", null, locale));
    }
    refillRequestDao.setStatusAndConfirmationDataById(requestId, newStatus, invoiceConfirmData);
  }

  @Override
  @Transactional(readOnly = true)
  public RefillRequestFlatDto getFlatById(Integer id) {
    return refillRequestDao.getFlatById(id)
        .orElseThrow(() -> new InvoiceNotFoundException(id.toString()));
  }

  @Override
  @Transactional
  public void revokeRefillRequest(int requestId) {
    RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
    RefillStatusEnum currentStatus = refillRequest.getStatus();
    InvoiceActionTypeEnum action = REVOKE;
    RefillStatusEnum newStatus = (RefillStatusEnum) currentStatus.nextState(action);
    refillRequestDao.setStatusById(requestId, newStatus);
  }

  @Override
  @Transactional(readOnly = true)
  public List<InvoiceBank> findBanksForCurrency(Integer currencyId) {
    return refillRequestDao.findInvoiceBanksByCurrency(currencyId);
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, String> correctAmountAndCalculateCommission(BigDecimal amount, String currency, String merchant) {
    OperationType operationType = INPUT;
    BigDecimal addition = currencyService.computeRandomizedAddition(currency, operationType);
    amount = amount.add(addition);
    Map<String, String> result = commissionService.computeCommissionAndMapAllToString(amount, operationType, currency, merchant);
    result.put("addition", addition.toString());
    return result;
  }

  @Override
  @Transactional
  public Integer clearExpiredInvoices() throws Exception {
    List<Integer> invoiceRequestStatusIdList = InvoiceRequestStatusEnum.getAvailableForActionStatusesList(EXPIRE).stream()
        .map(InvoiceStatus::getCode)
        .collect(Collectors.toList());
    List<OperationUserDto> userForNotificationList = new ArrayList<>();
    List<MerchantCurrencyLifetimeDto> merchantCurrencyList = merchantService.getMerchantCurrencyWithRefillLifetime();
    for (MerchantCurrencyLifetimeDto merchantCurrency : merchantCurrencyList) {
      Integer intervalHours = merchantCurrency.getRefillLifetimeHours();
      Integer merchantId = merchantCurrency.getMerchantId();
      Integer currencyId = merchantCurrency.getCurrencyId();
      Optional<LocalDateTime> nowDate = refillRequestDao.getAndBlockByIntervalAndStatus(
          merchantId,
          currencyId,
          intervalHours,
          invoiceRequestStatusIdList);
      if (nowDate.isPresent()) {
        refillRequestDao.setNewStatusByDateIntervalAndStatus(
            merchantId,
            currencyId,
            nowDate.get(),
            intervalHours,
            EXPIRED.getCode(),
            invoiceRequestStatusIdList);
        userForNotificationList.addAll(refillRequestDao.findInvoicesListByStatusChangedAtDate(
            merchantId,
            currencyId,
            EXPIRED.getCode(),
            nowDate.get()));
      }
    }
    if (!BLOCK_NOTIFYING) {
      for (OperationUserDto invoice : userForNotificationList) {
        notificationService.notifyUser(invoice.getUserId(), NotificationEvent.IN_OUT, "merchants.refillNotification.header",
            "merchants.refillNotification." + EXPIRED, new Integer[]{invoice.getId()});
      }
    }
    return userForNotificationList.size();
  }


  private void checkIfOperationLimitExceededForMerchantByUser(RefillRequestCreateDto request) {
    Integer merchantId = request.getMerchantId();
    Integer userId = request.getUserId();
    Integer operationLimit = request.getRefillOperationCountLimitForUserPerDay();
    Integer operationsAtTheMoment = refillRequestDao.findActiveRequestsByMerchantIdAndUserIdForCurrentDate(merchantId, userId);
    if (operationsAtTheMoment > operationLimit) {
      throw new RefillRequestLimitForMerchantExceededException(String.format("Merchant: %s user: %s operations at the moment: %s, limit: %s",
          merchantId,
          request.getUserEmail(),
          operationsAtTheMoment,
          operationLimit
      ));
    }
  }

  private Integer createRefill(RefillRequestCreateDto request) {
    RefillStatusEnum currentStatus = request.getStatus();
    Merchant merchant = merchantDao.findById(request.getMerchantId());
    InvoiceActionTypeEnum action = currentStatus.getStartAction(merchant);
    RefillStatusEnum newStatus = (RefillStatusEnum) currentStatus.nextState(action);
    request.setStatus(newStatus);
    return refillRequestDao.create(request);
  }

  private String sendRefillNotificationAfterCreation(
      RefillRequestCreateDto request,
      String addMessage,
      Locale locale) {
    String title = messageSource.getMessage("merchants.refillNotification.header", null, locale);
    Integer lifetime = merchantService.getMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(request.getMerchantId(), request.getCurrencyId()).getRefillLifetimeHours();
    String mainNotificationMessageCodeSuffix = lifetime == 0 ? "" : ".lifetime";
    String mainNotificationMessageCode = "merchants.refillNotification.".concat(request.getStatus().name()).concat(mainNotificationMessageCodeSuffix);
    Object[] messageParams = {
        request.getId(),
        request.getMerchantDescription(),
        lifetime
    };
    String mainNotification = messageSource.getMessage(mainNotificationMessageCode, messageParams, locale);
    String fullNotification = StringUtils.isEmpty(addMessage) ? mainNotification : mainNotification.concat("<br>").concat("<br>").concat(addMessage);
    notificationService.notifyUser(request.getUserId(), NotificationEvent.IN_OUT, title, fullNotification);
    return fullNotification;
  }

  private WithdrawStatusEnum checkPermissionOnActionAndGetNewStatus(Integer requesterAdminId, WithdrawRequestFlatDto withdrawRequest, InvoiceActionTypeEnum action) {
    Boolean requesterAdminIsHolder = requesterAdminId.equals(withdrawRequest.getAdminHolderId());
    InvoiceOperationPermission permission = userService.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(
        requesterAdminId,
        withdrawRequest.getCurrencyId(),
        WITHDRAW
    );
    InvoiceActionTypeEnum.InvoiceActionParamsValue paramsValue = InvoiceActionTypeEnum.InvoiceActionParamsValue.builder()
        .authorisedUserIsHolder(requesterAdminIsHolder)
        .permittedOperation(permission)
        .build();
    return (WithdrawStatusEnum) withdrawRequest.getStatus().nextState(action, paramsValue);
  }


}
