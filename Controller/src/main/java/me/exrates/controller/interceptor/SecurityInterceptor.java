package me.exrates.controller.interceptor;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.session.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserSessionService userSessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            String securityContextUserEmail = auth.getName();
            String sessionRegistryUserEmail = ((UserDetails) userSessionService.getSessionInfo(request.getSession().getId()).getPrincipal()).getUsername();
            if (!securityContextUserEmail.equalsIgnoreCase(sessionRegistryUserEmail)) {
                request.getSession().invalidate();
                auth.setAuthenticated(false);
            }
        }
        return true;
    }
}
