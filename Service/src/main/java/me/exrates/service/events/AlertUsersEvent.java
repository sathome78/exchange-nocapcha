package me.exrates.service.events;

import me.exrates.model.dto.AlertDto;
import org.springframework.context.ApplicationEvent;

/**
 * Created by Maks on 14.12.2017.
 */
public class AlertUsersEvent  extends ApplicationEvent {

    public AlertUsersEvent(AlertDto alertDto) {
        super(alertDto);
    }
}
