package me.exrates.security.filter;

import com.captcha.botdetect.web.servlet.Captcha;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
    @Autowired
    private UserService userService;

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String captchaType = request.getParameter("captchaType");
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
        HttpSession session = request.getSession();
        if (session.getAttribute("checkPin") != null) {
            if (!userService.checkPin(String.valueOf(session.getAttribute("username")), String.valueOf(request.getAttribute("pin")))) {
                throw new IncorrectPinException();
            }
            request.setAttribute(super.getUsernameParameter(), "");
        }
        return super.attemptAuthentication(request, response);
    }
}