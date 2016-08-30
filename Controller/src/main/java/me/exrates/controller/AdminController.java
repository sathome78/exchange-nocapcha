package me.exrates.controller;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.security.service.UserSecureServiceImpl;
import me.exrates.service.*;
import me.exrates.service.exception.OrderDeletingException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static me.exrates.model.enums.UserRole.ADMINISTRATOR;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class AdminController {

    private static final Logger LOG = LogManager.getLogger(AdminController.class);
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserSecureServiceImpl userSecureService;
    @Autowired
    private UserService userService;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RegisterFormValidation registerFormValidation;
    @Autowired
    private WalletService walletService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserFilesService userFilesService;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    @Qualifier("ExratesSessionRegistry")
    private SessionRegistry sessionRegistry;

    @RequestMapping(value = {"/admin", "/admin/users"})
    public ModelAndView admin(Principal principal, HttpSession httpSession) {

        final Object mutex = WebUtils.getSessionMutex(httpSession);
        synchronized (mutex) {
            httpSession.setAttribute("currentRole", ((UsernamePasswordAuthenticationToken) principal).getAuthorities().iterator().next().getAuthority());
        }

        ModelAndView model = new ModelAndView();
        List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairs();
        model.addObject("currencyPairList", currencyPairList);
        model.setViewName("admin/admin");
        return model;
    }

    @RequestMapping(value = "/admin/administrators", method = GET)
    public String administrators() {
        return "admin/administrators";
    }

    @RequestMapping(value = "/admin/referral", method = GET)
    public ModelAndView referral() {
        ModelAndView model = new ModelAndView();
        model.addObject("referralLevels", referralService.findAllReferralLevels());
        model.addObject("commonRefRoot", userService.getCommonReferralRoot());
        model.addObject("admins", userSecureService.getUsersByRoles(singletonList(ADMINISTRATOR)));
        model.setViewName("admin/referral");
        return model;
    }

    @RequestMapping(value = "/admin/removeOrder", method = GET)
    public ModelAndView orderDeletion() {
        ModelAndView model = new ModelAndView();
        List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairs();
        model.addObject("currencyPairList", currencyPairList);
        model.setViewName("admin/order_delete");
        return model;
    }

    @RequestMapping(value = "/admin/editCmnRefRoot", method = POST)
    @ResponseBody
    public ResponseEntity<Void> editCommonReferralRoot(final @RequestParam("id") int id) {
        userService.updateCommonReferralRoot(id);
        return new ResponseEntity<>(OK);
    }

    @RequestMapping(value = "/admin/editLevel", method = POST)
    @ResponseBody
    public ResponseEntity<Map<String, String>> editReferralLevel(final @RequestParam("level") int level, final @RequestParam("oldLevelId") int oldLevelId, final @RequestParam("percent") BigDecimal percent, final Locale locale) {
        final int result;
        try {
            result = referralService.updateReferralLevel(level, oldLevelId, percent);
            return new ResponseEntity<>(singletonMap("id", String.valueOf(result)), OK);
        } catch (final IllegalStateException e) {
            LOG.error(e);
            return new ResponseEntity<>(singletonMap("error", messageSource.getMessage("admin.refPercentExceedMaximum", null, locale)), BAD_REQUEST);
        } catch (final Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(singletonMap("error", messageSource.getMessage("admin.failureRefLevelEdit", null, locale)), BAD_REQUEST);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/admin/users/deleteUserFile", method = POST)
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
    @RequestMapping(value = "/admin/usersList", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
    public DataTable<List<OperationViewDto>> getUserTransactions(final @RequestParam int id, final @RequestParam Map<String, String> params, final HttpServletRequest request) {
        return transactionService.showUserOperationHistory(id, localeResolver.resolveLocale(request), params);
    }

    @ResponseBody
    @RequestMapping(value = "/admin/wallets", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<Wallet> getUserWallets(@RequestParam int id, HttpServletRequest request) {
        return walletService.getAllWallets(id);
    }

    @RequestMapping("/admin/addUser")
    public ModelAndView addUser(HttpSession httpSession) {

        final Object mutex = WebUtils.getSessionMutex(httpSession);
        String currentRole = "";
        synchronized (mutex) {
            currentRole = (String) httpSession.getAttribute("currentRole");
        }
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
    public ModelAndView submitcreate(@Valid @ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request,
                                     HttpSession httpSession) {

        final Object mutex = WebUtils.getSessionMutex(httpSession);
        String currentRole = "";
        synchronized (mutex) {
            currentRole = (String) httpSession.getAttribute("currentRole");
        }
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
    public ModelAndView editUser(@RequestParam int id, HttpSession httpSession) {

        ModelAndView model = new ModelAndView();

        model.addObject("statusList", UserStatus.values());
        List<UserRole> roleList = new ArrayList<>();
        final Object mutex = WebUtils.getSessionMutex(httpSession);
        String currentRole = "";
        synchronized (mutex) {
            currentRole = (String) httpSession.getAttribute("currentRole");
        }
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
    public ModelAndView userInfo(@RequestParam int id, HttpServletRequest request, HttpSession httpSession) {
        ModelAndView model = editUser(id, httpSession);
        return model;
    }

    @RequestMapping(value = "/admin/edituser/submit", method = RequestMethod.POST)
    public ModelAndView submitedit(@Valid @ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request, HttpServletResponse response,
                                   HttpSession httpSession) {
        final Object mutex = WebUtils.getSessionMutex(httpSession);
        String currentRole = "";
        synchronized (mutex) {
            currentRole = (String) httpSession.getAttribute("currentRole");
        }
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
            updateUserDto.setStatus(user.getUserStatus());
            userService.updateUserByAdmin(updateUserDto);
            if (updateUserDto.getStatus() == UserStatus.DELETED) {
                invalidateUserSession(updateUserDto.getEmail());
            }

            model.setViewName("redirect:/admin");
        }
        /**/
        model.addObject("user", user);
        /**/
        return model;
    }

    private void invalidateUserSession(String userEmail) {
        LOG.debug(sessionRegistry.getAllPrincipals().size());
        Optional<Object> updatedUser = sessionRegistry.getAllPrincipals().stream()
                .filter(principalObj -> {
            UserDetails principal = (UserDetails) principalObj;
            return userEmail.equals(principal.getUsername());
        })
                .findFirst();
        if (updatedUser.isPresent()) {
            sessionRegistry.getAllSessions(updatedUser.get(), false).forEach(SessionInformation::expireNow);
        }
    }

    @RequestMapping(value = "/settings/uploadFile", method = POST)
    public ModelAndView uploadUserDocs(final @RequestParam("file") MultipartFile[] multipartFiles,
                                       final Principal principal,
                                       final Locale locale) {
        final ModelAndView mav = new ModelAndView("globalPages/settings");
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
        user.setStatus(user.getUserStatus());
        registerFormValidation.validateResetPassword(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.setViewName("globalPages/settings");
            model.addObject("sectionid", "passwords-changing");
            model.addObject("tabIdx", 0);
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
                                                  ModelAndView model, HttpServletRequest request, RedirectAttributes redir) {
        user.setStatus(user.getUserStatus());
        registerFormValidation.validateResetFinPassword(user, result, localeResolver.resolveLocale(request));
        if (result.hasErrors()) {
            model.setViewName("globalPages/settings");
            model.addObject("sectionid", "passwords-changing");
            model.addObject("tabIdx", 1);
        } else {
            UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
            updateUserDto.setFinpassword(user.getFinpassword());
            updateUserDto.setEmail(user.getEmail()); //need for send the email
            userService.update(updateUserDto, localeResolver.resolveLocale(request));

            final String message = messageSource.getMessage("admin.changePasswordSendEmail", null, localeResolver.resolveLocale(request));
            redir.addFlashAttribute("msg", message);
            model.setViewName("redirect:/settings");
        }

        return model;
    }

    @RequestMapping(value = "/changePasswordConfirm")
    public ModelAndView verifyEmail(@RequestParam("token") String token, HttpServletRequest request) {
        try {
            request.setCharacterEncoding("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ModelAndView model = new ModelAndView();
        try {
            if (userService.verifyUserEmail(token) != 0) {
                model.addObject("successNoty", messageSource.getMessage("admin.passwordproved", null, localeResolver.resolveLocale(request)));
            } else {
                model.addObject("errorNoty", messageSource.getMessage("admin.passwordnotproved", null, localeResolver.resolveLocale(request)));
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
        try {
            request.setCharacterEncoding("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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

    @ResponseBody
    @RequestMapping(value = "/admin/orderinfo", method = RequestMethod.GET)
    public OrderInfoDto getOrderInfo(@RequestParam int id, HttpServletRequest request) {
        return orderService.getOrderInfo(id, localeResolver.resolveLocale(request));
    }

    @ResponseBody
    @RequestMapping(value = "/admin/orderdelete", method = RequestMethod.POST)
    public Integer deleteOrderByAdmin(@RequestParam int id) {
        try {
            return orderService.deleteOrderByAdmin(id);
        } catch (Exception e) {
            LOG.error(e);
            throw e;
        }
    }

    @ResponseBody
    @RequestMapping(value = "/admin/searchorder", method = RequestMethod.GET)
    public Integer searchOrderByAdmin(@RequestParam Integer currencyPair,
                                      @RequestParam String orderType,
                                      @RequestParam String orderDate,
                                      @RequestParam BigDecimal orderRate,
                                      @RequestParam BigDecimal orderVolume) {
        return orderService.searchOrderByAdmin(currencyPair, orderType, orderDate, orderRate, orderVolume);
    }


    @ResponseBody
    @RequestMapping(value = "/admin/searchorders", method = RequestMethod.GET)
    public DataTable<List<OrderBasicInfoDto>> searchOrderByAdmin(@RequestParam(required = false) Integer currencyPair,
                                      @RequestParam(required = false) String orderType,
                                      @RequestParam(required = false) String orderDateFrom,
                                      @RequestParam(required = false) String orderDateTo,
                                      @RequestParam(required = false) BigDecimal orderRate,
                                      @RequestParam(required = false) BigDecimal orderVolume,
                                      @RequestParam(required = false) String creator,
                                      @RequestParam Map<String, String> params,
                                      HttpServletRequest request) {

        try {
            DataTable<List<OrderBasicInfoDto>> orderInfo = orderService.searchOrdersByAdmin(currencyPair, orderType,
                    orderDateFrom, orderDateTo, orderRate, orderVolume, creator, localeResolver.resolveLocale(request), params);

            return orderInfo;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            DataTable<List<OrderBasicInfoDto>> errorResult = new DataTable<>();
            errorResult.setError(ex.getMessage());
            errorResult.setData(Collections.EMPTY_LIST);
            return errorResult;
        }

    }

    @RequestMapping(value = "admin/downloadUsersWalletsSummary", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String getUsersWalletsSummeryTxt(@RequestParam String startDate, @RequestParam String endDate) {
        return
                UserSummaryDto.getTitle() +
                        userService.getUsersSummaryList(startDate, endDate)
                                .stream()
                                .map(e -> e.toString())
                                .collect(Collectors.joining());
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(OrderDeletingException.class)
    @ResponseBody
    public ErrorInfo OrderDeletingExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @RequestMapping(value = "/admin/invoiceConfirmation")
    public ModelAndView invoiceTransactions(HttpSession httpSession) {
        final Object mutex = WebUtils.getSessionMutex(httpSession);
        String currentRole = "";
        synchronized (mutex) {
            currentRole = (String) httpSession.getAttribute("currentRole");
        }
        if (currentRole == null){
            return new ModelAndView("403");
        }
        List<InvoiceRequest> list;

        if (currentRole.equals(UserRole.ADMINISTRATOR.name()) || currentRole.equals(UserRole.ACCOUNTANT.name())) {
            list = invoiceService.findAllInvoiceRequests();
        } else {
            return new ModelAndView("403");
        }

        return new ModelAndView("admin/transaction_invoice", "invoiceRequests", list);
    }

    @RequestMapping(value = "/admin/bitcoinConfirmation")
    public ModelAndView bitcoinTransactions(HttpSession httpSession) {
        final Object mutex = WebUtils.getSessionMutex(httpSession);
        String currentRole = "";
        synchronized (mutex) {
            currentRole = (String) httpSession.getAttribute("currentRole");
        }
        if (currentRole == null){
            return new ModelAndView("403");
        }
        List<Transaction> list;
        Merchant merchant = new Merchant();
        merchant.setName("Blockchain");
        Transaction transaction;

        if (currentRole.equals(UserRole.ADMINISTRATOR.name()) || currentRole.equals(UserRole.ACCOUNTANT.name())) {
            list = transactionService.getOpenTransactionsByMerchant(merchant);
        } else {
            return new ModelAndView("403");
        }

        return new ModelAndView("admin/transaction_bitcoin", "bitcoinRequests", list);
    }

    @RequestMapping(value = "/admin/sessionControl")
    public ModelAndView sessionControl() {
        ModelAndView modelAndView = new ModelAndView("admin/sessionControl");
        sessionRegistry.getAllPrincipals().stream().peek(principal -> {
            LOG.debug(principal);
            LOG.debug(principal.getClass());
        });



        return modelAndView;
    }
}