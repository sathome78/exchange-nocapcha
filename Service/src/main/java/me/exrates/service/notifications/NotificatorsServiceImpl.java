package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificatorsDao;
import me.exrates.model.dto.Notificator;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * Created by Maks on 06.10.2017.
 */
@Log4j2
@Service
public class NotificatorsServiceImpl implements NotificatorsService {


    @Autowired
    private NotificatorsDao notificatorsDao;

    @Override
    public Notificator getById(int id){
        return notificatorsDao.getById(id);
    }

    @Override
    public BigDecimal getMessagePrice(int notificatorId) {
        return notificatorsDao.getMessagePrice(notificatorId);
    }

    public BigDecimal getSubscriptionPrice(int notificatorId) {
        return notificatorsDao.getSubscriptionPrice(notificatorId);
    }
}
