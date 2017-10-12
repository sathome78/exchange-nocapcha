package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.SmsSubscriptionDao;
import me.exrates.model.Email;
import me.exrates.model.dto.LookupResponseDto;
import me.exrates.model.dto.NotificationPayEventEnum;
import me.exrates.model.dto.SmsSubscriptionDto;
import me.exrates.model.enums.*;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import me.exrates.service.notifications.sms.Sms1s2uService;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static me.exrates.model.util.BigDecimalProcessing.doAction;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;

/**
 * Created by Maks on 29.09.2017.
 */
@Log4j2(topic = "message_notify")
@Service
public class SmsNotificatorServiceImpl implements NotificatorService, Subscribable {

    @Autowired
    private UserService userService;
    @Autowired
    private NotificatorsService notificatorsService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private SmsSubscriptionDao subscriptionDao;
    @Autowired
    private Sms1s2uService smsService;
    @Autowired
    private SendMailService sendMailService;

    private static final String CURRENCY_NAME = "USD";


    @Transactional
    @Override
    public String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException {
        int userId = userService.getIdByEmail(userEmail);
        BigDecimal messageCost = notificatorsService.getMessagePrice(getNotificationType().getCode());
        pay(
                messageCost,
                userId,
                getNotificationType().name().concat(":").concat(NotificationPayEventEnum.BUY_ONE.name()),
                NotificationPayEventEnum.BUY_ONE);
        SmsSubscriptionDto subscriptionDto = subscriptionDao.getByUserId(userService.getIdByEmail(userEmail));
        smsService.sendMessage(subscriptionDto.getContact(), message);
        return String.valueOf(subscriptionDto.getContact());
    }

    @Transactional
    @Override
    public Object subscribe(Object subscriptionObject) {
        SmsSubscriptionDto subscriptionDto = (SmsSubscriptionDto) subscriptionObject;
        BigDecimal lookUpCost = notificatorsService.getLookUpPrice(getNotificationType().getCode());
        LookupResponseDto dto;
        try {
            pay(
                    lookUpCost,
                    subscriptionDto.getUserId(),
                    getNotificationType().name().concat(":").concat(NotificationPayEventEnum.LOOKUP.name()),
                    NotificationPayEventEnum.LOOKUP);
            dto = smsService.getLookup(subscriptionDto.getContact());
        } catch (InvalidRefNumberException e) {
            throw new ServiceUnavailableException();
        } catch (InsuficcienceServiceBalanceException e) {
            sendAlertMessage();
            throw new ServiceUnavailableException();
        }
        if (dto.isOperable()) {
            this.createOrUpdate(subscriptionDto);
        } else {
            throw new UnoperableNumberException(dto);
        }
        return dto;
    }

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.SMS;
    }

    @Transactional
    private BigDecimal pay(BigDecimal price, int userId, String description, NotificationPayEventEnum payEventEnum) {
        UserRole role = userService.getUserRoleFromDB(userId);
        BigDecimal fee = notificatorsService.getFeePrice(getNotificationType().getCode(), role.getRole(), payEventEnum);
        BigDecimal totalAmount = doAction(price, fee, ActionType.ADD);
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

    private void sendAlertMessage() {
        Email email = new Email();
        email.setFrom("sell@exrates.top");
        email.setTo("sell@exrates.top");
        email.setMessage("Insuficcience Service Balance Exception on 1s2u numbers lookup, need to refund balance!");
        email.setSubject("Allert! InsuficcienceServiceBalanceException");
        sendMailService.sendInfoMail(email);
    }

    private void createOrUpdate(SmsSubscriptionDto dto) {
        if (getByUserId(dto.getUserId()) == null) {
            subscriptionDao.create(dto);
        } else {
            subscriptionDao.update(dto);
        }
    }

    public SmsSubscriptionDto getByUserId(int userId) {
        return subscriptionDao.getByUserId(userId);
    }
}
