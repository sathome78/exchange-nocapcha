package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificationMessageLogDao;
import me.exrates.dao.NotificationMessagesDao;
import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.dao.NotificatorsDao;
import me.exrates.model.Commission;
import me.exrates.model.Currency;
import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.dto.Notificator;
import me.exrates.model.enums.*;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.MessageUndeliweredException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.PaymentException;
import me.exrates.service.exception.UnknownNotyPaymentTypeException;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static me.exrates.model.enums.OperationType.BUY;
import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.util.BigDecimalProcessing.doAction;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;

/**
 * Created by Maks on 29.09.2017.
 */
@Log4j2
@Service
public class NotificationsServiceImpl implements NotificationService {

    @Autowired
    Map<String, NotificatorService> notificatorsMap;

    @Autowired
    private NotificatorsDao notificatorsDao;
    @Autowired
    private NotificationMessagesDao notificationMessagesDao;
    @Autowired
    private NotificationUserSettingsDao settingsDao;
    @Autowired
    private NotificationMessageLogDao messageLogDao;
    @Autowired
    private UserService userService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private CommissionService comissionService;


    private NotificationsUserSetting defaultSetting;
    private Currency currency;
    private static final String CURRENCY_NAME_FOR_PAY = "USD";

    public void init() {
        defaultSetting = NotificationsUserSetting.builder().notificatorId(1).build();
        currency = currencyService.findByName(CURRENCY_NAME_FOR_PAY);
    }

    @Override
    @Transactional
    public NotificationResultDto notifyUser(final String userEmail, final String message, final String subject, final NotificationMessageEventEnum event) {
        NotificationsUserSetting setting = settingsDao.getByUserAndEvent(userService.getIdByEmail(userEmail), event)
                                                        .orElse(defaultSetting);
        NotificatorService service = getNotificationService(setting.getNotificatorId());
        NotificationTypeEnum notificationTypeEnum = service.getNotificationType();
        String contactToNotify;
        int notyId = messageLogDao.saveLogNotification(userEmail, null, event, notificationTypeEnum);
        switch (service.getPayType()) {
            case FREE: {
                contactToNotify = service.sendMessageToUser(userEmail, message, subject);
                break;
            }
            case PAY_FOR_EACH: {
                try {
                    OperationType operationType = OperationType.BUY_SMS;
                    BigDecimal payedSum = payForMessage(service.getMessageCost(), userEmail, notyId, operationType, operationType.name().concat(":").concat(event.name()));
                    messageLogDao.update(notyId, payedSum, notificationTypeEnum);
                    contactToNotify = service.sendMessageToUser(userEmail, message, subject);
                } catch (PaymentException | MessageUndeliweredException e) {
                    log.error(e);
                    contactToNotify = service.sendMessageToUser(userEmail, message, subject);
                    messageLogDao.update(notyId, null, NotificationTypeEnum.EMAIL);
                }
                break;
            }
            case PREPAID_LIFETIME: {
                messageLogDao.saveLogNotification(userEmail, service.getMessageCost(), event, notificationTypeEnum);
                contactToNotify = service.sendMessageToUser(userEmail, message, subject);
                break;
            }
            default:{
                throw new UnknownNotyPaymentTypeException();
            }
        }
        return getResponseString(event, notificationTypeEnum, contactToNotify);
    }

    private NotificationResultDto getResponseString(NotificationMessageEventEnum event, NotificationTypeEnum typeEnum, String contactToNotify) {
        String message = notificationMessagesDao.gerResourceString(event, typeEnum);
        return new NotificationResultDto(message, new String[]{contactToNotify});
    }



    private NotificatorService getNotificationService(Integer notificatorId) {
        Notificator notificator = Optional.ofNullable(this.getById(notificatorId))
                .orElseThrow(() -> new RuntimeException(String.valueOf(notificatorId)));
        return notificatorsMap.get(notificator.getBeanName());
    }

    private Notificator getById(int id) {
        return notificatorsDao.getById(id);
    }


    @Transactional
    private BigDecimal payForMessage(BigDecimal amount, String userEmail, int notyLogId, OperationType operationType, String description) {
        UserRole role = userService.getUserRoleFromDB(userEmail);
        Commission commission = comissionService.findCommissionByTypeAndRole(operationType, role);
        int userId = userService.getIdByEmail(userEmail);
        WalletOperationData walletOperationData = new WalletOperationData();
        walletOperationData.setOperationType(operationType);
        walletOperationData.setWalletId(walletService.getWalletId(userId, currency.getId()));
        walletOperationData.setBalanceType(ACTIVE);
        walletOperationData.setCommission(commission);
        BigDecimal comissionAmount = doAction(amount, commission.getValue(), ActionType.MULTIPLY_PERCENT);
        BigDecimal totalAmount = doAction(amount, comissionAmount, ActionType.ADD);
        walletOperationData.setCommissionAmount(comissionAmount);
        walletOperationData.setAmount(totalAmount);
        walletOperationData.setSourceType(TransactionSourceType.BUY_NOTIFICATION);
        walletOperationData.setSourceId(notyLogId);
        walletOperationData.setDescription(description);
        WalletTransferStatus walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
        if(!walletTransferStatus.equals(WalletTransferStatus.SUCCESS)) {
            throw new PaymentException(walletTransferStatus);
        }
        return totalAmount;
    }


}
