package me.exrates.security.filter;

import com.sun.corba.se.spi.resolver.LocalResolver;
import me.exrates.service.DashboardService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by Valk on 28.04.2016.
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private String successUrl;

    @Autowired
    private UserService userService;

    @Autowired
    private LocaleResolver localeResolver;

    public LoginSuccessHandler(String successUrl) {
        this.successUrl = successUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User principal = (User) authentication.getPrincipal();
        String locale = userService.getPreferedLang(userService.getIdByEmail(principal.getUsername()));
        localeResolver.setLocale(request, response, new Locale(locale));
        response.sendRedirect(successUrl);
    }
}
