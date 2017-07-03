package me.exrates.security.filter;

import com.captcha.botdetect.web.servlet.Captcha;
import lombok.extern.log4j.Log4j2;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.exception.PinCodeCheckNeedException;
import me.exrates.service.UserService;
import org.apache.commons.lang3.StringUtils;
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
    VerifyReCaptchaSec verifyReCaptchaSec;
    @Autowired
    private UserService userService;

    private @Value("${session.checkPinParam}") String checkPinParam;
    private @Value("${session.pinParam}") String pinParam;
    private @Value("${session.passwordParam}") String passwordParam;


    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        HttpSession session = request.getSession(false);
        /*----------------------------*/
        String authenticationParamName = "authentication";
        if (session.getAttribute(checkPinParam) != null && request.getParameter(pinParam) != null
                         && request.getParameter(super.getUsernameParameter()) == null
                         && request.getParameter(super.getPasswordParameter()) == null
                         && session.getAttribute(authenticationParamName) != null) {
            Authentication authentication = (Authentication)session.getAttribute(authenticationParamName);
            User principal = (User) authentication.getPrincipal();
            if (!userService.checkPin(principal.getUsername(), request.getParameter(pinParam))) {
                userService.createSendAndSaveNewPinForUser(principal.getUsername());
                throw new IncorrectPinException("");
            }
            return attemptAuthentication(principal.getUsername(),
                    String.valueOf(session.getAttribute(passwordParam)),request, response);
        } else {
            String captchaType = request.getParameter("captchaType");
            if (captchaType == null) {
                throw new NotVerifiedCaptchaError( messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request)));
            }
            switch (captchaType) {
                case "BOTDETECT": {
                    String captchaId = request.getParameter("captchaId");
                    Captcha captcha = Captcha.load(request, captchaId);
                    String captchaCode = request.getParameter("captchaCode");
                    if (!captcha.validate(captchaCode)) {
                        String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
                        throw new NotVerifiedCaptchaError(correctCapchaRequired);
                    }
                    break;
                }
                case "RECAPTCHA": {
                    String recapchaResponse = request.getParameter("g-recaptcha-response");
                    if ((recapchaResponse != null) && !verifyReCaptchaSec.verify(recapchaResponse)) {
                        String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
                        throw new NotVerifiedCaptchaError(correctCapchaRequired);
                    }
                    break;
                }
            }
        }
        /*---------------*/
        Authentication authentication = super.attemptAuthentication(request, response);
        /*-------------------*/
        User principal = (User) authentication.getPrincipal();
        if (userService.getUse2Fa(principal.getUsername())) {
            userService.createSendAndSaveNewPinForUser(principal.getUsername());
            request.getSession().setAttribute(checkPinParam, "");
            request.getSession().setAttribute(authenticationParamName, authentication);
            request.getSession().setAttribute(passwordParam, request.getParameter(super.getPasswordParameter()));
            authentication.setAuthenticated(false);
            throw new PinCodeCheckNeedException("");
        }
        /*----------------------*/
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