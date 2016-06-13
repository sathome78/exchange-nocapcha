package me.exrates.controller;

import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.*;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.security.filter.VerifyReCaptchaSec;
import me.exrates.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
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

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
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

    @RequestMapping(value = {"/dashboard/locale"})
    public void localeSwitcherCommand(Principal principal, HttpServletRequest request) {
        if (principal != null) {
            userService.setPreferedLang(userService.getIdByEmail(principal.getName()), localeResolver.resolveLocale(request));
        }
        request.getSession(true);
    }

    @RequestMapping(value = {"/dashboard"})
    public ModelAndView dashboard(@ModelAttribute CurrencyPair currencyPair, @RequestParam(required = false) String errorNoty, @RequestParam(required = false) String successNoty, HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        if (successNoty == null) {
            successNoty = (String)request.getSession().getAttribute("successNoty");
            request.getSession().removeAttribute("successNoty");
        }
        model.addObject("successNoty", successNoty);
        if (errorNoty == null) {
            errorNoty = (String)request.getSession().getAttribute("errorNoty");
            request.getSession().removeAttribute("errorNoty");
        }
        model.addObject("errorNoty", errorNoty);
        model.setViewName("dashboard");
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        model.addObject(orderCreateDto);
        return model;
    }

    @RequestMapping(value = "/dashboard/commission/{type}", method = RequestMethod.GET)
    public
    @ResponseBody
    BigDecimal getCommissions(@PathVariable("type") String type) {
        switch (type) {
            case "sell":
                return commissionService.findCommissionByType(OperationType.SELL).getValue();
            case "buy":
                return commissionService.findCommissionByType(OperationType.BUY).getValue();
            default:
                return null;
        }
    }

    @RequestMapping(value = "/forgotPassword")
    public ModelAndView forgotPassword() {
        ModelAndView model = new ModelAndView();
        model.addObject("user", new User());
        model.setViewName("forgotPassword");

        return model;
    }

    @RequestMapping(value = "/forgotPassword/submit", method = RequestMethod.POST)
    public ModelAndView forgotPasswordSubmit(@ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {
        String recapchaResponse = request.getParameter("g-recaptcha-response");
        if (!verifyReCaptcha.verify(recapchaResponse)) {
            String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
            ModelAndView modelAndView = new ModelAndView("/forgotPassword", "user", user);
            modelAndView.addObject("cpch", correctCapchaRequired);
            return modelAndView;
        }
        /**/
        registerFormValidation.validateEmail(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.addObject("user", user);
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

        String recapchaResponse = request.getParameter("g-recaptcha-response");
        if (!verifyReCaptcha.verify(recapchaResponse)) {
            String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
            ModelAndView modelAndView = new ModelAndView("/updatePassword", "user", user);
            modelAndView.addObject("cpch", correctCapchaRequired);
            return modelAndView;
        }

        registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            return new ModelAndView("/updatePassword", "user", user);
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
}


