package me.exrates.controller;

import me.exrates.controller.annotation.AdminLoggable;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.security.filter.NotVerifiedCaptchaError;
import me.exrates.security.filter.VerifyReCaptchaSec;
import me.exrates.service.TemporalTokenService;
import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.UserRole;
import me.exrates.service.*;
import me.exrates.service.session.UserSessionService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

import static java.util.Objects.isNull;

@Controller
@PropertySource("classpath:/captcha.properties")
public class DashboardController {
    private static final Logger LOG = LogManager.getLogger(DashboardController.class);


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
    private UserSessionService userSessionService;

    @Autowired
    TemporalTokenService temporalTokenService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private VerifyReCaptchaSec verifyReCaptchaSec;

    @RequestMapping(value = {"/dashboard/locale"})
    @ResponseBody
    public void localeSwitcherCommand(
            Principal principal,
            HttpServletRequest request,
            HttpServletResponse response) {
        Locale locale = localeResolver.resolveLocale(request);
        localeResolver.setLocale(request, response, locale);
        if (principal != null) {
            userService.setPreferedLang(userService.getIdByEmail(principal.getName()), localeResolver.resolveLocale(request));
        }
        request.getSession();
    }

    public static String convertLanguageNameToMenuFormat(String lang) {
        final Map<String, String> convertMap = new HashMap<String, String>() {{
            put("in", "id");
        }};
        String convertedLangName = convertMap.get(lang);
        return convertedLangName == null ? lang : convertedLangName;
    }

  @RequestMapping(value = "/passwordRecovery", method = RequestMethod.GET)
  public ModelAndView recoveryPassword(@ModelAttribute("user") User user, @ModelAttribute("token") TemporalToken temporalToken) {
      ModelAndView model = new ModelAndView("fragments/recoverPassword");

      model.addObject("user", user);
      model.addObject("token", temporalToken);

      return model;
  }

    @RequestMapping(value = "/resetPasswordConfirm")
    public ModelAndView resetPasswordConfirm(@RequestParam("token") String token, @RequestParam("email") String email, RedirectAttributes attr, HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        try {
            TemporalToken dbToken = userService.verifyUserEmailForForgetPassword(token);
            if (dbToken != null && !dbToken.isAlreadyUsed()) {
                User user = userService.getUserById(dbToken.getUserId());

                attr.addFlashAttribute("recoveryConfirm", messageSource.getMessage("register.successfullyproved",
                        null, localeResolver.resolveLocale(request)));
                attr.addFlashAttribute("user", user);
                attr.addFlashAttribute("token", dbToken);

                model.setViewName("redirect:/passwordRecovery");
                temporalTokenService.updateTemporalToken(dbToken);
            } else {
                if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser") || request.isUserInRole(UserRole.ROLE_CHANGE_PASSWORD.name())) {
                    attr.addFlashAttribute("userEmail", email);
                    attr.addFlashAttribute("recoveryError", messageSource.getMessage("dashboard.resetPasswordDoubleClick", null, localeResolver.resolveLocale(request)));
                } else {
                    attr.addFlashAttribute("errorNoty", messageSource.getMessage("dashboard.resetPasswordDoubleClick", null, localeResolver.resolveLocale(request)));
                }
                return new ModelAndView(new RedirectView("/dashboard"));
            }
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
        }
        return model;
    }

    @RequestMapping(value = "/forgotPassword/submit", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity forgotPasswordSubmit(@ModelAttribute User user, BindingResult result, HttpServletRequest request) {

        String recapchaResponse = request.getParameter("g-recaptcha-response");

        registerFormValidation.validateEmail(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            //TODO
            throw new RuntimeException(result.toString());
        }
        String email = user.getEmail();
        user = userService.findByEmail(email);
        UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
        updateUserDto.setEmail(email);
        userService.update(updateUserDto, true, localeResolver.resolveLocale(request));

        Map<String, Object> body = new HashMap<>();
        body.put("result", messageSource.getMessage("admin.changePasswordSendEmail", null, localeResolver.resolveLocale(request)));
        body.put("email", email);
        return ResponseEntity.ok(body);
        }
    }

  @RequestMapping(value = "/dashboard/updatePasswordbytoken", method = RequestMethod.POST)
  public ModelAndView updatePassword(@RequestParam("token") String temporalToken, @RequestParam("password") String password, RedirectAttributes attr, Locale locale) {
    //registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));

    User userUpdate = userService.getUserByTemporalToken(temporalToken);

    ModelAndView model = new ModelAndView();

    UpdateUserDto updateUserDto = new UpdateUserDto(userUpdate.getId());
    updateUserDto.setPassword(password);
    userService.updateUserByAdmin(updateUserDto);
    temporalTokenService.deleteTemporalToken(temporalToken);

    userSessionService.invalidateUserSessionExceptSpecific(userUpdate.getEmail(), RequestContextHolder.currentRequestAttributes().getSessionId());

    attr.addFlashAttribute("successNoty", messageSource.getMessage("login.passwordUpdateSuccess", null, locale));
    model.setViewName("redirect:/dashboard");
    return model;
  }

    @AdminLoggable
    @GetMapping(value = "/getMerchantInputCommissionNotification")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getMerchantInputCommissionNotification(@RequestParam("merchant_id") int merchantId,
                                                                                      @RequestParam("currency_id") int currencyId,
                                                                                      @RequestParam("child_merchant") String childMerchant,
                                                                                      Locale locale) {
        if (isNull(childMerchant)) {
            childMerchant = StringUtils.EMPTY;
        }
        BigDecimal commissionPercents = merchantService.getMerchantInputCommission(merchantId, currencyId, childMerchant);

        String message = BigDecimal.ZERO.compareTo(commissionPercents) == 0
                ? messageSource.getMessage("merchant.commission.warning", null, locale)
                : messageSource.getMessage("merchant.commission.interkassa-attention", new String[]{commissionPercents.toString()}, locale);
        return ResponseEntity.ok(Collections.singletonMap("message", message));
    }
}


