package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificatorPriceDao;
import me.exrates.dao.NotificatorsDao;
import me.exrates.model.dto.NotificationPayEventEnum;
import me.exrates.model.dto.Notificator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
