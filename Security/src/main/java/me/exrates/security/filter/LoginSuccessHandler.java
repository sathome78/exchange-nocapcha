package me.exrates.security.filter;

import me.exrates.model.dto.UserIpDto;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserIpState;
import me.exrates.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Valk on 28.04.2016.
 */
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger LOGGER = LogManager.getLogger(LoginSuccessHandler.class);

    @Autowired
    MessageSource messageSource;
    @Autowired
    LocaleResolver localeResolver;
    private String successUrl;
    @Autowired
    private UserService userService;

    public LoginSuccessHandler(String successUrl) {
        this.successUrl = successUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            User principal = (User) authentication.getPrincipal();
            Locale locale = new Locale(userService.getPreferedLang(userService.getIdByEmail(principal.getUsername())));
            localeResolver.setLocale(request, response, locale);
        /**/
            request.getSession().removeAttribute("errorNoty");
            request.getSession().removeAttribute("successNoty");
        /**/
            String email = authentication.getName();
            String ip = request.getHeader("X-FORWARDED-FOR");
            if (ip == null) {
                ip = request.getRemoteHost();
            }
            UserIpDto userIpDto = userService.getUserIpState(email, ip);
            if (userIpDto.getUserIpState() != UserIpState.CONFIRMED) {
                authentication.setAuthenticated(false);
            /**/
                if (userIpDto.getUserIpState() == UserIpState.NEW) {
                    userService.insertIp(email, ip);
                }
                me.exrates.model.User u = new me.exrates.model.User();
                u.setId(userIpDto.getUserId());
                u.setEmail(email);
                u.setIp(ip);
                String rootUrl = request.getScheme() + "://" + request.getServerName() +
                        ":" + request.getServerPort();
                userService.sendEmailWithToken(u, TokenType.CONFIRM_NEW_IP, rootUrl + "/newIpConfirm", "emailsubmitnewip.subject", "emailsubmitnewip.text", locale);
            /**/
                request.getSession().setAttribute("errorNoty", messageSource.getMessage("login.newip", null, locale));
            /**/
                response.sendRedirect("/login");
                return;
            } else {
                userService.setLastRegistrationDate(userIpDto.getUserId(), ip);
            }
            response.sendRedirect(successUrl);
        } catch (Exception e) {
            LOGGER.error(e);
            authentication.setAuthenticated(false);
        }
    }
}
