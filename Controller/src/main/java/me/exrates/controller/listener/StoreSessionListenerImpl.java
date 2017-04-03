package me.exrates.controller.listener;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.SessionParams;
import me.exrates.service.SessionParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by OLEG on 11.10.2016.
 */
@Log4j2
@PropertySource("classpath:session.properties")
public class StoreSessionListenerImpl implements StoreSessionListener {

    @Autowired
    private SessionParamsService sessionParamsService;
    @Autowired
    private

    private @Value("${session.lifeTypeParamName}") String sessionLifeTimeParamName = "sessionLifeTypeId";
    private @Value("${session.timeParamName}") String sessionTimeMinutesParamName = "sessionTimeMinutesParamName";

    private static Map<String, HttpSession> sessionStorage = new ConcurrentHashMap<>();



    @Override
    public void sessionCreated(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        SessionParams params = sessionParamsService.determineSessionParams();
        log.error("params in listener{}, is new {}", params.toString(), session.isNew());
        session.setMaxInactiveInterval(0);
        session.setAttribute(sessionTimeMinutesParamName, params.getSessionTimeMinutes());
        session.setAttribute(sessionLifeTimeParamName, params.getSessionLifeTypeId());
        sessionStorage.put(session.getId(), session);
        log.debug(String.format("created session: %s, total registered: %s", se.getSession().getId(), sessionStorage.size()));
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {

        sessionStorage.remove(se.getSession().getId());
        log.debug(String.format("destroyed session: %s, total registered: %s", se.getSession().getId(), sessionStorage.size()));
    }

    @Override
    public Optional<HttpSession> getSessionById(String sessionId) {
        return Optional.ofNullable(sessionStorage.get(sessionId));
    }

    @Override
    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        sessionStorage.remove(oldSessionId);
        HttpSession session = event.getSession();
        SessionParams params = sessionParamsService.determineSessionParams();
        session.setMaxInactiveInterval(0);
        session.setAttribute(sessionTimeMinutesParamName, params.getSessionTimeMinutes());
        session.setAttribute(sessionLifeTimeParamName, params.getSessionLifeTypeId());
        sessionStorage.put(session.getId(), session);
    }

}
