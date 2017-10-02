package me.exrates.security.service;

import com.sun.corba.se.spi.resolver.LocalResolver;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.security.exception.PinCodeCheckNeedException;
import me.exrates.security.filter.CapchaAuthorizationFilter;
import me.exrates.service.UserService;
import me.exrates.service.notifications.NotificationService;
import me.exrates.service.util.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
    private NotificationService notificationService;
    @Autowired
    private UserService userService;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private MessageSource messageSource;


    public void checkLoginAuth(HttpServletRequest request, Authentication authentication, CapchaAuthorizationFilter filter) {
        if (userService.isGlobal2FaActive() || userService.getUse2Fa(authentication.getName())) {
            String result = sendLoginMessage(authentication.getName(), request);
            request.getSession().setAttribute(checkPinParam, "");
            request.getSession().setAttribute(authenticationParamName, authentication);
            request.getSession().setAttribute(passwordParam, request.getParameter(filter.getPasswordParameter()));
            authentication.setAuthenticated(false);
            throw new PinCodeCheckNeedException(result);
        }
    }

    public String sendLoginMessage(String email, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String pin = userService.createOrUpdatePinForUserForEvent(email, NotificationMessageEventEnum.LOGIN);
        String messageText = messageSource.getMessage("message.pincode.forlogin",
                new String[]{LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), IpUtils.getClientIpAddress(request), pin}, locale);
        return notificationService.notifyUser(email, messageText, NotificationMessageEventEnum.LOGIN);
    }

    public void checkWithdrawAuth(HttpServletRequest request, String email) {
      /*  if (userService.isWithdraw2faActive(email)) {
            Locale locale = localeResolver.resolveLocale(request);
            String pin = userService.createSendAndSaveNewPinForUser(email);
            String messageText = messageSource.getMessage("message.pincode.forlogin",
                    new String[]{LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")), IpUtils.getClientIpAddress(request), pin}, locale);
            String result = notificationService.notifyUser(authentication.getName(), messageText, NotificationMessageEventEnum.LOGIN);
            request.getSession().setAttribute(checkPinParam, "");
            request.getSession().setAttribute(authenticationParamName, authentication);
            request.getSession().setAttribute(passwordParam, request.getParameter(filter.getPasswordParameter()));
            authentication.setAuthenticated(false);
            throw new PinCodeCheckNeedException(result);
        }*/
    }



}
