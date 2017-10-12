package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificatorPriceDao;
import me.exrates.dao.NotificatorsDao;
import me.exrates.model.dto.NotificationPayEventEnum;
import me.exrates.model.dto.Notificator;
import me.exrates.model.enums.NotificationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Maks on 06.10.2017.
 */
@Log4j2(topic = "message_notify")
@Service
public class NotificatorsServiceImpl implements NotificatorsService {


    @Autowired
    private NotificatorsDao notificatorsDao;
    @Autowired
    private NotificatorPriceDao notificatorPriceDao;

    @Autowired
    Map<String, NotificatorService> notificatorsMap;

    @Override
    public NotificatorService getNotificationService(Integer notificatorId) {
        Notificator notificator = Optional.ofNullable(this.getById(notificatorId))
                .orElseThrow(() -> new RuntimeException(String.valueOf(notificatorId)));
        return notificatorsMap.get(notificator.getBeanName());
    }

    @Override
    public NotificatorService getNotificationServiceByBeanName(String beanName) {
        return notificatorsMap.get(beanName);
    }

    @Override
    public Map<Integer, Object> getSubscriptions(int userId) {
        Map<Integer, Object> subscrMap = new HashMap<>();
        Arrays.asList(NotificationTypeEnum.values()).forEach(p->{
            if (p.isNeedSubscribe()) {
                NotificatorService service = this.getNotificationService(p.getCode());
                subscrMap.put(p.getCode(), service.getSubscriptionByUserId(userId));
            }
        });
        return subscrMap;
    }

    @Override
    public Notificator getById(int id){
        return notificatorsDao.getById(id);
    }

    @Override
    public BigDecimal getMessagePrice(int notificatorId) {
        return notificatorsDao.getMessagePrice(notificatorId);
    }

    @Override
    public BigDecimal getFeePrice(int notificatorId, int roleId, NotificationPayEventEnum payEventEnum) {
        return notificatorPriceDao.getMessagePrice(notificatorId, roleId, payEventEnum);
    }

    @Override
    public BigDecimal getSubscriptionPrice(int notificatorId) {
        return notificatorsDao.getSubscriptionPrice(notificatorId);
    }

    @Override
    public BigDecimal getLookUpPrice(int notificatorId) {
        return notificatorsDao.getLookUpPrice(notificatorId);
    }
}
