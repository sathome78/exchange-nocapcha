package me.exrates.security.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by OLEG on 11.10.2016.
 */
public class QRAuthorizationFilter extends GenericFilterBean {

    private SessionAuthenticationStrategy authenticationStrategy = new NullAuthenticatedSessionStrategy();


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
            HttpSession session = httpServletRequest.getSession();
            UserDetails userDetails = (UserDetails) session.getAttribute("USER_DETAIL_TOKEN");
            if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Object mutex = WebUtils.getSessionMutex(session);
                synchronized (mutex) {
                    session.removeAttribute("USER_DETAIL_TOKEN");
                    session.setAttribute("QR_LOGGED_IN", true);
                }
                Authentication auth = new PreAuthenticatedAuthenticationToken(userDetails,
                        userDetails.getUsername(), userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                authenticationStrategy.onAuthentication(auth, httpServletRequest, ((HttpServletResponse)response));
            }

        }
        chain.doFilter(request, response);
    }

    public SessionAuthenticationStrategy getAuthenticationStrategy() {
        return authenticationStrategy;
    }

    public void setAuthenticationStrategy(SessionAuthenticationStrategy authenticationStrategy) {
        this.authenticationStrategy = authenticationStrategy;
    }
}
