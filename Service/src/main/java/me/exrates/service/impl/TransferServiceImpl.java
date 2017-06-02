package me.exrates.service.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.dao.TransferRequestDao;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.TransferRequest;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.vo.TransactionDescription;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.ITransfarable;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.OperationType.USER_TRANSFER;
import static me.exrates.model.enums.WalletTransferStatus.SUCCESS;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.REVOKE;

/**
 * created by ValkSam
 */

@Service
public class TransferServiceImpl implements TransferService {

  private static final Logger log = LogManager.getLogger("transfer");

  @Autowired
  private MerchantDao merchantDao;

  @Autowired
  private CurrencyService currencyService;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private TransferRequestDao transferRequestDao;

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
  public Map<String, Object> createTransferRequest(TransferRequestCreateDto request) {
    ProfileData profileData = new ProfileData(1000);
    try {
      IMerchantService merchantService = merchantServiceContext.getMerchantService(request.getServiceBeanName());
      request.setIsVoucher(((ITransfarable) merchantService).isVoucher());
      Integer requestId = createTransfer(request);
      request.setId(requestId);
      /**/
      String notification = null;
      try {
        notification = sendTransferNotification(
            new TransferRequest(request),
            request.getMerchantDescription(),
            request.getLocale());
      } catch (final MailException e) {
        log.error(e);
      }
      profileData.setTime2();
      BigDecimal newAmount = walletService.getWalletABalance(request.getUserWalletId());
      String currency = request.getCurrencyName();
      String balance = currency + " " + currencyService.amountToString(newAmount, currency);
      Map<String, Object> result = new HashMap<>();
      result.put("message", notification);
      result.put("balance", balance);
      profileData.setTime3();
      return result;
    } finally {
      profileData.checkAndLog("slow create TransferRequest: " + request + " profile: " + profileData);
    }
  }

  @Transactional(rollbackFor = {Exception.class})
  private Integer createTransfer(TransferRequestCreateDto transferRequestCreateDto) {
    TransferStatusEnum currentStatus = TransferStatusEnum.convert(transferRequestCreateDto.getStatusId());
    Boolean isVoucher = transferRequestCreateDto.getIsVoucher();
    InvoiceActionTypeEnum action = currentStatus.getStartAction(isVoucher);
    InvoiceStatus newStatus = currentStatus.nextState(action);
    transferRequestCreateDto.setStatusId(newStatus.getCode());
    int createdWithdrawRequestId = 0;
    if (walletService.ifEnoughMoney(
        transferRequestCreateDto.getUserWalletId(),
        transferRequestCreateDto.getAmount())) {
      if ((createdWithdrawRequestId = transferRequestDao.create(transferRequestCreateDto)) > 0) {
        String description = transactionDescription.get(currentStatus, action);
        if (isVoucher) {
          WalletTransferStatus result = walletService.walletInnerTransfer(
              transferRequestCreateDto.getUserWalletId(),
              transferRequestCreateDto.getAmount().negate(),
              TransactionSourceType.USER_TRANSFER,
              createdWithdrawRequestId,
              description);
          if (result != SUCCESS) {
            throw new TransferRequestCreationException(result.toString());
          }
        } else {
          walletService.transferCostsToUser(
              transferRequestCreateDto.getRecipientWalletId(),
              transferRequestCreateDto.getRecipient(),
              transferRequestCreateDto.getAmount(),
              transferRequestCreateDto.getLocale(),
              false);
        }
      }
    } else {
      throw new NotEnoughUserWalletMoneyException(transferRequestCreateDto.toString());
    }
    return createdWithdrawRequestId;
  }

  @Override
  @Transactional
  public List<MerchantCurrency> retrieveAdditionalParamsForWithdrawForMerchantCurrencies(List<MerchantCurrency> merchantCurrencies) {
    merchantCurrencies.forEach(e -> {
      IMerchantService merchantService = merchantServiceContext.getMerchantService(e.getMerchantId());
      e.setRecipientUserIsNeeded(((ITransfarable) merchantService).recipientUserIsNeeded());
    });
    return merchantCurrencies;
  }

  @Override
  @Transactional
  public void revokeTransferRequest(int requestId) {
    TransferRequestFlatDto transferRequest = transferRequestDao.getFlatByIdAndBlock(requestId)
        .orElseThrow(() -> new InvoiceNotFoundException(String.format("withdraw request id: %s", requestId)));
    WithdrawStatusEnum currentStatus = transferRequest.getStatus();
    InvoiceActionTypeEnum action = REVOKE;
    TransferStatusEnum newStatus = (TransferStatusEnum) currentStatus.nextState(action);
    transferRequestDao.setStatusById(requestId, newStatus);
    /**/
    Integer userWalletId = walletService.getWalletId(transferRequest.getUserId(), transferRequest.getCurrencyId());
    String description = transactionDescription.get(currentStatus, action);
    WalletTransferStatus result = walletService.walletInnerTransfer(
        userWalletId,
        transferRequest.getAmount(),
        TransactionSourceType.USER_TRANSFER,
        transferRequest.getId(),
        description);
    if (result != SUCCESS) {
      throw new TransferRequestRevokeException(result.toString());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<TransferRequestFlatDto> getRequestsByMerchantIdAndStatus(int merchantId, List<Integer> statuses) {
    return transferRequestDao.findRequestsByStatusAndMerchant(merchantId, statuses);
  }

  @Override
  @Transactional(readOnly = true)
  public TransferRequestFlatDto getFlatById(Integer id) {
    return transferRequestDao.getFlatById(id)
        .orElseThrow(() -> new TransferRequestNotFoundException(id.toString()));
  }

  private String sendTransferNotification(
      TransferRequest transferRequest,
      String merchantDescription,
      Locale locale) {
    final String notification;
    final Object[] messageParams = {
        transferRequest.getId(),
        merchantDescription
    };
    String notificationMessageCode;
    notificationMessageCode = "merchants.withdrawNotification.".concat(transferRequest.getStatus().name());
    notification = messageSource
        .getMessage(notificationMessageCode, messageParams, locale);
    notificationService.notifyUser(transferRequest.getUserEmail(), NotificationEvent.IN_OUT,
        "merchants.transferNotification.header", notificationMessageCode, messageParams);
    return notification;
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, String> correctAmountAndCalculateCommissionPreliminarily(
      Integer userId,
      BigDecimal amount,
      Integer currencyId,
      Integer merchantId,
      Locale locale) {
    OperationType operationType = USER_TRANSFER;
    BigDecimal addition = currencyService.computeRandomizedAddition(currencyId, operationType);
    amount = amount.add(addition);
    merchantService.checkAmountForMinSum(merchantId, currencyId, amount);
    Map<String, String> result = commissionService.computeCommissionAndMapAllToString(userId, amount, operationType, currencyId, merchantId, locale);
    result.put("addition", addition.toString());
    return result;
  }

}
