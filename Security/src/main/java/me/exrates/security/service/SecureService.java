package me.exrates.security.service;

import lombok.extern.log4j.Log4j2;
import me.exrates.security.exception.PinCodeCheckNeedException;
import me.exrates.security.filter.CapchaAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.apache.axis.attachments.MimeUtils.filter;

/**
 * Created by Maks on 28.09.2017.
 */
@Log4j2
@Component
@PropertySource("classpath:session.properties")
public class SecureService {

    private @Value("${session.checkPinParam}") String checkPinParam;
    private String authenticationParamName = "authentication";
    private @Value("${session.passwordParam}") String passwordParam;

    @Autowired
    private SecureNotificationService notificationService;


    public void resolveLoginAuth(HttpServletRequest request, Authentication authentication, CapchaAuthorizationFilter filter) {
        NotinotificationService.notifyUserLogin(authentication.getName());
        request.getSession().setAttribute(checkPinParam, "");
        request.getSession().setAttribute(authenticationParamName, authentication);
        request.getSession().setAttribute(passwordParam, request.getParameter(filter.getPasswordParameter()));
        authentication.setAuthenticated(false);
        throw new PinCodeCheckNeedException("");
    }
}
