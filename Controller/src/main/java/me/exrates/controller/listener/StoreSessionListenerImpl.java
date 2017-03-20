package me.exrates.controller.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by OLEG on 11.10.2016.
 */
public class StoreSessionListenerImpl implements StoreSessionListener {

    private static final Logger LOGGER = LogManager.getLogger(StoreSessionListenerImpl.class);

    private static Map<String, HttpSession> sessionStorage = new ConcurrentHashMap<>();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        sessionStorage.put(se.getSession().getId(), se.getSession());
        LOGGER.debug(String.format("created session: %s, total registered: %s", se.getSession().getId(), sessionStorage.size()));
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        sessionStorage.remove(se.getSession().getId());
        LOGGER.debug(String.format("destroyed session: %s, total registered: %s", se.getSession().getId(), sessionStorage.size()));
    }

    @Override
    public Optional<HttpSession> getSessionById(String sessionId) {
        return Optional.ofNullable(sessionStorage.get(sessionId));
    }

    @Override
    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
        sessionStorage.remove(oldSessionId);
        sessionStorage.put(event.getSession().getId(), event.getSession());
    }
}
