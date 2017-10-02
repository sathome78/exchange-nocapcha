package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificationMessageLogDao;
import me.exrates.dao.NotificationMessagesDao;
import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.dao.NotificatorsDao;
import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.dto.Notificator;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.MessageUndeliweredException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.UnknownNotyPaymentTypeException;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

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


    private NotificationsUserSetting defaultSetting;

    public void init() {
        defaultSetting = NotificationsUserSetting.builder().notificatorId(1).build();
    }

    @Override
    @Transactional
    public NotificationResultDto notifyUser(final String userEmail, final String message, final NotificationMessageEventEnum event) {
        NotificationsUserSetting setting = settingsDao.getByUserAndEvent(userService.getIdByEmail(userEmail), event)
                                                        .orElse(defaultSetting);
        NotificatorService service = getNotificationService(setting.getNotificatorId());
        NotificationTypeEnum notificationTypeEnum = service.getNotificationType();
        String contactToNotify;
        BigDecimal payAmount = null;
        switch (service.getPayType()) {
            case FREE: {
                contactToNotify = service.sendMessageToUser(userEmail, message);
                break;
            }
            case PAY_FOR_EACH: {
                UserRole role = userService.getUserRoleFromDB(userEmail);
                payAmount = service.getTotalMessageCost(role);
                try {
                    payForMessage(payAmount, userEmail);
                    contactToNotify = service.sendMessageToUser(userEmail, message);
                } catch (NotEnoughUserWalletMoneyException | MessageUndeliweredException e) {
                    log.error(e);
                    contactToNotify = service.sendMessageToUser(userEmail, message);
                }
                break;
            }
            case PREPAID_LIFETIME: {
                contactToNotify = service.sendMessageToUser(userEmail, message);
                break;
            }
            default:{
                throw new UnknownNotyPaymentTypeException();
            }
        }
        messageLogDao.saveLogNotification(userEmail, payAmount, event, notificationTypeEnum);
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
    private void payForMessage(BigDecimal amount, String userEmail) {
        WalletOperationData walletOperationData = new WalletOperationData();
        walletService.walletBalanceChange()
    }


}
