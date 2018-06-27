package me.exrates.controller;

import com.captcha.botdetect.web.servlet.Captcha;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.User;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.UserRole;
import me.exrates.security.filter.VerifyReCaptchaSec;
import me.exrates.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.*;

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
  VerifyReCaptchaSec verifyReCaptcha;

  @Value("${captcha.type}")
  String CAPTCHA_TYPE;

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

  @RequestMapping(value = "/forgotPassword")
  public String forgotPassword(Model model) {
    if (!model.containsAttribute("user")) {
      model.addAttribute("user", new User());
    }
    model.addAttribute("captchaType", CAPTCHA_TYPE);
    return "forgotPassword";
  }

  @RequestMapping(value = "/forgotPassword/submit", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity forgotPasswordSubmit(@ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request, RedirectAttributes attr) {
    /**/
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
    /**/
    Map<String, Object> body = new HashMap<>();
    body.put("result",messageSource.getMessage("admin.changePasswordSendEmail", null, localeResolver.resolveLocale(request)));
    body.put("email", email);
    return ResponseEntity.ok(body);
  }

  @RequestMapping(value = "/forgotPassword/recover", method = RequestMethod.GET)
  public ModelAndView recoveryPassword() {
    return new ModelAndView("fragments/recoverPassword");
  }

  @RequestMapping(value = "/resetPasswordConfirm")
  public ModelAndView resetPasswordConfirm(@RequestParam("token") String token, @RequestParam("email") String email, RedirectAttributes attr, HttpServletRequest request) {
    ModelAndView model = new ModelAndView();
    try {
      int userId = userService.verifyUserEmail(token);
      if (userId != 0) {
          User user = userService.getUserById(userId);
          attr.addFlashAttribute("recoveryConfirm", messageSource.getMessage("register.successfullyproved",
                null, localeResolver.resolveLocale(request)));
          model.setViewName("redirect:/forgotPassword/recover");
          org.springframework.security.core.userdetails.User userSpring = new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), false, false, false, false,
                  userDetailsService.loadUserByUsername(user.getEmail()).getAuthorities());
          Collection<GrantedAuthority> authList = new ArrayList<>();
          authList.add(new SimpleGrantedAuthority(UserRole.ROLE_CHANGE_PASSWORD.name()));
          Authentication auth = new UsernamePasswordAuthenticationToken(
                  userSpring, null, authList);
          SecurityContextHolder.getContext().setAuthentication(auth);
          user.setPassword(null);
      } else {
          model.addObject("email", email);
          model.addObject("user", new User());
          model.addObject("captchaType", CAPTCHA_TYPE);
          model.setViewName("passRecoveryError");
          SecurityContextHolder.getContext().setAuthentication(null);
      }
    } catch (Exception e) {
      model.setViewName("DBError");
      e.printStackTrace();
    }
    return model;
  }

  @RequestMapping(value = "/dashboard/updatePassword", method = RequestMethod.POST)
  public ModelAndView updatePassword(@ModelAttribute User user, BindingResult result, HttpServletRequest request, Principal principal) {
        /**/
    registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
    if (result.hasErrors()) {
      ModelAndView modelAndView = new ModelAndView("/updatePassword", "user", user);
      modelAndView.addObject("captchaType", CAPTCHA_TYPE);
      return modelAndView;
    } else {
      String password = user.getPassword();
      ModelAndView model = new ModelAndView();
      User updatedUser = userService.findByEmail(principal.getName());
      UpdateUserDto updateUserDto = new UpdateUserDto(updatedUser.getId());
      updateUserDto.setPassword(password);
      updateUserDto.setRole(updatedUser.getRole());
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


}


