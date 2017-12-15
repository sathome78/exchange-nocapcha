package me.exrates.service;

import me.exrates.model.dto.AlertDto;
import me.exrates.model.enums.AlertType;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

/**
 * Created by Maks on 13.12.2017.
 */
public interface UsersAlertsService {

    List<AlertDto> getActiveAlerts(Locale locale);

    List<AlertDto> getAllAlerts(Locale locale);

    @Transactional
    void enableAlert(AlertType alertType, Duration minutes);

    @Transactional
    void disableAlert(AlertType alertType);
}
