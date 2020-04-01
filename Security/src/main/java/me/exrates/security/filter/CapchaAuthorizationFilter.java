package me.exrates.security.filter;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.PinDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.UserStatus;
import me.exrates.security.exception.BannedIpException;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.exception.UnconfirmedUserException;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.service.SecureService;
import me.exrates.service.UserService;
import me.exrates.service.util.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by Valk on 31.03.16.
 */
@Log4j2
@PropertySource("classpath:session.properties")
public class CapchaAuthorizationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @Autowired
    private UserService userService;
    @Autowired
    private SecureService secureServiceImpl;

    @Autowired
    private IpBlockingService ipBlockingService;

    private @Value("${session.checkPinParam}") String checkPinParam;
    private @Value("${session.pinParam}") String pinParam;
    private @Value("${session.passwordParam}") String passwordParam;
    private String authenticationParamName = "authentication";


    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        HttpSession session = request.getSession(false);
        /*----------------------------*/
        String ipAddress = IpUtils.getClientIpAddress(request);
        try {
//            ipBlockingService.checkIp(ipAddress, IpTypesOfChecking.LOGIN);
        } catch (BannedIpException e) {
            long banDuration = e.getBanDurationSeconds();
            String durationMessage;
            if (banDuration < 60 * 60) {
                String durationMinutes = String.valueOf(banDuration / 60);
                durationMessage = messageSource.getMessage("ip.ban.time.minutes", new Object[]{durationMinutes}, localeResolver.resolveLocale(request));
            } else {
                String durationHours = String.valueOf(banDuration / (60 * 60));
                durationMessage = messageSource.getMessage("ip.ban.time.hours", new Object[]{durationHours},
                        localeResolver.resolveLocale(request));
            }

            // rethrow to add localization message
            throw new BannedIpException(messageSource.getMessage("ip.ban.message", new Object[]{durationMessage},
                    localeResolver.resolveLocale(request)), banDuration);
        }

        if (session.getAttribute(checkPinParam) != null && request.getParameter(pinParam) != null
                         && request.getParameter(super.getUsernameParameter()) == null
                         && request.getParameter(super.getPasswordParameter()) == null
                         && session.getAttribute(authenticationParamName) != null) {
            Authentication authentication = (Authentication)session.getAttribute(authenticationParamName);
            User principal = (User) authentication.getPrincipal();
            if (!userService.checkPin(principal.getUsername(), request.getParameter(pinParam), NotificationMessageEventEnum.LOGIN)) {
                PinDto res = secureServiceImpl.reSendLoginMessage(request, authentication.getName(), true);
                throw new IncorrectPinException(res);
            }
            return attemptAuthentication(principal.getUsername(),
                    String.valueOf(session.getAttribute(passwordParam)),request, response);
        } else {

            String recapchaResponse = request.getParameter("g-recaptcha-response");

        }
        if(userService.findByEmail(request.getParameter("username")).getUserStatus()==UserStatus.REGISTERED){
            throw new UnconfirmedUserException(userService.findByEmail(request.getParameter("username")).getEmail());
        }
        /*---------------*/
        Authentication authentication = super.attemptAuthentication(request, response);
        /*-------------------*/
        session.setAttribute("raw_password", request.getParameter("password"));
        /*-------------------*/
        secureServiceImpl.checkLoginAuth(request, authentication, this);
        /* old impl
        User principal = (User) authentication.getPrincipal();
        if (userService.isGlobal2FaActive() || userService.getUse2Fa(principal.getUsername())) {
            userService.createSendAndSaveNewPinForUser(principal.getUsername(), request);
            request.getSession().setAttribute(checkPinParam, "");
            request.getSession().setAttribute(authenticationParamName, authentication);
            request.getSession().setAttribute(passwordParam, request.getParameter(super.getPasswordParameter()));
            authentication.setAuthenticated(false);
            throw new PinCodeCheckNeedException("");
        }*/
        return authentication;
    }


    private Authentication attemptAuthentication(String username, String password,  HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        username = username.trim();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username, password);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
