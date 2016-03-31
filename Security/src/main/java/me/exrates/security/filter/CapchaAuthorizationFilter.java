package me.exrates.security.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Valk on 31.03.16.
 */
public class CapchaAuthorizationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @Autowired
    VerifyReCaptchaSec verifyReCaptchaSec;

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String recapchaResponse = request.getParameter("g-recaptcha-response");
        if ((recapchaResponse!=null) && !verifyReCaptchaSec.verify(recapchaResponse)) {
            String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
            throw new UsernameNotFoundException("capcha error");
        }


        return super.attemptAuthentication(request, response);
    }
}