package me.exrates.controller.listener;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.SessionParams;
import me.exrates.model.enums.SessionLifeTypeEnum;
import me.exrates.service.SessionParamsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.security.Principal;
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

    private  /*@Value("${session.default_session_lifetime_minutes}") */int defaultSessionLifetimeMinutes = 20;
    private /* @Value("${session.lifeTypeParamName}")*/ String sessionLifeTimeParamName = "sessionLifeTypeId";

    private static Map<String, HttpSession> sessionStorage = new ConcurrentHashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        SessionParams params = determineSessionParams();
        HttpSession session = se.getSession();
        session.setMaxInactiveInterval(params.getSessionTimeSeconds());
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
        sessionStorage.remove(oldSessionId);
        HttpSession session = event.getSession();
        SessionParams params = determineSessionParams();
        session.setMaxInactiveInterval(params.getSessionTimeSeconds());
        session.setAttribute(sessionLifeTimeParamName, params.getSessionLifeTypeId());
        sessionStorage.put(session.getId(), session);
    }

    private SessionParams determineSessionParams() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Principal principal = (Principal) authentication.getPrincipal();
            SessionParams sessionParams = sessionParamsService.getByUserEmail(principal.getName());
            if (sessionParams == null) {
                SessionParams params = getDefaultSessionPararms();
                sessionParamsService.saveOrUpdate(params, principal.getName());
                return params;
            } else {
                return sessionParams;
            }
        } else {
            return getDefaultSessionPararms();
        }
    }

    private SessionParams getDefaultSessionPararms() {
        return new SessionParams(defaultSessionLifetimeMinutes * 60, SessionLifeTypeEnum.INACTIVE_COUNT_LIFETIME.getTypeId());
    }

}
