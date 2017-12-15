package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.UserAlertsDao;
import me.exrates.model.dto.AlertDto;
import me.exrates.model.enums.AlertType;
import me.exrates.service.UsersAlertsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;

/**
 * Created by Maks on 13.12.2017.
 */
@Log4j2
@Service
public class UsersAlertsServiceImpl implements UsersAlertsService {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserAlertsDao userAlertsDao;

    private static final int MINUTES_FOR_OUTDATE = 30;

    @Override
    public List<AlertDto> getActiveAlerts(Locale locale) {
       List<AlertDto> list = userAlertsDao.getAlerts(true);
       completeDtos(list, locale);
       return list;
    }

    @Override
    public List<AlertDto> getAllAlerts(Locale locale) {
        List<AlertDto> list = userAlertsDao.getAlerts(false);
        completeDtos(list, locale);
        return list;
    }

    private void completeDtos(List<AlertDto> alertDtos, Locale locale) {
        alertDtos.forEach(p->{
            if (p.isEnabled()) {
                AlertType alertType = AlertType.valueOf(p.getAlertType());
                if (alertType.isNeedDateTime()) {
                    LocalTime length = p.getLenghtOfWorks();
                    p.setText(messageSource.getMessage(alertType.getMessageTmpl(),
                            new String[]{String.valueOf(length.getMinute())},
                            locale));
                } else {
                    p.setText(messageSource.getMessage(alertType.getMessageTmpl(), null, locale));
                }
            }
        });
    }

    private boolean checkForOutdate(AlertDto dto) {
        LocalTime length = dto.getLenghtOfWorks();
        return dto.getLaunchDateTime()
                .plusHours(length.getHour())
                .plusMinutes(length.getMinute())
                .plusMinutes(MINUTES_FOR_OUTDATE)
                .isBefore(LocalDateTime.now());
    }

    @Transactional
    @Override
    public void enableAlert(AlertType alertType, Duration minutes) {
        userAlertsDao.updateAlert(AlertDto
                .builder()
                .alertType(alertType.name())
                .launchDateTime(LocalDateTime.now())
                .enabled(true)
                .eventStart(LocalDateTime.now().plus(minutes))
                .build());
    }

    @Transactional
    @Override
    public void disableAlert(AlertType alertType) {
        userAlertsDao.updateAlert(AlertDto
                .builder()
                .alertType(alertType.name())
                .lenghtOfWorks(null)
                .eventStart(null)
                .launchDateTime(null)
                .enabled(false)
                .build());
    }
}
