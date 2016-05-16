package me.exrates.controller;

import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.User;
import me.exrates.model.UserFile;
import me.exrates.model.Wallet;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.dto.UpdateUserDto;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.security.service.UserSecureServiceImpl;
import me.exrates.service.MerchantService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserFilesService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class AdminController {

    @Autowired
    MessageSource messageSource;
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

    @Autowired
    private UserFilesService userFilesService;

    private static final Logger LOG = LogManager.getLogger(AdminController.class);


    private String currentRole;

    @RequestMapping("/admin")
    public ModelAndView admin(Principal principal) {

        currentRole = ((UsernamePasswordAuthenticationToken) principal).getAuthorities().iterator().next().getAuthority();

        ModelAndView model = new ModelAndView();
        model.setViewName("admin/admin");

        return model;
    }

    @ResponseBody
    @RequestMapping(value = "/admin/users/deleteUserFile", method = POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, String>> deleteUserDoc(final @RequestParam("fileId") int fileId,
                                                             final @RequestParam("userId") int userId,
                                                             final @RequestParam("path") String path,
                                                             final Locale locale) {
        try {
            final String filename = path.substring(path.lastIndexOf('/') + 1);
            userFilesService.deleteUserFile(filename, userId);
        } catch (IOException e) {
            LOG.error(e);
            return new ResponseEntity<>(singletonMap("error",
                    messageSource.getMessage("admin.internalError", null, locale)), INTERNAL_SERVER_ERROR);
        }
        userService.deleteUserFile(fileId);
        return new ResponseEntity<>(singletonMap("success",
                messageSource.getMessage("admin.successfulDeleteUserFiles", null, locale)), OK);
    }


    @ResponseBody
    @RequestMapping(value = "/admin/users", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<User> getAllUsers() {
        List<UserRole> userRoles = new ArrayList<>();
        userRoles.add(UserRole.USER);
        return userSecureService.getUsersByRoles(userRoles);
    }

    @ResponseBody
    @RequestMapping(value = "/admin/admins", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<User> getAllAdmins() {
        List<UserRole> adminRoles = new ArrayList<>();
        adminRoles.add(UserRole.ADMINISTRATOR);
        adminRoles.add(UserRole.ACCOUNTANT);
        adminRoles.add(UserRole.ADMIN_USER);
        return userSecureService.getUsersByRoles(adminRoles);
    }

    @ResponseBody
    @RequestMapping(value = "/admin/transactions", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<OperationViewDto> getUserTransactions(@RequestParam int id, HttpServletRequest request) {
        return transactionService.showUserOperationHistory(id, localeResolver.resolveLocale(request));
    }

    @ResponseBody
    @RequestMapping(value = "/admin/wallets", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
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

    @RequestMapping(value = "/admin/adduser/submit", method = POST)
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
        model.addObject("userFiles", userService.findUserDoc(id));
        return model;
    }

    @RequestMapping("/admin/userInfo")
    public ModelAndView userInfo(@RequestParam int id, HttpServletRequest request) {
        return editUser(id);
    }

    @RequestMapping(value = "/admin/edituser/submit", method = POST)
    public ModelAndView submitedit(@Valid @ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request, HttpServletResponse response) {
        if (!currentRole.equals(UserRole.ADMINISTRATOR.name()) && !user.getRole().name().equals(UserRole.USER.name())) {
            return new ModelAndView("403");
        }
        user.setConfirmPassword(user.getPassword());
        if (user.getFinpassword() == null) {
            user.setFinpassword("");
        }
        /**/
        registerFormValidation.validateEditUser(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.addObject("statusList", UserStatus.values());
            model.addObject("roleList", userService.getAllRoles());
            model.setViewName("admin/editUser");
        } else {
            UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
            updateUserDto.setEmail(user.getEmail());
            updateUserDto.setPassword(user.getPassword());
            updateUserDto.setPhone(user.getPhone());
            updateUserDto.setRole(user.getRole());
            updateUserDto.setStatus(user.getStatus());
            userService.updateUserByAdmin(updateUserDto);

            model.setViewName("redirect:/admin");
        }
        /**/
        model.addObject("user", user);
        /**/
        return model;
    }

    @RequestMapping("/settings")
    public ModelAndView settings(Principal principal, @RequestParam(required = false) Integer tabIdx, @RequestParam(required = false) String msg, HttpServletRequest request) {
        final User user = userService.getUserById(userService.getIdByEmail(principal.getName()));
        final ModelAndView mav = new ModelAndView("settings");
        final List<UserFile> userFile = userService.findUserDoc(user.getId());
        final Map<String, ?> map = RequestContextUtils.getInputFlashMap(request);
        mav.addObject("user", user);
        mav.addObject("tabIdx", tabIdx);
        mav.addObject("errorNoty", map != null ? map.get("msg") : msg);
        mav.addObject("userFiles", userFile);
        return mav;
    }

    @RequestMapping(value = "/settings/uploadFile", method = POST)
    public ModelAndView uploadUserDocs(final @RequestParam("file") MultipartFile[] multipartFiles,
                                       final Principal principal,
                                       final Locale locale)
    {
        final ModelAndView mav = new ModelAndView("settings");
        final User user = userService.getUserById(userService.getIdByEmail(principal.getName()));
        final List<MultipartFile> uploaded = userFilesService.reduceInvalidFiles(multipartFiles);
        mav.addObject("user", user);
        if (uploaded.isEmpty()) {
            mav.addObject("userFiles", userService.findUserDoc(user.getId()));
            mav.addObject("errorNoty", messageSource.getMessage("admin.errorUploadFiles", null, locale));
            return mav;
        }
        try {
            userFilesService.createUserFiles(user.getId(), uploaded);
        } catch (final IOException e) {
            LOG.error(e);
            mav.addObject("errorNoty", messageSource.getMessage("admin.internalError", null, locale));
            return mav;
        }
        mav.addObject("successNoty", messageSource.getMessage("admin.successUploadFiles", null, locale));
        mav.addObject("userFiles", userService.findUserDoc(user.getId()));
        return mav;
    }


    @RequestMapping(value = "settings/changePassword/submit", method = POST)
    public ModelAndView submitsettingsPassword(@Valid @ModelAttribute User user, BindingResult result,
                                               ModelAndView model, HttpServletRequest request) {

        registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.setViewName("settings");
            model.addObject("tabIdx", 1);
        } else {
            UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
            updateUserDto.setPassword(user.getPassword());
            updateUserDto.setEmail(user.getEmail()); //need for send the email
            userService.update(updateUserDto, localeResolver.resolveLocale(request));
            new SecurityContextLogoutHandler().logout(request, null, null);
            model.setViewName("redirect:/dashboard");
        }

        model.addObject("user", user);

        return model;
    }

    @RequestMapping(value = "settings/changeFinPassword/submit", method = POST)
    public ModelAndView submitsettingsFinPassword(@Valid @ModelAttribute User user, BindingResult result,
                                                  ModelAndView model, HttpServletRequest request) {


        registerFormValidation.validateResetFinPassword(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.setViewName("settings");
            model.addObject("tabIdx", 2);
        } else {
            UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
            updateUserDto.setFinpassword(user.getFinpassword());
            updateUserDto.setEmail(user.getEmail()); //need for send the email
            userService.update(updateUserDto, localeResolver.resolveLocale(request));
            model.setViewName("redirect:/mywallets");
        }

        model.addObject("user", user);

        return model;
    }

    @RequestMapping(value = "/changePasswordConfirm")
    public ModelAndView verifyEmail(@RequestParam("token") String token, HttpServletRequest req) {
        ModelAndView model = new ModelAndView();
        try {
            if (userService.verifyUserEmail(token) != 0) {
                model.addObject("successNoty", messageSource.getMessage("admin.passwordproved", null, localeResolver.resolveLocale(req)));
            } else {
                model.addObject("errorNoty", messageSource.getMessage("admin.passwordnotproved", null, localeResolver.resolveLocale(req)));
            }
            model.setViewName("redirect:/dashboard");
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
        }
        return model;
    }

    @RequestMapping(value = "/changeFinPasswordConfirm")
    public ModelAndView verifyEmailForFinPassword(HttpServletRequest request, @RequestParam("token") String token) {
        ModelAndView model = new ModelAndView();
        try {
            if (userService.verifyUserEmail(token) != 0) {
                model.addObject("successNoty", messageSource.getMessage("admin.finpasswordproved", null, localeResolver.resolveLocale(request)));
            } else {
                model.addObject("errorNoty", messageSource.getMessage("admin.finpasswordnotproved", null, localeResolver.resolveLocale(request)));
            }
            model.setViewName("redirect:/dashboard");
        } catch (Exception e) {
            model.setViewName("DBError");
            e.printStackTrace();
        }
        return model;
    }

    @RequestMapping(value = "/newIpConfirm")
    public ModelAndView verifyEmailForNewIp(@RequestParam("token") String token, HttpServletRequest req) {
        ModelAndView model = new ModelAndView();
        try {
            if (userService.verifyUserEmail(token) != 0) {
                req.getSession().setAttribute("successNoty", messageSource.getMessage("admin.newipproved", null, localeResolver.resolveLocale(req)));
            } else {
                req.getSession().setAttribute("errorNoty", messageSource.getMessage("admin.newipnotproved", null, localeResolver.resolveLocale(req)));
            }
            model.setViewName("redirect:/login");
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
