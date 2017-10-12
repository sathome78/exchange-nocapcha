package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.TelegramSubscriptionDao;
import me.exrates.model.Currency;
import me.exrates.model.dto.NotificationPayEventEnum;
import me.exrates.model.dto.TelegramSubscription;
import me.exrates.model.enums.*;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.MessageUndeliweredException;
import me.exrates.service.exception.PaymentException;
import me.exrates.service.exception.TelegramSubscriptionException;
import me.exrates.service.notifications.telegram.TelegramBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.StringJoiner;

import static me.exrates.model.util.BigDecimalProcessing.doAction;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;

/**
 * Created by Maks on 29.09.2017.
 */
@Log4j2(topic = "message_notify")
@Component("telegramNotificatorServiceImpl")
public class TelegramNotificatorServiceImpl implements NotificatorService, Subscribable {

    @Autowired
    private TelegramSubscriptionDao subscribtionDao;
    @Autowired
    private UserService userService;
    @Autowired
    private TelegramBotService telegramBotService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private NotificatorsService notificatorsService;


    private Currency currency;
    private static final String CURRENCY_NAME_FOR_PAY = "USD";

    @PostConstruct
    private void init() {
        currency = currencyService.findByName(CURRENCY_NAME_FOR_PAY);
    }

    @Transactional
    @Override
    public Object subscribe(Object subscribeData) {
        TelegramSubscription subscriptionDto = (TelegramSubscription)subscribeData;
        String[] data = (subscriptionDto.getRawText()).split("\\:");
        String email = data[0];
        String code = data[1];
        Optional<TelegramSubscription> subscriptionOptional = subscribtionDao.getSubscribtionByCodeAndEmail(code, email);
        TelegramSubscription subscription = subscriptionOptional.orElseThrow(TelegramSubscriptionException::new);
        TelegramSubscriptionStateEnum nextState = subscription.getSubscriptionState().getNextState();
        if (nextState == null) {
            /*set New account for subscription if allready subscribed*/
            subscription.setChatId(subscriptionDto.getChatId());
            subscription.setUserAccount(subscriptionDto.getUserAccount());
            subscription.setCode(null);
        } else if (subscription.getSubscriptionState().isFinalState()) {
            subscription.setSubscriptionState(nextState);
            subscription.setChatId(subscriptionDto.getChatId());
            subscription.setUserAccount(subscriptionDto.getUserAccount());
            subscription.setCode(null);
        }
        subscribtionDao.updateSubscription(subscription);
        return null;
    }

    @Override
    public Object getSubscriptionByUserId(int userId) {
        return subscribtionDao.getSubscribtionByUserId(userId);
    }

    @Transactional
    public String createSubscription(String userEmail) {
        String code = generateCode(userEmail);
        int id = subscribtionDao.create(TelegramSubscription.builder()
                .id(userService.getIdByEmail(userEmail))
                .subscriptionState(TelegramSubscriptionStateEnum.getBeginState())
                .code(new StringJoiner(":", userEmail, code).toString()).build());
        payForSubscribe(
                BigDecimal.ZERO,
                userEmail,
                OperationType.BUY_NOTIFICATION_SUBSCRIPTION,
                getNotificationType().name().concat(":").concat(NotificationPayEventEnum.SUBSCRIBE.name()),
                NotificationPayEventEnum.SUBSCRIBE);
        return code;
    }

    @Transactional
    public String getNewCode(String userEmail) {
        String code = generateCode(userEmail);
        subscribtionDao.updateCode(code, userService.getIdByEmail(userEmail));
        return code;
    }

    @Transactional
    @Override
    public String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException {
        Optional<TelegramSubscription> subscriptionOptional = Optional.ofNullable(subscribtionDao.getSubscribtionByUserId(userService.getIdByEmail(userEmail)));
        TelegramSubscription subscription = subscriptionOptional.orElseThrow(MessageUndeliweredException::new);
        if (!subscription.getSubscriptionState().isFinalState()) {
            throw new MessageUndeliweredException();
        }
        telegramBotService.sendMessage(subscription.getChatId(), message);
        return subscription.getUserAccount();
    }

    private String generateCode(String email) {
        return new StringJoiner(":", email, String.valueOf(100000000 + new Random().nextInt(100000000))).toString();
    }

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.TELEGRAM;
    }

    @Transactional
    private BigDecimal payForSubscribe(BigDecimal amount, String userEmail, OperationType operationType,
                                 String description, NotificationPayEventEnum payEventEnum) {
        int userId = userService.getIdByEmail(userEmail);
        UserRole role = userService.getUserRoleFromDB(userEmail);
        BigDecimal fee = notificatorsService.getFeePrice(getNotificationType().getCode(), role.getRole(), payEventEnum);
        BigDecimal totalAmount = doAction(amount, fee, ActionType.ADD);
        WalletOperationData walletOperationData = new WalletOperationData();
        walletOperationData.setOperationType(operationType);
        walletOperationData.setWalletId(walletService.getWalletId(userId, currency.getId()));
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
