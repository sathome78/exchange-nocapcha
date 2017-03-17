package me.exrates.service.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.model.Transaction;
import me.exrates.model.WithdrawRequest;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.WithdrawalRequestStatus;
import me.exrates.service.NotificationService;
import me.exrates.service.WithdrawService;
import me.exrates.service.exception.MerchantInternalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

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

}
