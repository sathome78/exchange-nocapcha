package me.exrates.service.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.*;
import me.exrates.model.enums.invoice.*;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.model.vo.WithdrawData;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.util.Cache;
import me.exrates.service.vo.ProfileData;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;
import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.UserCommentTopicEnum.WITHDRAW_DECLINE;
import static me.exrates.model.enums.UserCommentTopicEnum.WITHDRAW_POSTED;
import static me.exrates.model.enums.WalletTransferStatus.SUCCESS;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.*;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;
import static me.exrates.model.enums.invoice.PendingPaymentStatusEnum.ON_BCH_EXAM;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;

/**
 * created by ValkSam
 */

@Service
public class WithdrawServiceImpl implements WithdrawService {

  private static final Logger log = LogManager.getLogger("withdraw");

  @Value("${invoice.blockNotifyUsers}")
  private Boolean BLOCK_NOTIFYING;

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
      CreditsOperation creditsOperation,
      WithdrawData withdrawData,
      String userEmail,
      Locale locale) {
    ProfileData profileData = new ProfileData(1000);
    try {
      MerchantCurrencyAutoParamDto autoParamDto = getAutoWithdrawParamsByMerchantAndCurrency(
          creditsOperation.getMerchant().getId(),
          creditsOperation.getCurrency().getId());
      profileData.setTime1();
      WithdrawStatusEnum withdrawRequestStatus = ((WithdrawStatusEnum) WithdrawStatusEnum.getBeginState());
      WithdrawRequestCreateDto request = new WithdrawRequestCreateDto();
      request.setAutoEnabled(autoParamDto.getWithdrawAutoEnabled());
      request.setAutoThresholdAmount(autoParamDto.getWithdrawAutoThresholdAmount());
      request.setUserId(creditsOperation.getUser().getId());
      request.setUserEmail(creditsOperation.getUser().getEmail());
      request.setUserWalletId(creditsOperation.getWallet().getId());
      request.setCurrencyId(creditsOperation.getCurrency().getId());
      request.setAmount(creditsOperation.getFullAmount());
      request.setUserId(creditsOperation.getWallet().getUser().getId());
      request.setCommission(creditsOperation.getCommissionAmount());
      request.setCommissionId(creditsOperation.getCommission().getId());
      if (creditsOperation.getDestination().isPresent() && !creditsOperation.getDestination().get().isEmpty()) {
        request.setDestinationWallet(creditsOperation.getDestination().get());
      } else {
        request.setDestinationWallet(withdrawData.getUserAccount());
      }
      request.setMerchantId(creditsOperation.getMerchant().getId());
      creditsOperation
          .getMerchantImage()
          .ifPresent(request::setMerchantImage);
      request.setStatusId(withdrawRequestStatus.getCode());
      request.setRecipientBankName(withdrawData.getRecipientBankName());
      request.setRecipientBankCode(withdrawData.getRecipientBankCode());
      request.setUserFullName(withdrawData.getUserFullName());
      request.setRemark(withdrawData.getRemark());
      Integer requestId = createWithdraw(request);
      request.setId(requestId);
      profileData.setTime2();
    /**/
      String notification = null;
      String delayDescription = convertWithdrawAutoToString(autoParamDto.getWithdrawAutoDelaySeconds(), locale);
      try {
        notification = sendWithdrawalNotification(
            new WithdrawRequest(request),
            creditsOperation.getMerchant().getDescription(),
            delayDescription,
            locale);
      } catch (final MailException e) {
        log.error(e);
      }
      profileData.setTime3();
      BigDecimal newAmount = walletService.getWalletABalance(request.getUserWalletId());
      String currency = creditsOperation.getCurrency().getName();
      String balance = currency + " " + currencyService.amountToString(newAmount, currency);
      Map<String, String> result = new HashMap<>();
      result.put("success", notification);
      result.put("balance", balance);
      profileData.setTime4();
      return result;
    } finally {
      profileData.checkAndLog("slow create WithdrawalRequest: " + creditsOperation + " profile: " + profileData);
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
  public Map<String, String> withdrawRequest(CreditsOperation creditsOperation, WithdrawData withdrawData, String userEmail, Locale locale) {
    log.error("NOT IMPLEMENTED");
    throw new NotImplimentedMethod("method NOT IMPLEMENTED !");
  }

  @Override
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
            generateAndGetButtonsSet(
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
  @Transactional(readOnly = true)
  public List<MyInputOutputHistoryDto> getMyInputOutputHistory(
      CacheData cacheData,
      String email,
      Integer offset, Integer limit,
      Locale locale) {
    List<Integer> operationTypeList = OperationType.getInputOutputOperationsList()
        .stream()
        .map(OperationType::getType)
        .collect(Collectors.toList());
    List<MyInputOutputHistoryDto> result = withdrawRequestDao.findMyInputOutputHistoryByOperationType(email, offset, limit, operationTypeList, locale);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<MyInputOutputHistoryDto>() {{
        add(new MyInputOutputHistoryDto(false));
      }};
    } else {
      result.forEach(e ->
      {
        e.setSummaryStatus(generateAndGetSummaryStatus(e, locale));
        e.setButtons(generateAndGetButtonsSet(e.getStatus(), null, false, locale));
        e.setAuthorisedUserId(e.getUserId());
      });
    }
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public List<MyInputOutputHistoryDto> getMyInputOutputHistory(
      String email,
      Integer offset, Integer limit,
      Locale locale) {
    List<Integer> operationTypeList = OperationType.getInputOutputOperationsList()
        .stream()
        .map(OperationType::getType)
        .collect(Collectors.toList());
    List<MyInputOutputHistoryDto> result = withdrawRequestDao.findMyInputOutputHistoryByOperationType(email, offset, limit, operationTypeList, locale);
    result.forEach(e ->
    {
      e.setSummaryStatus(generateAndGetSummaryStatus(e, locale));
      e.setButtons(generateAndGetButtonsSet(e.getStatus(), null, false, locale));
      e.setAuthorisedUserId(e.getUserId());
    });
    return result;
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
  public void autoPostWithdrawalRequest(WithdrawRequestPostDto withdrawRequest) {
    IMerchantService merchantService = merchantServiceContext.getMerchantService(withdrawRequest.getMerchantServiceBeanName());
    WithdrawMerchantOperationDto withdrawMerchantOperation = WithdrawMerchantOperationDto.builder()
        .currency(withdrawRequest.getCurrencyName())
        .amount(BigDecimalProcessing.doAction(withdrawRequest.getAmount(), withdrawRequest.getCommissionAmount(), ActionType.SUBTRACT).toString())
        .accountTo(withdrawRequest.getWallet())
        .build();
    try {
      WithdrawRequestFlatDto withdrawRequestResult = postWithdrawal(withdrawRequest.getId(), null);
      merchantService.withdraw(withdrawMerchantOperation);
      /**/
      Locale locale = new Locale(userService.getPreferedLang(withdrawRequestResult.getUserId()));
      String title = messageSource.getMessage("withdrawal.posted.title", new Integer[]{withdrawRequest.getId()}, locale);
      String comment = messageSource.getMessage("merchants.withdrawNotification.".concat(withdrawRequestResult.getStatus().name()), new Integer[]{withdrawRequest.getId()}, locale);
      String userEmail = userService.getEmailById(withdrawRequestResult.getUserId());
      userService.addUserComment(WITHDRAW_POSTED, comment, userEmail, false);
      notificationService.notifyUser(withdrawRequestResult.getUserId(), NotificationEvent.IN_OUT, title, comment);
    } catch (Exception e) {
      throw new WithdrawRequestPostException(String.format("withdraw data: %s via merchant: %s", withdrawMerchantOperation.toString(), merchantService.toString()));
    }
  }

  @Override
  @Transactional
  public void postWithdrawalRequest(int requestId, Integer requesterAdminId) {
    WithdrawRequestFlatDto withdrawRequestResult = postWithdrawal(requestId, requesterAdminId);
    /**/
    Locale locale = new Locale(userService.getPreferedLang(withdrawRequestResult.getUserId()));
    String title = messageSource.getMessage("withdrawal.posted.title", new Integer[]{requestId}, locale);
    String comment = messageSource.getMessage("merchants.withdrawNotification.".concat(withdrawRequestResult.getStatus().name()), new Integer[]{requestId}, locale);
    String userEmail = userService.getEmailById(withdrawRequestResult.getUserId());
    userService.addUserComment(WITHDRAW_POSTED, comment, userEmail, false);
    notificationService.notifyUser(withdrawRequestResult.getUserId(), NotificationEvent.IN_OUT, title, comment);
  }

  private WithdrawRequestFlatDto postWithdrawal(int requestId, Integer requesterAdminId) {
    ProfileData profileData = new ProfileData(1000);
    try {
      WithdrawRequestFlatDto withdrawRequest = withdrawRequestDao.getFlatByIdAndBlock(requestId)
          .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
      WithdrawStatusEnum currentStatus = withdrawRequest.getStatus();
      InvoiceActionTypeEnum action = withdrawRequest.getStatus().availableForAction(POST_HOLDED) ? POST_HOLDED : POST_AUTO;
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

  private WithdrawStatusEnum checkPermissionOnActionAndGetNewStatus(Integer requesterAdminId, WithdrawRequestFlatDto withdrawRequest, InvoiceActionTypeEnum action) {
    Boolean requesterAdminIsHolder = requesterAdminId.equals(withdrawRequest.getAdminHolderId());
    InvoiceOperationPermission permission = userService.getCurrencyPermissionsByUserIdAndCurrencyIdAndDirection(
        requesterAdminId,
        withdrawRequest.getCurrencyId(),
        WITHDRAW
    );
    return (WithdrawStatusEnum) withdrawRequest.getStatus().nextState(action, requesterAdminIsHolder, permission);
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

  private String generateAndGetSummaryStatus(MyInputOutputHistoryDto row, Locale locale) {
    switch (row.getSourceType()) {
      case INVOICE: {
        InvoiceRequestStatusEnum status = (InvoiceRequestStatusEnum) row.getStatus();
        return messageSource.getMessage("merchants.invoice.".concat(status.name()), null, locale);
      }
      case WITHDRAW: {
        WithdrawStatusEnum status = (WithdrawStatusEnum) row.getStatus();
        return messageSource.getMessage("merchants.withdraw.".concat(status.name()), null, locale);
      }
      case BTC_INVOICE: {
        PendingPaymentStatusEnum status = (PendingPaymentStatusEnum) row.getStatus();
        if (status == ON_BCH_EXAM) {
          String confirmations = row.getConfirmation() == null ? "0" : row.getConfirmation().toString();
          String message = confirmations.concat("/").concat(String.valueOf(BitcoinService.CONFIRMATION_NEEDED_COUNT));
          return message;
        } else {
          return messageSource.getMessage("merchants.invoice.".concat(status.name()), null, locale);
        }
      }
      default: {
        return row.getTransactionProvided();
      }
    }
  }

  private List<Map<String, Object>> generateAndGetButtonsSet(
      InvoiceStatus status,
      InvoiceOperationPermission permittedOperation,
      boolean authorisedUserIsHolder,
      Locale locale) {
    if (status == null) return EMPTY_LIST;
    return status.getAvailableActionList(authorisedUserIsHolder, permittedOperation).stream()
        .filter(e -> e.getActionTypeButton() != null)
        .map(e -> new HashMap<String, Object>(e.getActionTypeButton().getProperty()))
        .peek(e -> e.put("buttonTitle", messageSource.getMessage((String) e.get("buttonTitle"), null, locale)))
        .collect(Collectors.toList());
  }


}
