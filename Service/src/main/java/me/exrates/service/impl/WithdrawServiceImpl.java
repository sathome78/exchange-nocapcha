package me.exrates.service.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.enums.*;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.WithdrawRequestCreationException;
import me.exrates.service.exception.WithdrawRequestPostException;
import me.exrates.service.exception.WithdrawRequestRevokeException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.vo.ProfileData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.UserCommentTopicEnum.WITHDRAW_DECLINE;
import static me.exrates.model.enums.UserCommentTopicEnum.WITHDRAW_POSTED;
import static me.exrates.model.enums.WalletTransferStatus.SUCCESS;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.*;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;

/**
 * created by ValkSam
 */

@Service
public class WithdrawServiceImpl implements WithdrawService {

  private static final Logger log = LogManager.getLogger("withdraw");

  @Autowired
  private MerchantDao merchantDao;

  @Autowired
  private CurrencyService currencyService;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private WithdrawRequestDao withdrawRequestDao;

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

  @Autowired
  InputOutputService inputOutputService;

  @Autowired
  private MerchantService merchantService;

  @Override
  @Transactional
  public void setAutoWithdrawParams(MerchantCurrencyOptionsDto merchantCurrencyOptionsDto) {
    merchantDao.setAutoWithdrawParamsByMerchantAndCurrency(
        merchantCurrencyOptionsDto.getMerchantId(),
        merchantCurrencyOptionsDto.getCurrencyId(),
        merchantCurrencyOptionsDto.getWithdrawAutoEnabled(),
        merchantCurrencyOptionsDto.getWithdrawAutoDelaySeconds(),
        merchantCurrencyOptionsDto.getWithdrawAutoThresholdAmount());
  }

  @Override
  @Transactional
  public List<WithdrawRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(
      String startDate,
      String endDate,
      List<Integer> roleIdList,
      List<Integer> currencyList) {
    return withdrawRequestDao.findAllByDateIntervalAndRoleAndCurrency(startDate, endDate, roleIdList, currencyList);
  }

  @Override
  @Transactional(readOnly = true)
  public MerchantCurrencyAutoParamDto getAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId) {
    return merchantDao.findAutoWithdrawParamsByMerchantAndCurrency(merchantId, currencyId);
  }

  @Override
  @Transactional
  public Map<String, String> createWithdrawalRequest(
      WithdrawRequestCreateDto request,
      Locale locale) {
    ProfileData profileData = new ProfileData(1000);
    try {
      MerchantCurrencyAutoParamDto autoParamDto = getAutoWithdrawParamsByMerchantAndCurrency(
          request.getMerchantId(),
          request.getCurrencyId());
      profileData.setTime1();
      request.setAutoEnabled(autoParamDto.getWithdrawAutoEnabled());
      request.setAutoThresholdAmount(autoParamDto.getWithdrawAutoThresholdAmount());
      Integer requestId = createWithdraw(request);
      request.setId(requestId);
      /**/
      String notification = null;
      String delayDescription = convertWithdrawAutoToString(autoParamDto.getWithdrawAutoDelaySeconds(), locale);
      try {
        notification = sendWithdrawalNotification(
            new WithdrawRequest(request),
            request.getMerchantDescription(),
            delayDescription,
            locale);
      } catch (final MailException e) {
        log.error(e);
      }
      profileData.setTime2();
      BigDecimal newAmount = walletService.getWalletABalance(request.getUserWalletId());
      String currency = request.getCurrencyName();
      String balance = currency + " " + currencyService.amountToString(newAmount, currency);
      Map<String, String> result = new HashMap<>();
      result.put("message", notification);
      result.put("balance", balance);
      profileData.setTime3();
      return result;
    } finally {
      profileData.checkAndLog("slow create WithdrawalRequest: " + request + " profile: " + profileData);
    }
  }

  @Transactional(rollbackFor = {Exception.class})
  private Integer createWithdraw(WithdrawRequestCreateDto withdrawRequestCreateDto) {
    WithdrawStatusEnum currentStatus = WithdrawStatusEnum.convert(withdrawRequestCreateDto.getStatusId());
    InvoiceActionTypeEnum action = currentStatus.getStartAction(
        withdrawRequestCreateDto.getAutoEnabled(),
        withdrawRequestCreateDto.getAmount(),
        withdrawRequestCreateDto.getAutoThresholdAmount());
    InvoiceStatus newStatus = currentStatus.nextState(action);
    withdrawRequestCreateDto.setStatusId(newStatus.getCode());
    int createdWithdrawRequestId = 0;
    if (walletService.ifEnoughMoney(
        withdrawRequestCreateDto.getUserWalletId(),
        withdrawRequestCreateDto.getAmount())) {
      if ((createdWithdrawRequestId = withdrawRequestDao.create(withdrawRequestCreateDto)) > 0) {
        String description = transactionDescription.get(currentStatus, action);
        WalletTransferStatus result = walletService.walletInnerTransfer(
            withdrawRequestCreateDto.getUserWalletId(),
            withdrawRequestCreateDto.getAmount().negate(),
            TransactionSourceType.WITHDRAW,
            createdWithdrawRequestId,
            description);
        if (result != SUCCESS) {
          throw new WithdrawRequestCreationException(result.toString());
        }
      }
    } else {
      throw new NotEnoughUserWalletMoneyException(withdrawRequestCreateDto.toString());
    }
    return createdWithdrawRequestId;
  }

  private String convertWithdrawAutoToString(Integer seconds, Locale locale) {
    if (seconds <= 0) {
      return "";
    }
    if (seconds > 60 * 60 - 1) {
      return String.valueOf(Math.round(seconds / (60 * 60)))
          .concat(" ")
          .concat(messageSource.getMessage("merchant.withdrawAutoDelayHour", null, locale));
    }
    if (seconds > 59) {
      return String.valueOf(Math.round(seconds / 60))
          .concat(" ")
          .concat(messageSource.getMessage("merchant.withdrawAutoDelayMinute", null, locale));
    }
    return String.valueOf(seconds)
        .concat(" ")
        .concat(messageSource.getMessage("merchant.withdrawAutoDelaySecond", null, locale));
  }

  @Override
  @Transactional
  public DataTable<List<WithdrawRequestsAdminTableDto>> getWithdrawRequestByStatusList(
      List<Integer> requestStatus,
      DataTableParams dataTableParams,
      WithdrawFilterData withdrawFilterData,
      String authorizedUserEmail,
      Locale locale) {
    Integer authorizedUserId = userService.getIdByEmail(authorizedUserEmail);
    PagingData<List<WithdrawRequestFlatDto>> result = withdrawRequestDao.getPermittedFlatByStatus(
        requestStatus,
        authorizedUserId,
        dataTableParams,
        withdrawFilterData);
    DataTable<List<WithdrawRequestsAdminTableDto>> output = new DataTable<>();
    output.setData(result.getData().stream()
        .map(e -> new WithdrawRequestsAdminTableDto(e, withdrawRequestDao.getAdditionalDataForId(e.getId())))
        .peek(e -> e.setButtons(
            inputOutputService.generateAndGetButtonsSet(
                e.getStatus(),
                e.getInvoiceOperationPermission(),
                authorizedUserId.equals(e.getAdminHolderId()),
                locale)
        ))
        .collect(Collectors.toList())
    );
    output.setRecordsTotal(result.getTotal());
    output.setRecordsFiltered(result.getFiltered());
    return output;
  }

  @Override
  @Transactional
  public WithdrawRequestsAdminTableDto getWithdrawRequestById(
      Integer id,
      String authorizedUserEmail) {
    Integer authorizedUserId = userService.getIdByEmail(authorizedUserEmail);
    WithdrawRequestFlatDto withdraw = withdrawRequestDao.getPermittedFlatById(
        id,
        authorizedUserId);
    DataTable<List<WithdrawRequestsAdminTableDto>> output = new DataTable<>();
    return new WithdrawRequestsAdminTableDto(withdraw, withdrawRequestDao.getAdditionalDataForId(withdraw.getId()));
  }

  @Override
  @Transactional
  public void revokeWithdrawalRequest(int requestId) {
    WithdrawRequestFlatDto withdrawRequest = withdrawRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
    WithdrawStatusEnum currentStatus = withdrawRequest.getStatus();
    InvoiceActionTypeEnum action = REVOKE;
    WithdrawStatusEnum newStatus = (WithdrawStatusEnum) currentStatus.nextState(action);
    withdrawRequestDao.setStatusById(requestId, newStatus);
    /**/
    Integer userWalletId = walletService.getWalletId(withdrawRequest.getUserId(), withdrawRequest.getCurrencyId());
    String description = transactionDescription.get(currentStatus, action);
    WalletTransferStatus result = walletService.walletInnerTransfer(
        userWalletId,
        withdrawRequest.getAmount(),
        TransactionSourceType.WITHDRAW,
        withdrawRequest.getId(),
        description);
    if (result != SUCCESS) {
      throw new WithdrawRequestRevokeException(result.toString());
    }
  }

  @Override
  @Transactional
  public void takeInWorkWithdrawalRequest(int requestId, Integer requesterAdminId) {
    WithdrawRequestFlatDto withdrawRequest = withdrawRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
    InvoiceActionTypeEnum action = TAKE_TO_WORK;
    WithdrawStatusEnum newStatus = checkPermissionOnActionAndGetNewStatus(requesterAdminId, withdrawRequest, action);
    withdrawRequestDao.setStatusById(requestId, newStatus);
    /**/
    withdrawRequestDao.setHolderById(requestId, requesterAdminId);
  }

  @Override
  @Transactional
  public void returnFromWorkWithdrawalRequest(int requestId, Integer requesterAdminId) {
    WithdrawRequestFlatDto withdrawRequest = withdrawRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
    InvoiceActionTypeEnum action = RETURN_FROM_WORK;
    WithdrawStatusEnum newStatus = checkPermissionOnActionAndGetNewStatus(requesterAdminId, withdrawRequest, action);
    withdrawRequestDao.setStatusById(requestId, newStatus);
    /**/
    withdrawRequestDao.setHolderById(requestId, null);
  }

  @Override
  @Transactional
  public void declineWithdrawalRequest(int requestId, Integer requesterAdminId, String comment) {
    ProfileData profileData = new ProfileData(1000);
    try {
      WithdrawRequestFlatDto withdrawRequest = withdrawRequestDao.getFlatByIdAndBlock(requestId)
          .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
      WithdrawStatusEnum currentStatus = withdrawRequest.getStatus();
      InvoiceActionTypeEnum action = withdrawRequest.getStatus().availableForAction(DECLINE_HOLDED) ? DECLINE_HOLDED : DECLINE;
      WithdrawStatusEnum newStatus = checkPermissionOnActionAndGetNewStatus(requesterAdminId, withdrawRequest, action);
      withdrawRequestDao.setStatusById(requestId, newStatus);
      withdrawRequestDao.setHolderById(requestId, requesterAdminId);
      profileData.setTime1();
      /**/
      Integer userWalletId = walletService.getWalletId(withdrawRequest.getUserId(), withdrawRequest.getCurrencyId());
      String description = transactionDescription.get(currentStatus, action);
      WalletTransferStatus result = walletService.walletInnerTransfer(
          userWalletId,
          withdrawRequest.getAmount(),
          TransactionSourceType.WITHDRAW,
          withdrawRequest.getId(),
          description);
      if (result != SUCCESS) {
        throw new WithdrawRequestRevokeException(result.toString());
      }
      profileData.setTime2();
      /**/
      Locale locale = new Locale(userService.getPreferedLang(withdrawRequest.getUserId()));
      String title = messageSource.getMessage("withdrawal.declined.title", new Integer[]{requestId}, locale);
      String notification = String.join(": ", messageSource.getMessage("merchants.withdrawNotification.".concat(newStatus.name()), new Integer[]{requestId}, locale),
          comment);
      String userEmail = userService.getEmailById(withdrawRequest.getUserId());
      userService.addUserComment(WITHDRAW_DECLINE, comment, userEmail, false);
      notificationService.notifyUser(withdrawRequest.getUserId(), NotificationEvent.IN_OUT, title, notification);
      profileData.setTime3();
    } finally {
      profileData.checkAndLog("slow decline WithdrawalRequest: " + requestId + " profile: " + profileData);
    }
  }

  @Override
  @Transactional
  public void confirmWithdrawalRequest(int requestId, Integer requesterAdminId) {
    WithdrawRequestFlatDto withdrawRequest = withdrawRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
    WithdrawStatusEnum currentStatus = withdrawRequest.getStatus();
    InvoiceActionTypeEnum action = CONFIRM_ADMIN;
    WithdrawStatusEnum newStatus = checkPermissionOnActionAndGetNewStatus(requesterAdminId, withdrawRequest, action);
    withdrawRequestDao.setStatusById(requestId, newStatus);
    /**/
    withdrawRequestDao.setHolderById(requestId, requesterAdminId);
  }

  @Override
  @Transactional
  public void rejectError(int requestId, long timeoutInMinutes, String reasonCode) {
    WithdrawRequestFlatDto withdrawRequest = withdrawRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
    LocalDateTime rejectTimeLimit = withdrawRequest.getStatusModificationDate().plusMinutes(timeoutInMinutes);
    if (LocalDateTime.now().isAfter(rejectTimeLimit)) {
      InvoiceStatus newStatus = withdrawRequest.getStatus().nextState(REJECT_ERROR);
      withdrawRequestDao.setStatusById(requestId, newStatus);
      Integer userWalletId = walletService.getWalletId(withdrawRequest.getUserId(), withdrawRequest.getCurrencyId());
      String description = transactionDescription.get(withdrawRequest.getStatus(), REJECT_ERROR);
      WalletTransferStatus result = walletService.walletInnerTransfer(
          userWalletId,
          withdrawRequest.getAmount(),
          TransactionSourceType.WITHDRAW,
          withdrawRequest.getId(),
          description);
      if (result != SUCCESS) {
        throw new WithdrawRequestPostException(result.name());
      }
      Locale locale = new Locale(userService.getPreferedLang(withdrawRequest.getUserId()));
      String title = messageSource.getMessage("withdraw.rejectError.title", null, locale);
      String reason = messageSource.getMessage(reasonCode, null, locale);
      String message = messageSource.getMessage("withdraw.rejectError.body", new Object[]{withdrawRequest.getId(), reason}, locale);
      notificationService.notifyUser(withdrawRequest.getUserId(), NotificationEvent.IN_OUT, title, message);
    }
  }

  @Override
  @Transactional
  public void rejectError(int requestId, String reasonCode) {
    rejectError(requestId, 0, reasonCode);
  }


  @Override
  @Transactional
  public void rejectToReview(int requestId) {
    WithdrawRequestFlatDto withdrawRequest = withdrawRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
    InvoiceStatus newStatus = withdrawRequest.getStatus().nextState(REJECT_TO_REVIEW);
    withdrawRequestDao.setStatusById(requestId, newStatus);
    Locale locale = new Locale(userService.getPreferedLang(withdrawRequest.getUserId()));
    String title = messageSource.getMessage("withdraw.rejectReview.title", null, locale);
    String message = messageSource.getMessage("withdraw.rejectReview.body", new Object[]{withdrawRequest.getId()}, locale);
    notificationService.notifyUser(withdrawRequest.getUserId(), NotificationEvent.IN_OUT, title, message);
  }


  @Override
  @Transactional
  public void autoPostWithdrawalRequest(WithdrawRequestPostDto withdrawRequest) {
    IMerchantService merchantService = merchantServiceContext.getMerchantService(withdrawRequest.getMerchantServiceBeanName());
    WithdrawMerchantOperationDto withdrawMerchantOperation = WithdrawMerchantOperationDto.builder()
        .currency(withdrawRequest.getCurrencyName())
        .amount(BigDecimalProcessing.doAction(withdrawRequest.getAmount(), withdrawRequest.getCommissionAmount(), ActionType.SUBTRACT).toString())
        .accountTo(withdrawRequest.getWallet())
        .build();
    try {
      WithdrawRequestFlatDto withdrawRequestResult = postWithdrawal(withdrawRequest.getId(), null, withdrawRequest.isWithdrawTransferringConfirmNeeded());
      merchantService.withdraw(withdrawMerchantOperation);
      /**/
      Locale locale = new Locale(userService.getPreferedLang(withdrawRequestResult.getUserId()));
      String title = messageSource.getMessage("withdrawal.posted.title", new Integer[]{withdrawRequest.getId()}, locale);
      String comment = messageSource.getMessage("merchants.withdrawNotification.".concat(withdrawRequestResult.getStatus().name()), new Integer[]{withdrawRequest.getId()}, locale);
      String userEmail = userService.getEmailById(withdrawRequestResult.getUserId());
      userService.addUserComment(WITHDRAW_POSTED, comment, userEmail, false);
      notificationService.notifyUser(withdrawRequestResult.getUserId(), NotificationEvent.IN_OUT, title, comment);
    } catch (MerchantException e) {
      log.error(e);
      throw e;
    } catch (Exception e) {
      throw new WithdrawRequestPostException(String.format("withdraw data: %s via merchant: %s", withdrawMerchantOperation.toString(), merchantService.toString()));
    }
  }

  @Override
  @Transactional
  public void postWithdrawalRequest(int requestId, Integer requesterAdminId) {
    WithdrawRequestFlatDto withdrawRequestResult = postWithdrawal(requestId, requesterAdminId, false);
    /**/
    Locale locale = new Locale(userService.getPreferedLang(withdrawRequestResult.getUserId()));
    String title = messageSource.getMessage("withdrawal.posted.title", new Integer[]{requestId}, locale);
    String comment = messageSource.getMessage("merchants.withdrawNotification.".concat(withdrawRequestResult.getStatus().name()), new Integer[]{requestId}, locale);
    String userEmail = userService.getEmailById(withdrawRequestResult.getUserId());
    userService.addUserComment(WITHDRAW_POSTED, comment, userEmail, false);
    notificationService.notifyUser(withdrawRequestResult.getUserId(), NotificationEvent.IN_OUT, title, comment);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ClientBank> findClientBanksForCurrency(Integer currencyId) {
    return withdrawRequestDao.findClientBanksForCurrency(currencyId);
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, String> correctAmountAndCalculateCommissionPreliminarily(Integer userId, BigDecimal amount, Integer currencyId, Integer merchantId, Locale locale) {
    OperationType operationType = OUTPUT;
    BigDecimal addition = currencyService.computeRandomizedAddition(currencyId, operationType);
    amount = amount.add(addition);
    merchantService.checkAmountForMinSum(merchantId, currencyId, amount);
    Map<String, String> result = commissionService.computeCommissionAndMapAllToString(userId, amount, operationType, currencyId, merchantId, locale);
    result.put("addition", addition.toString());
    return result;
  }

  private WithdrawRequestFlatDto postWithdrawal(int requestId, Integer requesterAdminId, boolean withdrawTransferringConfirmNeeded) {
    ProfileData profileData = new ProfileData(1000);
    try {
      WithdrawRequestFlatDto withdrawRequest = withdrawRequestDao.getFlatByIdAndBlock(requestId)
          .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
      WithdrawStatusEnum currentStatus = withdrawRequest.getStatus();
      InvoiceActionTypeEnum action = withdrawTransferringConfirmNeeded ? START_BCH_EXAMINE :
          withdrawRequest.getStatus().availableForAction(POST_HOLDED) ? POST_HOLDED : POST_AUTO;
      WithdrawStatusEnum newStatus = requesterAdminId == null ?
          (WithdrawStatusEnum) currentStatus.nextState(action) :
          checkPermissionOnActionAndGetNewStatus(requesterAdminId, withdrawRequest, action);
      withdrawRequestDao.setStatusById(requestId, newStatus);
      withdrawRequestDao.setHolderById(requestId, requesterAdminId);
      withdrawRequest.setStatus(newStatus);
      withdrawRequest.setAdminHolderId(requesterAdminId);
      profileData.setTime1();
      /**/
      Integer userWalletId = walletService.getWalletId(withdrawRequest.getUserId(), withdrawRequest.getCurrencyId());
      String description = transactionDescription.get(currentStatus, action);
      WalletTransferStatus result = walletService.walletInnerTransfer(
          userWalletId,
          withdrawRequest.getAmount(),
          TransactionSourceType.WITHDRAW,
          withdrawRequest.getId(),
          description);
      if (result != SUCCESS) {
        throw new WithdrawRequestPostException(result.name());
      }
      profileData.setTime2();
      WalletOperationData walletOperationData = new WalletOperationData();
      walletOperationData.setOperationType(OUTPUT);
      walletOperationData.setWalletId(userWalletId);
      walletOperationData.setAmount(withdrawRequest.getAmount());
      walletOperationData.setBalanceType(ACTIVE);
      walletOperationData.setCommission(new Commission(withdrawRequest.getCommissionId()));
      walletOperationData.setCommissionAmount(withdrawRequest.getCommissionAmount());
      walletOperationData.setSourceType(TransactionSourceType.WITHDRAW);
      walletOperationData.setSourceId(withdrawRequest.getId());
      walletOperationData.setDescription(description);
      WalletTransferStatus walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
      if (walletTransferStatus != SUCCESS) {
        throw new WithdrawRequestPostException(walletTransferStatus.name());
      }
      profileData.setTime3();
      CompanyWallet companyWallet = companyWalletService.findByCurrency(new Currency(withdrawRequest.getCurrencyId()));
      companyWalletService.deposit(
          companyWallet,
          withdrawRequest.getAmount().negate(),
          walletOperationData.getCommissionAmount()
      );
      profileData.setTime4();
      return withdrawRequest;
    } finally {
      profileData.checkAndLog("slow post WithdrawalRequest: " + requestId + " profile: " + profileData);
    }
  }

  @Override
  @Transactional
  public void setAllAvailableInPostingStatus() {
    InvoiceActionTypeEnum action = HOLD_TO_POST;
    List<Integer> invoiceRequestStatusIdList = WithdrawStatusEnum.getAvailableForActionStatusesList(action).stream()
        .map(InvoiceStatus::getCode)
        .collect(Collectors.toList());
    WithdrawStatusEnum newStatus = (WithdrawStatusEnum) WithdrawStatusEnum.getInvoiceStatusAfterAction(action);
    withdrawRequestDao.setInPostingStatusByStatus(
        newStatus.getCode(),
        invoiceRequestStatusIdList);
  }

  @Override
  @Transactional(isolation = Isolation.READ_UNCOMMITTED)
  public List<WithdrawRequestPostDto> dirtyReadForPostByStatusList(InvoiceStatus status) {
    return withdrawRequestDao.getForPostByStatusList(status.getCode());
  }

  @Override
  @Transactional(readOnly = true)
  public boolean checkOutputRequestsLimit(int merchantId, String email) {
    return withdrawRequestDao.checkOutputRequests(merchantId, email);
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

  private String sendWithdrawalNotification(
      WithdrawRequest withdrawRequest,
      String merchantDescription,
      String withdrawDelay,
      Locale locale) {
    final String notification;
    final Object[] messageParams = {
        withdrawRequest.getId(),
        merchantDescription,
        withdrawDelay.isEmpty() ? "" : withdrawDelay
    };
    String notificationMessageCode;
    notificationMessageCode = "merchants.withdrawNotification.".concat(withdrawRequest.getStatus().name());
    notification = messageSource
        .getMessage(notificationMessageCode, messageParams, locale);
    notificationService.notifyUser(withdrawRequest.getUserEmail(), NotificationEvent.IN_OUT,
        "merchants.withdrawNotification.header", notificationMessageCode, messageParams);
    return notification;
  }


}
