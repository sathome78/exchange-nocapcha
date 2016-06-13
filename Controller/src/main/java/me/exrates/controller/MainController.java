package me.exrates.controller;

import me.exrates.controller.exception.AbsentFinPasswordException;
import me.exrates.controller.exception.NotConfirmedFinPasswordException;
import me.exrates.controller.exception.NotCreateUserException;
import me.exrates.controller.exception.WrongFinPasswordException;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.User;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.enums.TokenType;
import me.exrates.security.filter.VerifyReCaptchaSec;
import me.exrates.service.ReferralService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;

@Controller
@PropertySource("classpath:about_us.properties")
public class MainController {

    private @Value("${contacts.telephone}") String telephone;
    private @Value("${contacts.email}") String email;

    @Autowired
    private UserService userService;

    @Autowired
    private RegisterFormValidation registerFormValidation;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private VerifyReCaptchaSec verifyReCaptcha;

    @Autowired
    private ReferralService referralService;

    private static final Logger logger = LogManager.getLogger(MainController.class);

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
    public ModelAndView registerUser(@RequestParam(value = "ref", required = false) String refReference, HttpServletRequest request) {
        User user = new User();
        ModelAndView mav = new ModelAndView("register", "user", user);
        mav.addObject("cpch", "");
        if (!isNull(refReference)) {
            final Optional<Integer> parentId = referralService.reduceReferralRef(refReference);
            if (parentId.isPresent()) {
                final String email = userService.getUserById(parentId.get()).getEmail();
                if (email != null) {
                    user.setParentEmail(email);
                    return mav;
                }
            }
        }
        //TODO for Denis
        User refferalRoot = userService.getCommonReferralRoot();
        if (refferalRoot != null) {
            user.setParentEmail(refferalRoot.getEmail());
        }
        return mav;
    }

    @RequestMapping("/generateReferral")
    public @ResponseBody Map<String,String> generateReferral(final Principal principal) {
        return singletonMap("referral",referralService.generateReferral(principal.getName()));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public ModelAndView createUser(@ModelAttribute("user") User user, BindingResult result, ModelMap model, HttpServletRequest request) {
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
                final int child = userService.getIdByEmail(user.getEmail());
                final int parent = userService.getIdByEmail(user.getParentEmail());
                //TODO for Denis
                if (child>0 && parent>0) {
                    referralService.bindChildAndParent(child, parent);
                }
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

    @RequestMapping(value = "/referral")
    public ModelAndView referral(final Principal principal) {
        final int id = userService.getIdByEmail(principal.getName());
        return new ModelAndView("referral", singletonMap("referralTxs", referralService.findAll(id)));
    }

    @RequestMapping("/aboutUs")
    public ModelAndView aboutUs() {
        ModelAndView modelAndView = new ModelAndView("aboutUs");
        modelAndView.addObject("telephone", telephone);
        modelAndView.addObject("email", email);
        return modelAndView;
    }

    /*CHECK FIN PASSWORD*/

    @RequestMapping(value = "/checkfinpass", method = RequestMethod.POST)
    @ResponseBody
    public void checkFinPassword(User user, HttpServletRequest request) {
        String enteredFinPassword = user.getFinpassword();
        User storedUser = userService.getUserById(userService.getIdByEmail(user.getEmail()));
        boolean isNotConfirmedToken = userService.getTokenByUserAndType(storedUser, TokenType.CHANGE_FIN_PASSWORD).size() > 0;
        if (isNotConfirmedToken) {
            throw new NotConfirmedFinPasswordException(messageSource.getMessage("admin.notconfirmedfinpassword", null, localeResolver.resolveLocale(request)));
        }
        String currentFinPassword = storedUser.getFinpassword();
        if (currentFinPassword == null || currentFinPassword.isEmpty()) {
            throw new AbsentFinPasswordException(messageSource.getMessage("admin.absentfinpassword", null, localeResolver.resolveLocale(request)));
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean authSuccess = passwordEncoder.matches(enteredFinPassword, currentFinPassword);
        if (!authSuccess) {
            throw new WrongFinPasswordException(messageSource.getMessage("admin.wrongfinpassword", null, localeResolver.resolveLocale(request)));
        }
    }
}
