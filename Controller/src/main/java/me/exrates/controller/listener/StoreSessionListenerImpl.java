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
        LOGGER.debug("Created session: " + se.getSession().getId());
        sessionStorage.put(se.getSession().getId(), se.getSession());
        LOGGER.debug("Registered sessions: " + sessionStorage.size());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        LOGGER.debug("Destroyed session: " + se.getSession().getId());
        sessionStorage.remove(se.getSession().getId());
        LOGGER.debug("Registered sessions: " + sessionStorage.size());
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
