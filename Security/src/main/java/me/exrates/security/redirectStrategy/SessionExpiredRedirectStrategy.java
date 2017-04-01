package me.exrates.security.redirectStrategy;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by maks on 01.04.2017.
 */
@Log4j2
public class SessionExpiredRedirectStrategy implements RedirectStrategy{

    public SessionExpiredRedirectStrategy() {
    }

    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        log.error("session expired, redirect {}", url);
        response.sendRedirect(url);

    }
}
