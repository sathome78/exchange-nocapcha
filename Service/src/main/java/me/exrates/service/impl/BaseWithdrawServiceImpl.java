package me.exrates.service.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.model.Transaction;
import me.exrates.model.WithdrawRequest;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.WithdrawalRequestStatus;
import me.exrates.model.enums.invoice.*;
import me.exrates.service.BitcoinService;
import me.exrates.service.NotificationService;
import me.exrates.service.WithdrawService;
import me.exrates.service.exception.MerchantInternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.EMPTY_LIST;
import static me.exrates.model.enums.invoice.PendingPaymentStatusEnum.ON_BCH_EXAM;

/**
 * Created by ValkSam on 17.03.2017.
 */
public abstract class BaseWithdrawServiceImpl implements WithdrawService {

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private MerchantDao merchantDao;

  protected String sendWithdrawalNotification(final WithdrawRequest withdrawRequest,
                                              final WithdrawalRequestStatus status,
                                              final Locale locale) {
    final String notification;
    final Transaction transaction = withdrawRequest.getTransaction();
    final Object[] messageParams = {
        transaction
            .getId(),
        transaction
            .getMerchant()
            .getDescription()
    };
    String notificationMessageCode;
    switch (status) {
      case NEW:
        notificationMessageCode = "merchants.withdrawNotification";
        break;
      case ACCEPTED:
        notificationMessageCode = "merchants.withdrawNotificationAccepted";
        break;
      case DECLINED:
        notificationMessageCode = "merchants.withdrawNotificationDeclined";
        break;
      default:
        throw new MerchantInternalException(status + "Withdrawal status is invalid");
    }
    notification = messageSource
        .getMessage(notificationMessageCode, messageParams, locale);
    notificationService.notifyUser(withdrawRequest.getUserEmail(), NotificationEvent.IN_OUT,
        "merchants.withdrawNotification.header", notificationMessageCode, messageParams);
    return notification;
  }

  protected String sendWithdrawalNotification(
      WithdrawRequest withdrawRequest,
      String merchantDescription,
      String withdrawDelay,
      Locale locale) {
    final String notification;
    final Object[] messageParams = {
        withdrawRequest.getId(),
        merchantDescription,
        withdrawDelay.isEmpty() ? "" : "within".concat(withdrawDelay)
    };
    String notificationMessageCode;
    notificationMessageCode = "merchants.withdrawNotification.".concat(withdrawRequest.getWithdrawStatus().name());
    notification = messageSource
        .getMessage(notificationMessageCode, messageParams, locale);
    notificationService.notifyUser(withdrawRequest.getUserEmail(), NotificationEvent.IN_OUT,
        "merchants.withdrawNotification.header", notificationMessageCode, messageParams);
    return notification;
  }

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

  protected String generateAndGetSummaryStatus(MyInputOutputHistoryDto row, Locale locale) {
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

  protected List<Map<String, Object>> generateAndGetButtonsSet(
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
