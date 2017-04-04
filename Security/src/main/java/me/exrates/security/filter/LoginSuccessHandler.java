package me.exrates.security.filter;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.SessionLifeTimeType;
import me.exrates.model.SessionParams;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.enums.SessionLifeTypeEnum;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserIpState;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Valk on 28.04.2016.
 */
@Log4j2
@PropertySource("classpath:session.properties")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {


    @Autowired
    private SessionParamsService sessionParamsService;
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
        sessionParamsService.setSessionLifeParams(request);
        try {
            User principal = (User) authentication.getPrincipal();
            log.info("Authentication succeeded for user: " + principal.getUsername());

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

            if (userIpDto.getUserIpState() == UserIpState.NEW) {
                userService.insertIp(email, ip);
                me.exrates.model.User u = new me.exrates.model.User();
                u.setId(userIpDto.getUserId());
                u.setEmail(email);
                u.setIp(ip);
                userService.sendUnfamiliarIpNotificationEmail(u, "emailsubmitnewip.subject", "emailsubmitnewip.text", locale);
            }
            userService.setLastRegistrationDate(userIpDto.getUserId(), ip);
            response.sendRedirect(successUrl);
            return;


        } catch (Exception e) {
            log.error(e);
            authentication.setAuthenticated(false);
        }
    }


}
