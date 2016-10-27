package me.exrates.controller.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;
import java.util.Optional;

/**
 * Created by OLEG on 11.10.2016.
 */
public interface StoreSessionListener extends HttpSessionListener, HttpSessionIdListener {

    Optional<HttpSession> getSessionById(String sessionId);
}
