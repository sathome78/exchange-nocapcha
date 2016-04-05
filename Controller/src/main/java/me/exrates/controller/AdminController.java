package me.exrates.controller;

import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.OperationView;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.security.service.UserSecureServiceImpl;
import me.exrates.service.MerchantService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Controller
public class AdminController {

    @Autowired
    private UserSecureServiceImpl userSecureService;

    @Autowired
    private UserService userService;

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private RegisterFormValidation registerFormValidation;

    @Autowired
    private WalletService walletService;

    @Autowired
    private TransactionService transactionService;

    private String currentRole;

    @RequestMapping("/admin")
    public ModelAndView admin(Principal principal) {

        currentRole = ((UsernamePasswordAuthenticationToken) principal).getAuthorities().iterator().next().getAuthority();

        ModelAndView model = new ModelAndView();
        model.setViewName("admin/admin");

        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/admin/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<User> getAllUsers() {
        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(UserRole.USER);
        return userSecureService.getUsersByRoles(userRoles);
    }

    @ResponseBody
    @RequestMapping(value = "/admin/admins", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<User> getAllAdmins() {
        List<UserRole> adminRoles = new ArrayList<>();
        adminRoles.add(UserRole.ADMINISTRATOR);
        adminRoles.add(UserRole.ACCOUNTANT);
        adminRoles.add(UserRole.ADMIN_USER);
        return userSecureService.getUsersByRoles(adminRoles);
    }

    @ResponseBody
    @RequestMapping(value = "/admin/transactions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<OperationView> getUserTransactions(@RequestParam int id, HttpServletRequest request) {
        return transactionService.showUserOperationHistory(id, localeResolver.resolveLocale(request));
    }

    @ResponseBody
    @RequestMapping(value = "/admin/wallets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Wallet> getUserWalletss(@RequestParam int id, HttpServletRequest request) {
        return walletService.getAllWallets(id);
    }

    @RequestMapping("/admin/addUser")
    public ModelAndView addUser() {

        if (!currentRole.equals(UserRole.ADMINISTRATOR.name())) {
            return new ModelAndView("403");
        }
        ModelAndView model = new ModelAndView();

        model.addObject("roleList", userService.getAllRoles());
        User user = new User();
        model.addObject("user", user);
        model.setViewName("admin/addUser");

        return model;
    }

    @RequestMapping(value = "/admin/adduser/submit", method = RequestMethod.POST)
    public ModelAndView submitcreate(@Valid @ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {

        if (!currentRole.equals(UserRole.ADMINISTRATOR.name())) {
            return new ModelAndView("403");
        }

        user.setConfirmPassword(user.getPassword());
        user.setStatus(UserStatus.ACTIVE);
        registerFormValidation.validate(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.addObject("roleList", userService.getAllRoles());
            model.setViewName("admin/addUser");
        } else {
            userService.createUserByAdmin(user);
            model.setViewName("redirect:/admin");
        }

        model.addObject("user", user);

        return model;
    }

    @RequestMapping("/admin/editUser")
    public ModelAndView editUser(@RequestParam int id) {

        ModelAndView model = new ModelAndView();

        model.addObject("statusList", UserStatus.values());
        List<UserRole> roleList = new ArrayList<>();
        if (currentRole.equals(UserRole.ADMIN_USER.name()) || currentRole.equals(UserRole.ACCOUNTANT.name())) {
            roleList.add(UserRole.USER);
        } else {
            roleList = userService.getAllRoles();
        }
        model.addObject("roleList", roleList);
        User user = userService.getUserById(id);
        if (!currentRole.equals(UserRole.ADMINISTRATOR.name()) && !user.getRole().name().equals(UserRole.USER.name())) {
            return new ModelAndView("403");
        }
        user.setId(id);
        model.addObject("user", user);
        model.setViewName("admin/editUser");

        return model;
    }

    @RequestMapping("/admin/userInfo")
    public ModelAndView userInfo(@RequestParam int id, HttpServletRequest request) {
        ModelAndView model = editUser(id);
        return model;
    }

    @RequestMapping(value = "/admin/edituser/submit", method = RequestMethod.POST)
    public ModelAndView submitedit(@Valid @ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request) {

        if (!currentRole.equals(UserRole.ADMINISTRATOR.name()) && !user.getRole().name().equals(UserRole.USER.name())) {
            return new ModelAndView("403");
        }
        user.setConfirmPassword(user.getPassword());
        if (user.getFinpassword() == null) {
            user.setFinpassword("");
        }
        registerFormValidation.validateEditUser(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.addObject("statusList", UserStatus.values());
            model.addObject("roleList", userService.getAllRoles());
            model.setViewName("admin/editUser");
        } else {
            userService.updateUserByAdmin(user);
            model.setViewName("redirect:/admin");
        }

        model.addObject("user", user);

        return model;
    }

    @RequestMapping("/settings")
    public ModelAndView settings(Principal principal) {

        ModelAndView model = new ModelAndView();

        User user = userService.getUserById(userService.getIdByEmail(principal.getName()));
        model.addObject("user", user);
        model.setViewName("settings");

        return model;
    }

    @RequestMapping(value = "settings/changePassword/submit", method = RequestMethod.POST)
    public ModelAndView submitsettingsPassword(@Valid @ModelAttribute User user, BindingResult result,
                                               ModelAndView model, HttpServletRequest request) {

        registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.setViewName("settings");
        } else {
            userService.update(user, true, false, false, localeResolver.resolveLocale(request));
            new SecurityContextLogoutHandler().logout(request, null, null);
            model.setViewName("redirect:/dashboard");
        }

        model.addObject("user", user);

        return model;
    }

    @RequestMapping(value = "settings/changeFinPassword/submit", method = RequestMethod.POST)
    public ModelAndView submitsettingsFinPassword(@Valid @ModelAttribute User user, BindingResult result,
                                                  ModelAndView model, HttpServletRequest request) {

        registerFormValidation.validateResetFinPassword(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.setViewName("settings");
        } else {
            userService.update(user, false, true, false, localeResolver.resolveLocale(request));
            model.setViewName("redirect:/mywallets");
        }

        model.addObject("user", user);

        return model;
    }

    @RequestMapping(value = "/changePasswordConfirm")
    public ModelAndView verifyEmail(WebRequest request, @RequestParam("token") String token) {
        ModelAndView model = new ModelAndView();
        try {
            userService.verifyUserEmail(token);
            model.setViewName("RegistrationConfirmed");
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
        }
        return model;
    }

    @RequestMapping(value = "/admin/withdrawal")
    public ModelAndView withdrawalRequests() {
        final List<UserRole> admins = new ArrayList<>();
        admins.addAll(asList(UserRole.ADMINISTRATOR, UserRole.ACCOUNTANT));
        final Map<String, Object> params = new HashMap<>();
        params.put("admins", admins);
        params.put("requests", this.merchantService.findAllWithdrawRequests());
        return new ModelAndView("withdrawalRequests", params);
    }

}
