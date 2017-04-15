package me.exrates.service.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.dao.RefillRequestDao;
import me.exrates.model.InvoiceBank;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawRequestFlatDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.service.*;
import me.exrates.service.exception.RefillRequestLimitForMerchantExceededException;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.vo.ProfileData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.math.BigDecimal.ZERO;
import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;

/**
 * created by ValkSam
 */

@Service
public class RefillServiceImpl implements RefillService {

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
  private WalletService walletService;

  @Autowired
  private CompanyWalletService companyWalletService;

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

  @Override
  @Transactional
  public Map<String, String> createRefillRequest(
      RefillRequestCreateDto request,
      Locale locale) {
    ProfileData profileData = new ProfileData(1000);
    try {
      checkIfOperationLimitExceededForMerchantByUser(request);
      Integer requestId = createRefill(request);
      profileData.setTime1();
      request.setId(requestId);
      IMerchantService merchantService = merchantServiceContext.getMerchantService(request.getServiceBeanName());
      profileData.setTime2();
      Map<String, String> result = merchantService.refill(request);
      profileData.setTime3();
      if (result.get("message") != null) {
        String notification = null;
        try {
          notification = sendRefillNotification(
              request,
              request.getMerchantDescription(),
              locale);
          result.put("message", notification);
        } catch (MailException e) {
          log.error(e);
        }
      }
      profileData.setTime4();
      return result;
    } finally {
      profileData.checkAndLog("slow create RefillRequest: " + request + " profile: " + profileData);
    }
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

  private String sendRefillNotification(
      RefillRequestCreateDto withdrawRequest,
      String merchantDescription,
      Locale locale) {
    final String notification;
    final Object[] messageParams = {
        withdrawRequest.getId(),
        merchantDescription
    };
    String notificationMessageCode;
    notificationMessageCode = "merchants.refillNotification.".concat(withdrawRequest.getStatus().name());
    notification = messageSource
        .getMessage(notificationMessageCode, messageParams, locale);
    notificationService.notifyUser(withdrawRequest.getUserEmail(), NotificationEvent.IN_OUT,
        "merchants.refillNotification.header", notificationMessageCode, messageParams);
    return notification;
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


  private WithdrawStatusEnum checkPermissionOnActionAndGetNewStatus(Integer requesterAdminId, WithdrawRequestFlatDto withdrawRequest, InvoiceActionTypeEnum action) {
    Boolean requesterAdminIsHolder = requesterAdminId.equals(withdrawRequest.getAdminHolderId());
    InvoiceOperationPermission permission = userService.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(
        requesterAdminId,
        withdrawRequest.getCurrencyId(),
        WITHDRAW
    );
    return (WithdrawStatusEnum) withdrawRequest.getStatus().nextState(action, requesterAdminIsHolder, permission);
  }


}
