package me.exrates.security.filter;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.enums.UserIpState;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    private final String pinUrl = "/dashboard?pin=true";


    public LoginSuccessHandler(String successUrl) {
        this.successUrl = successUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            User principal = (User) authentication.getPrincipal();
            log.info("Authentication succeeded for user: " + principal.getUsername());
            sessionParamsService.setSessionLifeParams(request);
            if (userService.getUse2Fa(principal.getUsername()) && request.getSession().getAttribute("pinOk") == null) {
                userService.createSendAndSaveNewPinForUser(principal.getUsername());
                request.getSession().setAttribute("pinCheck", "");
                request.getSession().setAttribute("name", principal.getUsername());
                request.getSession().setAttribute("password", principal.getPassword());
                authentication.setAuthenticated(false);
                response.sendRedirect("");
            }
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
