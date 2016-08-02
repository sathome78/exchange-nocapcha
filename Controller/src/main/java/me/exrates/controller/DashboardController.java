package me.exrates.controller;

import com.captcha.botdetect.web.servlet.Captcha;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.UserRole;
import me.exrates.security.filter.NotVerifiedCaptchaError;
import me.exrates.security.filter.VerifyReCaptchaSec;
import me.exrates.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@Controller
@PropertySource("classpath:/captcha.properties")
public class DashboardController {
    @Autowired
    OrderService orderService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    DashboardService dashboardService;

    @Autowired
    UserService userService;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    CommissionService commissionService;

    @Autowired
    WalletService walletService;

    @Autowired
    RegisterFormValidation registerFormValidation;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @Autowired
    VerifyReCaptchaSec verifyReCaptcha;

    @Value("${captcha.type}")
    String CAPTCHA_TYPE;

    @RequestMapping(value = {"/dashboard/locale"})
    @ResponseBody
    public void localeSwitcherCommand(Principal principal, HttpServletRequest request) {
        if (principal != null) {
            userService.setPreferedLang(userService.getIdByEmail(principal.getName()), localeResolver.resolveLocale(request));
        }
        request.getSession();
    }

    @RequestMapping(value = "/forgotPassword")
    public ModelAndView forgotPassword() {
        ModelAndView model = new ModelAndView();
        model.addObject("user", new User());
        model.addObject("captchaType", CAPTCHA_TYPE);
        model.setViewName("forgotPassword");

        return model;
    }

    @RequestMapping(value = "/forgotPassword/submit", method = RequestMethod.POST)
    public ModelAndView forgotPasswordSubmit(@ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {
        switch (CAPTCHA_TYPE) {
            case "BOTDETECT": {
                String captchaId = request.getParameter("captchaId");
                Captcha captcha = Captcha.load(request, captchaId);
                String captchaCode = request.getParameter("captchaCode");
                if (!captcha.validate(captchaCode)) {
                    String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
                    ModelAndView modelAndView = new ModelAndView("/forgotPassword", "user", user);
                    modelAndView.addObject("captchaType", CAPTCHA_TYPE);
                    modelAndView.addObject("cpch", correctCapchaRequired);
                    return modelAndView;
                }
                break;
            }
            case "RECAPTCHA": {
                String recapchaResponse = request.getParameter("g-recaptcha-response");
                if (!verifyReCaptcha.verify(recapchaResponse)) {
                    String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
                    ModelAndView modelAndView = new ModelAndView("/forgotPassword", "user", user);
                    modelAndView.addObject("captchaType", CAPTCHA_TYPE);
                    modelAndView.addObject("cpch", correctCapchaRequired);
                    return modelAndView;
                }
                break;
            }
        }
        /**/
        registerFormValidation.validateEmail(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.addObject("user", user);
            model.addObject("captchaType", CAPTCHA_TYPE);
            model.setViewName("/forgotPassword");
            return model;
        }
        String email = user.getEmail();
        user = userService.findByEmail(email);
        UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
        updateUserDto.setEmail(email);
        userService.update(updateUserDto, true, localeResolver.resolveLocale(request));
        /**/
        request.getSession().setAttribute("successNoty", messageSource.getMessage("admin.changePasswordSendEmail", null, localeResolver.resolveLocale(request)));
        model.setViewName("redirect:/dashboard");
        /**/
        return model;
    }

    @RequestMapping(value = "/resetPasswordConfirm")
    public ModelAndView resetPasswordConfirm(@RequestParam("token") String token) {
        ModelAndView model = new ModelAndView();
        try {
            int userId = userService.verifyUserEmail(token);
            User user = userService.getUserById(userId);
            model.addObject("user", user);
            model.addObject("captchaType", CAPTCHA_TYPE);
            model.setViewName("updatePassword");
            org.springframework.security.core.userdetails.User userSpring = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), false, false, false, false,
                    userDetailsService.loadUserByUsername(user.getEmail()).getAuthorities());
            Collection<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
            authList.add(new SimpleGrantedAuthority(UserRole.ROLE_CHANGE_PASSWORD.name()));
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userSpring, null, authList);
            SecurityContextHolder.getContext().setAuthentication(auth);
            user.setPassword(null);
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
        }
        return model;
    }

    @RequestMapping(value = "/dashboard/updatePassword", method = RequestMethod.POST)
    public ModelAndView updatePassword(@ModelAttribute User user, BindingResult result, HttpServletRequest request, Principal principal) {
        switch (CAPTCHA_TYPE) {
            case "BOTDETECT": {
                String captchaId = request.getParameter("captchaId");
                Captcha captcha = Captcha.load(request, captchaId);
                String captchaCode = request.getParameter("captchaCode");
                if (!captcha.validate(captchaCode)) {
                    String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
                    ModelAndView modelAndView = new ModelAndView("/updatePassword", "user", user);
                    modelAndView.addObject("captchaType", CAPTCHA_TYPE);
                    modelAndView.addObject("cpch", correctCapchaRequired);
                    return modelAndView;
                }
                break;
            }
            case "RECAPTCHA": {
                String recapchaResponse = request.getParameter("g-recaptcha-response");
                if (!verifyReCaptcha.verify(recapchaResponse)) {
                    String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
                    ModelAndView modelAndView = new ModelAndView("/updatePassword", "user", user);
                    modelAndView.addObject("captchaType", CAPTCHA_TYPE);
                    modelAndView.addObject("cpch", correctCapchaRequired);
                    return modelAndView;
                }
                break;
            }
        }
        /**/
        registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("/updatePassword", "user", user);
            modelAndView.addObject("captchaType", CAPTCHA_TYPE);
            return modelAndView;
        } else {
            String password = user.getPassword();
            ModelAndView model = new ModelAndView();
            UpdateUserDto updateUserDto = new UpdateUserDto(userService.findByEmail(principal.getName()).getId());
            updateUserDto.setPassword(password);
            userService.updateUserByAdmin(updateUserDto);
            /**/
            new SecurityContextLogoutHandler().logout(request, null, null);
            model.setViewName("redirect:/dashboard");
            return model;
        }
    }

    @RequestMapping(value = "/forgotPassword/submitUpdate", method = RequestMethod.POST)
    public ModelAndView submitUpdate(@ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {
        registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
        /**/
        if (result.hasErrors()) {
            model.addObject("user", user);
            model.addObject("captchaType", CAPTCHA_TYPE);
            model.setViewName("updatePassword");
            return model;
        } else {
            UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
            updateUserDto.setPassword(user.getPassword());
            userService.updateUserByAdmin(updateUserDto);
            model.setViewName("redirect:/dashboard");
        }
        /**/
        return model;
    }

    @RequestMapping(value = "/termsAndConditions", method = RequestMethod.GET)
    public ModelAndView termsAndConditions() {
        return new ModelAndView("/globalPages/termsAndConditions", "captchaType", CAPTCHA_TYPE);
    }

    @RequestMapping(value = "/privacyPolicy", method = RequestMethod.GET)
    public ModelAndView privacyPolicy() {
        return new ModelAndView("/globalPages/privacyPolicy", "captchaType", CAPTCHA_TYPE);
    }

    @RequestMapping(value = "/returnPolicy", method = RequestMethod.GET)
    public ModelAndView returnPolicy() {
        return new ModelAndView("/globalPages/returnPolicy", "captchaType", CAPTCHA_TYPE);
    }
}


