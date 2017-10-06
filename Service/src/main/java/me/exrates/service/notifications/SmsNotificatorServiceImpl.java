package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.*;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.exception.MessageUndeliweredException;
import me.exrates.service.exception.PaymentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static me.exrates.model.util.BigDecimalProcessing.doAction;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;

/**
 * Created by Maks on 29.09.2017.
 */
@Log4j2(topic = "SMS_NOTY")
@Component
public class SmsNotificatorServiceImpl implements NotificatorService {

    @Autowired
    private UserService userService;
    @Autowired
    private NotificatorsService notificatorsService;
    @Autowired
    private NotificatorsPriceService notificatorsPriceService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private CurrencyService currencyService;

    private static final String CURRENCY_NAME = "USD";


    @Transactional
    @Override
    public String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException {
        payForMessage(userEmail, "send notification sms");
        return null;
    }

    @Override
    public NotificationPayTypeEnum getPayType() {
        return NotificationPayTypeEnum.PAY_FOR_EACH;
    }

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.SMS;
    }

    @Transactional
    private BigDecimal payForMessage(String userEmail, String description) {
        UserRole role = userService.getUserRoleFromDB(userEmail);
        BigDecimal messageCost = notificatorsService.getMessagePrice(getNotificationType().getCode());
        BigDecimal fee = notificatorsPriceService.getFeeForNotificatorAndRole(getNotificationType().getCode(), role.getRole());
        BigDecimal totalAmount = doAction(messageCost, fee, ActionType.ADD);
        int userId = userService.getIdByEmail(userEmail);
        WalletOperationData walletOperationData = new WalletOperationData();
        walletOperationData.setOperationType(OperationType.PAY_FOR_NOTIFICATION);
        walletOperationData.setWalletId(walletService.getWalletId(userId, currencyService.findByName(CURRENCY_NAME).getId()));
        walletOperationData.setBalanceType(ACTIVE);
        walletOperationData.setAmount(totalAmount);
        walletOperationData.setSourceType(TransactionSourceType.NOTIFICATIONS);
        walletOperationData.setDescription(description);
        WalletTransferStatus walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
        if(!walletTransferStatus.equals(WalletTransferStatus.SUCCESS)) {
            throw new PaymentException(walletTransferStatus);
        }
        return totalAmount;
    }
}
