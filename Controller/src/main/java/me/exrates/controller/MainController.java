package me.exrates.controller;

import me.exrates.controller.exception.NotCreateUserException;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.User;
import me.exrates.security.filter.VerifyReCaptchaSec;
import me.exrates.security.service.UserSecureService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.security.Principal;
import java.util.List;

@Controller
@PropertySource("classpath:about_us.properties")
public class MainController {


    private @Value("${contacts.telephone}") String telephone;
    private @Value("${contacts.email}") String email;

    @Autowired
    UserService userService;

    @Autowired
    UserSecureService userSecureService;

    @Autowired
    RegisterFormValidation registerFormValidation;

    @Autowired
    HttpServletRequest request;

    private static final Logger logger = LogManager.getLogger(MainController.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @Autowired
    VerifyReCaptchaSec verifyReCaptcha;

    @RequestMapping(value = "57163a9b3d1eafe27b8b456a.txt", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getFile() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("public/57163a9b3d1eafe27b8b456a.txt").getFile());
        return new FileSystemResource(file);
    }

    @RequestMapping("/403")
    public String error403() {
        return "403";
    }

    @RequestMapping("/register")
    public ModelAndView registerUser(HttpServletRequest request) {
        User user = new User();
        ModelAndView modelAndView = new ModelAndView("register", "user", user);
        modelAndView.addObject("cpch", "");
        return modelAndView;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView createUser(@ModelAttribute User user, BindingResult result, ModelMap model, HttpServletRequest request) {
        boolean flag = false;

        String recapchaResponse = request.getParameter("g-recaptcha-response");
        if (!verifyReCaptcha.verify(recapchaResponse)) {
            String correctCapchaRequired = messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request));
            ModelAndView modelAndView = new ModelAndView("register", "user", user);
            modelAndView.addObject("cpch", correctCapchaRequired);
            return modelAndView;
        }

        if (result.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView("register", "user", user);
            modelAndView.addObject("cpch", "");
            return modelAndView;
        }

        registerFormValidation.validate(user, result, localeResolver.resolveLocale(request));
        user.setPhone("");
        if (result.hasErrors()) {
            return new ModelAndView("register", "user", user);
        } else {
            user = (User) result.getModel().get("user"); 
            try {
                user.setIp(request.getRemoteHost());
                if (userService.create(user, localeResolver.resolveLocale(request))) {
                    flag = true;
                    logger.info("User registered with parameters = " + user.toString());
                } else {
                    throw new NotCreateUserException("Error while user creation");
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("User can't be registered with parameters = " + user.toString() + "  " + e.getMessage());
            }
            if (flag) {
                ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
                modelAndView.addObject("successNoty", messageSource.getMessage("register.sendletter", null, localeResolver.resolveLocale(request)));
                return  modelAndView;
            }
            else return new ModelAndView("DBError", "user", user);
        }
    }

    @RequestMapping(value = "/registrationConfirm")
    public ModelAndView verifyEmail(HttpServletRequest request, @RequestParam("token") String token) {
        ModelAndView model = new ModelAndView();
        try {
            if (userService.verifyUserEmail(token) != 0){
                model.addObject("successNoty", messageSource.getMessage("register.successfullyproved", null, localeResolver.resolveLocale(request)));
            } else {
                model.addObject("errorNoty", messageSource.getMessage("register.unsuccessfullyproved", null, localeResolver.resolveLocale(request)));
            }
            model.setViewName("redirect:/dashboard");
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
            logger.error("Error while verifing user registration email  " + e.getLocalizedMessage());
        }
        return model;
    }

    @RequestMapping("/personalpage")
    public ModelAndView gotoPersonalPage(@ModelAttribute User user, Principal principal) {
        String host = request.getRemoteHost();
        String email = principal.getName();
        String userIP = userService.logIP(email, host);
        return new ModelAndView("personalpage", "userIP", userIP);
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(HttpSession httpSession,
                              @RequestParam(value = "error", required = false) String error) {
        ModelAndView model = new ModelAndView();
        if (error != null) {
            if (httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION") != null) {
                String[] parts = httpSession.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").getClass().getName().split("\\.");
                String exceptionClass = parts[parts.length - 1];
                if (exceptionClass.equals("DisabledException")) {
                    model.addObject("error", messageSource.getMessage("login.blocked", null, localeResolver.resolveLocale(request)));
                } else if (exceptionClass.equals("BadCredentialsException")) {
                    model.addObject("error", messageSource.getMessage("login.notFound", null, localeResolver.resolveLocale(request)));
                } else if (exceptionClass.equals("NotVerifiedCaptchaError")) {
                    model.addObject("error", messageSource.getMessage("register.capchaincorrect", null, localeResolver.resolveLocale(request)));
                } else {
                    model.addObject("error", messageSource.getMessage("login.errorLogin", null, localeResolver.resolveLocale(request)));
                }
            }
        }

        model.setViewName("login");

        return model;

    }

    @RequestMapping(value = "/transaction")
    public ModelAndView transactions(Principal principal) {
        List<OperationViewDto> list = transactionService.showMyOperationHistory(principal.getName(), localeResolver.resolveLocale(request)).getData();
        return new ModelAndView("transaction", "transactions", list);
    }

    @RequestMapping("/aboutUs")
    public ModelAndView aboutUs() {
        ModelAndView modelAndView = new ModelAndView("aboutUs");
        modelAndView.addObject("telephone", telephone);
        modelAndView.addObject("email", email);
        return modelAndView;
    }

    @RequestMapping(value = "yandex_4b3a16d69d4869cb.html", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource getYandex_4b3a16d69d4869cb() {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("public/yandex_4b3a16d69d4869cb.html").getFile());
        return new FileSystemResource(file);
    }

}  

