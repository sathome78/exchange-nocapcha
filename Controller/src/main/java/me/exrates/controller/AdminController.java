package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.*;
import me.exrates.controller.exception.NoRequestedBeansFoundException;
import me.exrates.controller.exception.NotAcceptableOrderException;
import me.exrates.controller.exception.NotEnoughMoneyException;
import me.exrates.controller.validator.RegisterFormValidation;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.filterData.AdminStopOrderFilterData;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.*;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.form.AuthorityOptionsForm;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.security.service.UserSecureService;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.stopOrder.StopOrderService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static me.exrates.model.enums.BusinessUserRoleEnum.ADMIN;
import static me.exrates.model.enums.GroupUserRoleEnum.ADMINS;
import static me.exrates.model.enums.GroupUserRoleEnum.USERS;
import static me.exrates.model.enums.UserCommentTopicEnum.GENERAL;
import static me.exrates.model.enums.UserRole.ADMINISTRATOR;
import static me.exrates.model.enums.UserRole.FIN_OPERATOR;
import static me.exrates.model.enums.UserRole.TRADER;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.REFILL;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Log4j2
@Controller
public class AdminController {

  private static final Logger LOG = LogManager.getLogger(AdminController.class);
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private UserSecureService userSecureService;
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
  private Map<String, BitcoinService> bitcoinLikeServices;
  @Autowired
  private NotificationService notificationService;
  @Autowired
  private PhraseTemplateService phraseTemplateService;
  @Autowired
  private CommissionService commissionService;
  @Autowired
  UserRoleService userRoleService;
  @Autowired
  UserTransferService userTransferService;
  @Autowired
  WithdrawService withdrawService;
  @Autowired
  StopOrderService stopOrderService;
  @Autowired
  RefillService refillService;

  @Autowired
  BotService botService;

  @Autowired
  private MerchantServiceContext serviceContext;

  @Autowired
  @Qualifier("ExratesSessionRegistry")
  private SessionRegistry sessionRegistry;

  public static String adminAnyAuthority;
  public static String pureAdminAnyAuthority;
  public static String nonAdminAnyAuthority;
  public static String traderAuthority;

  @PostConstruct
  private void init() {
    List<UserRole> adminRoles = userRoleService.getRealUserRoleByBusinessRoleList(ADMIN);
    String adminList = adminRoles.stream()
        .map(e -> "'" + e.name() + "'")
        .collect(Collectors.joining(","));
    List<UserRole> traderRoles = userRoleService.getRealUserRoleByBusinessRoleList(BusinessUserRoleEnum.TRADER);
    String traderList = traderRoles.stream()
            .map(e -> "'" + e.name() + "'")
            .collect(Collectors.joining(","));
    traderAuthority = "hasAnyAuthority(" + traderList + ")";
    adminAnyAuthority = "hasAnyAuthority(" + adminList + ")";
    nonAdminAnyAuthority = "!" + adminAnyAuthority;
    String pureAdminList = adminRoles.stream()
        .filter(e -> e != FIN_OPERATOR)
        .map(e -> "'" + e.name() + "'")
        .collect(Collectors.joining(","));
    pureAdminAnyAuthority = "hasAnyAuthority(" + adminList + ")";
  }

  @RequestMapping(value = {"/2a8fy7b07dxe44", "/2a8fy7b07dxe44/users"})
  public ModelAndView admin(Principal principal, HttpSession httpSession) {

    final Object mutex = WebUtils.getSessionMutex(httpSession);
    synchronized (mutex) {
      httpSession.setAttribute("currentRole", ((UsernamePasswordAuthenticationToken) principal).getAuthorities().iterator().next().getAuthority());
    }

    ModelAndView model = new ModelAndView();
    List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairs();
    model.addObject("currencyPairList", currencyPairList);
    model.addObject("enable_2fa", userService.isGlobal2FaActive());
    model.addObject("post_url", "/2a8fy7b07dxe44/set2fa");
    model.setViewName("admin/admin");
    return model;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/administrators", method = GET)
  public String administrators() {
    return "admin/administrators";
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/referral", method = GET)
  public ModelAndView referral() {
    ModelAndView model = new ModelAndView();
    model.addObject("referralLevels", referralService.findAllReferralLevels());
    model.addObject("commonRefRoot", userService.getCommonReferralRoot());
    model.addObject("admins", userSecureService.getUsersByRoles(singletonList(ADMINISTRATOR)));
    model.setViewName("admin/referral");
    return model;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/removeOrder", method = GET)
  public ModelAndView orderDeletion() {
    ModelAndView model = new ModelAndView();
    List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairs();
    model.addObject("currencyPairList", currencyPairList);
    model.addObject("operationTypes", Arrays.asList(OperationType.SELL, OperationType.BUY));
    model.addObject("statusList", Arrays.asList(OrderStatus.values()));
    model.addObject("roleList", Arrays.asList(UserRole.values()));
    model.setViewName("admin/order_delete");
    return model;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/removeStopOrder", method = GET)
  public ModelAndView stopOrderDeletion() {
    ModelAndView model = new ModelAndView();
    List<CurrencyPair> currencyPairList = currencyService.getAllCurrencyPairs();
    model.addObject("currencyPairList", currencyPairList);
    model.addObject("operationTypes", Arrays.asList(OperationType.SELL, OperationType.BUY));
    model.addObject("statusList", Arrays.asList(OrderStatus.OPENED, OrderStatus.CLOSED, OrderStatus.CANCELLED, OrderStatus.INPROCESS));
    model.setViewName("admin/stop_order_delete");
    return model;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editCmnRefRoot", method = POST)
  @ResponseBody
  public ResponseEntity<Void> editCommonReferralRoot(final @RequestParam("id") int id) {
    userService.updateCommonReferralRoot(id);
    return new ResponseEntity<>(OK);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editLevel", method = POST)
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
  @RequestMapping(value = "/2a8fy7b07dxe44/users/deleteUserFile", method = POST)
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
  @RequestMapping(value = "/2a8fy7b07dxe44/usersList", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public DataTable<List<User>> getAllUsers(@RequestParam Map<String, String> params) {
    List<UserRole> userRoles = userRoleService.getRealUserRoleByGroupRoleList(USERS);
    return userSecureService.getUsersByRolesPaginated(userRoles, params);
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/admins", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Collection<User> getAllAdmins() {
    List<UserRole> adminRoles = userRoleService.getRealUserRoleByGroupRoleList(ADMINS);
    return userSecureService.getUsersByRoles(adminRoles);
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/transactions", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public DataTable<List<OperationViewDto>> getUserTransactions(
      AdminTransactionsFilterData filterData,
      @RequestParam Integer id,
      @RequestParam Map<String, String> params,
      Principal principal,
      HttpServletRequest request) {
    filterData.initFilterItems();
    DataTableParams dataTableParams = DataTableParams.resolveParamsFromRequest(params);
    Integer requesterAdminId = userService.getIdByEmail(principal.getName());
    return transactionService.showUserOperationHistory(
        requesterAdminId,
        id,
        filterData,
        dataTableParams,
        localeResolver.resolveLocale(request));
  }


  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/wallets", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Collection<WalletFormattedDto> getUserWallets(@RequestParam int id) {
    return walletService.getAllWallets(id).stream().map(WalletFormattedDto::new).collect(Collectors.toList());
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/comments", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Collection<Comment> getUserComments(@RequestParam int id, HttpServletRequest request) {

    return userService.getUserComments(id);
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/addComment", method = POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<String, String>> addUserComment(@RequestParam String newComment, @RequestParam String email,
                                                            @RequestParam boolean sendMessage, HttpServletRequest request, final Locale locale) {

    try {
      userService.addUserComment(GENERAL, newComment, email, sendMessage);
    } catch (Exception e) {
      LOG.error(e);
      return new ResponseEntity<>(singletonMap("error",
          messageSource.getMessage("admin.internalError", null, locale)), INTERNAL_SERVER_ERROR);
    }

    return new ResponseEntity<>(singletonMap("success",
        messageSource.getMessage("admin.successfulDeleteUserFiles", null, locale)), OK);
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/deleteUserComment", method = POST)
  public ResponseEntity<Map<String, String>> deleteUserComment(final @RequestParam("commentId") int commentId,
                                                               final Locale locale) {
    try {
      userService.deleteUserComment(commentId);
    } catch (Exception e) {
      LOG.error(e);
      return new ResponseEntity<>(singletonMap("error",
          messageSource.getMessage("admin.internalError", null, locale)), INTERNAL_SERVER_ERROR);
    }
    return new ResponseEntity<>(singletonMap("success",
        messageSource.getMessage("admin.successCommentDelete", null, locale)), OK);
  }


  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/orders", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<OrderWideListDto> getUserOrders(final @RequestParam int id, final @RequestParam("tableType") String tableType,
                                              final @RequestParam("currencyPairId") int currencyPairId, final HttpServletRequest request) {

    CurrencyPair currencyPair;
    if (currencyPairId != 0) {
      currencyPair = currencyService.findCurrencyPairById(currencyPairId);
    } else {
      currencyPair = null;
    }
    String email = userService.getUserById(id).getEmail();
    Map<String, List<OrderWideListDto>> resultMap = new HashMap<>();

    List<OrderWideListDto> result = new ArrayList<>();
    switch (tableType) {
      case "ordersBuyClosed":
        List<OrderWideListDto> ordersBuyClosed = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CLOSED, OperationType.BUY, 0, -1, localeResolver.resolveLocale(request));
        result = ordersBuyClosed;
        break;
      case "ordersSellClosed":
        List<OrderWideListDto> ordersSellClosed = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CLOSED, OperationType.SELL, 0, -1, localeResolver.resolveLocale(request));
        result = ordersSellClosed;
        break;
      case "ordersBuyOpened":
        List<OrderWideListDto> ordersBuyOpened = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.OPENED, OperationType.BUY, 0, -1, localeResolver.resolveLocale(request));
        result = ordersBuyOpened;
        break;
      case "ordersSellOpened":
        List<OrderWideListDto> ordersSellOpened = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.OPENED, OperationType.SELL, 0, -1, localeResolver.resolveLocale(request));
        result = ordersSellOpened;
        break;
      case "ordersBuyCancelled":
        List<OrderWideListDto> ordersBuyCancelled = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CANCELLED, OperationType.BUY, 0, -1, localeResolver.resolveLocale(request));
        result = ordersBuyCancelled;
        break;
      case "ordersSellCancelled":
        List<OrderWideListDto> ordersSellCancelled = orderService.getUsersOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CANCELLED, OperationType.SELL, 0, -1, localeResolver.resolveLocale(request));
        result = ordersSellCancelled;
        break;
      case "stopOrdersCancelled":
        List<OrderWideListDto> stopOrdersCancelled = stopOrderService.getUsersStopOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CANCELLED, null, 0, -1, localeResolver.resolveLocale(request));
        result = stopOrdersCancelled ;
        break;
      case "stopOrdersClosed":
        List<OrderWideListDto> stopOrdersClosed = stopOrderService.getUsersStopOrdersWithStateForAdmin(email, currencyPair, OrderStatus.CLOSED, null, 0, -1, localeResolver.resolveLocale(request));
        result = stopOrdersClosed ;
        break;
      case "stopOrdersOpened":
        List<OrderWideListDto> stopOrdersOpened = stopOrderService.getUsersStopOrdersWithStateForAdmin(email, currencyPair, OrderStatus.OPENED, null,0, -1, localeResolver.resolveLocale(request));
        result = stopOrdersOpened;
        break;
    }
    return result;
  }

  @RequestMapping("/2a8fy7b07dxe44/addUser")
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

  @RequestMapping(value = "/2a8fy7b07dxe44/adduser/submit", method = RequestMethod.POST)
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
      model.setViewName("redirect:/2a8fy7b07dxe44");
    }

    model.addObject("user", user);

    return model;
  }

  @RequestMapping({"/2a8fy7b07dxe44/editUser", "/2a8fy7b07dxe44/userInfo"})
  public ModelAndView editUser(@RequestParam int id, HttpSession httpSession, HttpServletRequest request) {

    ModelAndView model = new ModelAndView();

    model.addObject("statusList", UserStatus.values());
    List<UserRole> roleList = new ArrayList<>();
    final Object mutex = WebUtils.getSessionMutex(httpSession);
    String currentRole = "";
    synchronized (mutex) {
      currentRole = (String) httpSession.getAttribute("currentRole");
    }
    if (currentRole.equals(UserRole.ADMINISTRATOR.name())) {
      roleList = userRoleService.getRolesAvailableForChangeByAdmin();
    }
    model.addObject("roleList", roleList);
    User user = userService.getUserById(id);
    user.setId(id);
    model.addObject("user", user);
    model.addObject("roleSettings", userRoleService.retrieveSettingsForRole(user.getRole().getRole()));
    model.addObject("currencies", currencyService.findAllCurrencies());
    model.addObject("currencyPairs", currencyService.getAllCurrencyPairs());
    model.setViewName("admin/editUser");
    model.addObject("userFiles", userService.findUserDoc(id));
    model.addObject("transactionTypes", Arrays.asList(TransactionType.values()));
    List<Merchant> merchantList = merchantService.findAll();
    model.addObject("merchants", merchantList);
    model.addObject("maxAmount", transactionService.maxAmount());
    model.addObject("maxCommissionAmount", transactionService.maxCommissionAmount());
    Set<String> allowedAuthorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
        .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    AuthorityOptionsForm form = new AuthorityOptionsForm();
    form.setUserId(id);
    form.setOptions(userService.getAuthorityOptionsForUser(id, allowedAuthorities, localeResolver.resolveLocale(request)));
    model.addObject("authorityOptionsForm", form);
    model.addObject("userActiveAuthorityOptions", userService.getActiveAuthorityOptionsForUser(id).stream().map(e -> e.getAdminAuthority().name()).collect(Collectors.joining(",")));
    model.addObject("userLang", userService.getPreferedLang(id).toUpperCase());
    model.addObject("usersInvoiceRefillCurrencyPermissions", currencyService.findWithOperationPermissionByUserAndDirection(user.getId(), REFILL));
    model.addObject("usersInvoiceWithdrawCurrencyPermissions", currencyService.findWithOperationPermissionByUserAndDirection(user.getId(), WITHDRAW));
    model.addObject("enable_2fa", userService.getUse2Fa(user.getEmail()));
    return model;
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/editUser/submit2faOptions", method = POST)
  public String submitNotificationOptions(@RequestParam String email,
                                          HttpServletRequest request, HttpServletResponse response) {
    boolean use2fa = String.valueOf(request.getParameter("enable_2fa")).equals("on");
    try {
      userService.setUse2Fa(email, use2fa);
    } catch (Exception e) {
      log.error(e);
      response.setStatus(400);
      return "error";
    }
    return "ok";
  }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/set2fa", method = POST)
    public String setGlobal2fa(HttpServletRequest request, HttpServletResponse response) {
        boolean use2fa = String.valueOf(request.getParameter("enable_2fa")).equals("on");
        try {
            userService.setGlobal2FaActive(use2fa);
        } catch (Exception e) {
            log.error(e);
            response.setStatus(400);
            return "error";
        }
        return "ok";
    }

  @RequestMapping(value = "/2a8fy7b07dxe44/edituser/submit", method = RequestMethod.POST)
  public ModelAndView submitedit(@Valid @ModelAttribute User user, BindingResult result, ModelAndView model, HttpServletRequest request, HttpServletResponse response,
                                 HttpSession httpSession) {
    final Object mutex = WebUtils.getSessionMutex(httpSession);
    String currentRole = "";
    synchronized (mutex) {
      currentRole = (String) httpSession.getAttribute("currentRole");
    }
    if (!currentRole.equals(UserRole.ADMINISTRATOR.name()) && user.getRole() == ADMINISTRATOR) {
      return new ModelAndView("403");
    }
    user.setConfirmPassword(user.getPassword());
    if (user.getFinpassword() == null) {
      user.setFinpassword("");
    }
        /**/
    registerFormValidation.validateEditUser(user, result, localeResolver.resolveLocale(request));
    if (result.hasErrors()) {
      model.setViewName("admin/editUser");
      model.addObject("statusList", UserStatus.values());
      if (currentRole.equals(ADMINISTRATOR.name())) {
        model.addObject("roleList", userRoleService.getRolesAvailableForChangeByAdmin());
      }
    } else {
      UpdateUserDto updateUserDto = new UpdateUserDto(user.getId());
      updateUserDto.setEmail(user.getEmail());
      updateUserDto.setPassword(user.getPassword());
      updateUserDto.setPhone(user.getPhone());
      if (currentRole.equals(UserRole.ADMINISTRATOR.name())) {
        updateUserDto.setRole(user.getRole());
      }
      updateUserDto.setStatus(user.getUserStatus());
      userService.updateUserByAdmin(updateUserDto);
      if (updateUserDto.getStatus() == UserStatus.DELETED) {
        invalidateUserSession(updateUserDto.getEmail());
      } else if (updateUserDto.getStatus() == UserStatus.BANNED_IN_CHAT) {
        notificationService.notifyUser(user.getEmail(), NotificationEvent.ADMIN, "account.bannedInChat.title", "dashboard.onlinechatbanned", null);
      }

      model.setViewName("redirect:/2a8fy7b07dxe44");
    }
        /**/
    model.addObject("user", user);
        /**/
    return model;
  }

  private void invalidateUserSession(String userEmail) {
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

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/orderinfo", method = RequestMethod.GET)
  public AdminOrderInfoDto getOrderInfo(@RequestParam int id, HttpServletRequest request) {
    return orderService.getAdminOrderInfo(id, localeResolver.resolveLocale(request));
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/stopOrderinfo", method = RequestMethod.GET)
  public OrderInfoDto getStopOrderInfo(@RequestParam int id, HttpServletRequest request) {
    return stopOrderService.getStopOrderInfo(id, localeResolver.resolveLocale(request));
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/transferInfo", method = RequestMethod.GET)
  public UserTransferInfoDto getTransferInfo(@RequestParam int id, HttpServletRequest request) {
    return userTransferService.getTransferInfoBySourceId(id);
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/orderdelete", method = RequestMethod.POST)
  public Integer deleteOrderByAdmin(@RequestParam int id) {
    try {
      return (Integer) orderService.deleteOrderByAdmin(id);
    } catch (Exception e) {
      LOG.error(e);
      throw e;
    }
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/order/accept", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public Map<String, Object> acceptOrderByAdmin(@RequestParam int id, Principal principal, Locale locale) {
    orderService.acceptOrderByAdmin(principal.getName(), id, locale);
    return Collections.singletonMap("result", messageSource.getMessage("admin.order.acceptsuccess", new Object[]{id}, locale));
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/stopOrderDelete", method = RequestMethod.POST)
  public boolean deleteStopOrderByAdmin(@RequestParam int id, HttpServletRequest request) {
    try {
      return (boolean) stopOrderService.deleteOrderByAdmin(id, localeResolver.resolveLocale(request));
    } catch (Exception e) {
      LOG.error(e);
      throw e;
    }
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/searchorders", method = RequestMethod.GET)
  public DataTable<List<OrderBasicInfoDto>> searchOrderByAdmin(AdminOrderFilterData adminOrderFilterData,
                                                               @RequestParam Map<String, String> params,
                                                               HttpServletRequest request) {

    try {
      adminOrderFilterData.initFilterItems();
      DataTableParams dataTableParams = DataTableParams.resolveParamsFromRequest(params);
      DataTable<List<OrderBasicInfoDto>> orderInfo = orderService.searchOrdersByAdmin(adminOrderFilterData, dataTableParams,
          localeResolver.resolveLocale(request));
      return orderInfo;
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
      DataTable<List<OrderBasicInfoDto>> errorResult = new DataTable<>();
      errorResult.setError(ex.getMessage());
      errorResult.setData(Collections.EMPTY_LIST);
      return errorResult;
    }
  }

  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/searchStopOrders", method = RequestMethod.GET)
  public DataTable<List<OrderBasicInfoDto>> searchStopOrderByAdmin(AdminStopOrderFilterData adminOrderFilterData,
                                                                   @RequestParam Map<String, String> params,
                                                                   HttpServletRequest request) {

    try {
      adminOrderFilterData.initFilterItems();
      DataTableParams dataTableParams = DataTableParams.resolveParamsFromRequest(params);
      DataTable<List<OrderBasicInfoDto>> orderInfo = stopOrderService.searchOrdersByAdmin(adminOrderFilterData, dataTableParams,
          localeResolver.resolveLocale(request));
      return orderInfo;
    } catch (Exception ex) {
      LOG.error(ex.getMessage(), ex);
      DataTable<List<OrderBasicInfoDto>> errorResult = new DataTable<>();
      errorResult.setError(ex.getMessage());
      errorResult.setData(Collections.EMPTY_LIST);
      return errorResult;
    }

  }

  @RequestMapping("/2a8fy7b07dxe44/userswallets")
  public ModelAndView showUsersWalletsSummary(Principal principal) {
    Integer requesterUserId = userService.getIdByEmail(principal.getName());
    Map<String, List<UserWalletSummaryDto>> mapUsersWalletsSummaryList = new LinkedHashMap<>();
    List<UserWalletSummaryDto> fullResult = walletService.getUsersWalletsSummaryForPermittedCurrencyList(requesterUserId);
    /**/
    List<UserWalletSummaryDto> allFiltered = getSublistForRole(fullResult, "ALL");
    List<UserWalletSummaryDto> adminFiltered = getSublistForRole(fullResult, "ADMIN");
    List<UserWalletSummaryDto> userFiltered = getSublistForRole(fullResult, "USER");
    List<UserWalletSummaryDto> exchangeFiltered = getSublistForRole(fullResult, "EXCHANGE");
    List<UserWalletSummaryDto> vipUserFiltered = getSublistForRole(fullResult, "VIP_USER");
    List<UserWalletSummaryDto> traderFiltered = getSublistForRole(fullResult, "TRADER");
    /**/
    mapUsersWalletsSummaryList.put("ALL", allFiltered);
    mapUsersWalletsSummaryList.put("ADMIN", adminFiltered);
    mapUsersWalletsSummaryList.put("USER", userFiltered);
    mapUsersWalletsSummaryList.put("EXCHANGE", exchangeFiltered);
    mapUsersWalletsSummaryList.put("VIP_USER", vipUserFiltered);
    mapUsersWalletsSummaryList.put("TRADER", traderFiltered);
    /**/
    ModelAndView model = new ModelAndView();
    model.setViewName("UsersWallets");
    model.addObject("mapUsersWalletsSummaryList", mapUsersWalletsSummaryList);
    Set<String> usersCurrencyPermittedList = new LinkedHashSet<String>() {{
      add("ALL");
    }};
    usersCurrencyPermittedList.addAll(currencyService.getCurrencyPermittedNameList(requesterUserId));
    model.addObject("usersCurrencyPermittedList", usersCurrencyPermittedList);
    List<String> operationDirectionList = Arrays.asList("ANY", InvoiceOperationDirection.REFILL.name(), InvoiceOperationDirection.WITHDRAW.name());
    model.addObject("operationDirectionList", operationDirectionList);

    return model;
  }

  private List<UserWalletSummaryDto> getSublistForRole(List<UserWalletSummaryDto> fullResult, String role) {
    List<Integer> realRoleList = userRoleService.getRealUserRoleIdByBusinessRoleList(role);
    List<UserWalletSummaryDto> roleFiltered = fullResult.stream()
        .filter(e -> realRoleList.isEmpty() || realRoleList.contains(e.getUserRoleId()))
        .collect(Collectors.toList());
    List<UserWalletSummaryDto> result = new ArrayList<>();
    for (UserWalletSummaryDto item : roleFiltered) {
      if (!result.contains(item)) {
        result.add(new UserWalletSummaryDto(item));
      } else {
        UserWalletSummaryDto storedItem = result.stream().filter(e -> e.equals(item)).findAny().get();
        storedItem.increment(item);
      }
    }
    result.forEach(UserWalletSummaryDto::calculate);
    return result;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/userStatements/{walletId}")
  public ModelAndView accountStatementPage(@PathVariable("walletId") Integer walletId) {
    return new ModelAndView("/admin/user_statement", "walletId", walletId);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/getStatements", method = RequestMethod.GET)
  @ResponseBody
  public DataTable<List<AccountStatementDto>> getStatements(@RequestParam Integer walletId, @RequestParam Map<String, String> params,
                                                            HttpServletRequest request) {
    Integer offset = Integer.parseInt(params.getOrDefault("start", "0"));
    Integer limit = Integer.parseInt(params.getOrDefault("length", "-1"));
    return transactionService.getAccountStatementForAdmin(walletId, offset, limit, localeResolver.resolveLocale(request));
  }


 /* @RequestMapping(value = "/2a8fy7b07dxe44/invoiceConfirmation")
  public ModelAndView invoiceTransactions(Principal principal) {
    Integer requesterUserId = userService.getIdByEmail(principal.getName());
    return new ModelAndView("admin/transaction_invoice");
  }
*/

  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinConfirmation")
  public ModelAndView bitcoinTransactions() {
    return new ModelAndView("admin/transaction_bitcoin");
  }


  private BitcoinService findAnyBitcoinServiceBean() {
    return bitcoinLikeServices.entrySet().stream().findAny().orElseThrow(NoRequestedBeansFoundException::new).getValue();
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/sessionControl")
  public ModelAndView sessionControl() {
    return new ModelAndView("admin/sessionControl");
  }


  @RequestMapping(value = "/2a8fy7b07dxe44/userSessions")
  @ResponseBody
  public List<UserSessionDto> retrieveUserSessionInfo() {
    List<UserSessionDto> result = null;
    try {
      Map<String, String> usersSessions = sessionRegistry.getAllPrincipals().stream()
          .flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
          .collect(Collectors.toMap(SessionInformation::getSessionId, sessionInformation -> {
            UserDetails user = (UserDetails) sessionInformation.getPrincipal();
            return user.getUsername();
          }));
      log.debug("USsize ", + usersSessions.size());
      Map<String, UserSessionInfoDto> userSessionInfo = userService.getUserSessionInfo(usersSessions.values().stream().collect(Collectors.toSet()))
          .stream().collect(Collectors.toMap(UserSessionInfoDto::getUserEmail, userSessionInfoDto -> userSessionInfoDto));
      log.debug("USinfosize ", + userSessionInfo.size());
      result = usersSessions.entrySet().stream()
          .map(entry -> {
            UserSessionDto dto = new UserSessionDto(userSessionInfo.get(entry.getValue()), entry.getKey());
            return dto;
          }).collect(Collectors.toList());
    } catch (Exception e) {
      log.error("session_error {}", e);
    }
    return result;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/expireSession", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<String> expireSession(@RequestParam String sessionId) {
    SessionInformation sessionInfo = sessionRegistry.getSessionInformation(sessionId);
    if (sessionInfo == null) {
      return new ResponseEntity<>("Sesion not found", HttpStatus.NOT_FOUND);
    }
    sessionInfo.expireNow();
    return new ResponseEntity<>("Session " + sessionId + " expired", HttpStatus.OK);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits", method = RequestMethod.GET)
  public ModelAndView currencyLimits() {
    ModelAndView modelAndView = new ModelAndView("admin/currencyLimits");
    modelAndView.addObject("roleNames", BusinessUserRoleEnum.values());
    modelAndView.addObject("operationTypes", Arrays.asList(OperationType.INPUT.name(), OperationType.OUTPUT.name(), OperationType.USER_TRANSFER.name()));
    modelAndView.addObject("orderTypes", OrderType.values());
    return modelAndView;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits/retrieve", method = RequestMethod.GET)
  @ResponseBody
  public List<CurrencyLimit> retrieveCurrencyLimits(@RequestParam String roleName,
                                                    @RequestParam OperationType operationType) {
    return currencyService.retrieveCurrencyLimitsForRole(roleName, operationType);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits/submit", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Void> editCurrencyLimit(@RequestParam int currencyId,
                                                @RequestParam OperationType operationType,
                                                @RequestParam String roleName,
                                                @RequestParam BigDecimal minAmount,
                                                @RequestParam Integer maxDailyRequest) {

    currencyService.updateCurrencyLimit(currencyId, operationType, roleName, minAmount, maxDailyRequest);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits/pairs/retrieve", method = RequestMethod.GET)
  @ResponseBody
  public List<CurrencyPairLimitDto> retrieveCurrencyPairLimits(@RequestParam String roleName,
                                                               @RequestParam OrderType orderType) {
    return currencyService.findAllCurrencyLimitsForRoleAndType(roleName, orderType);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyLimits/pairs/submit", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Void> editCurrencyPairLimit(@RequestParam int currencyPairId,
                                                    @RequestParam OrderType orderType,
                                                    @RequestParam String roleName,
                                                    @RequestParam BigDecimal minRate,
                                                    @RequestParam BigDecimal maxRate) {
    if (!BigDecimalProcessing.isNonNegative(minRate) || !BigDecimalProcessing.isNonNegative(maxRate) || minRate.compareTo(maxRate) >= 0) {
      throw new InvalidNumberParamException("Invalid request params!");
    }
    currencyService.updateCurrencyPairLimit(currencyPairId, orderType, roleName, minRate, maxRate);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editAuthorities/submit", method = RequestMethod.POST)
  public RedirectView editAuthorities(@ModelAttribute AuthorityOptionsForm authorityOptionsForm, Principal principal,
                                      RedirectAttributes redirectAttributes) {
    RedirectView redirectView = new RedirectView("/2a8fy7b07dxe44/userInfo?id=" + authorityOptionsForm.getUserId());
    try {
      userService.updateAdminAuthorities(authorityOptionsForm.getOptions(), authorityOptionsForm.getUserId(), principal.getName());
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("errorNoty", e.getMessage());
      return redirectView;
    }
    String updatedUserEmail = userService.getUserById(authorityOptionsForm.getUserId()).getEmail();
    sessionRegistry.getAllPrincipals().stream()
        .filter(currentPrincipal -> ((UserDetails) currentPrincipal).getUsername().equals(updatedUserEmail))
        .findFirst()
        .ifPresent(updatedUser -> sessionRegistry.getAllSessions(updatedUser, false).forEach(SessionInformation::expireNow));
    return redirectView;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/changeActiveBalance/submit", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Void> changeActiveBalance(@RequestParam Integer userId, @RequestParam("currency") Integer currencyId,
                                                  @RequestParam BigDecimal amount) {
    LOG.debug("userId = " + userId + ", currencyId = " + currencyId + "? amount = " + amount);
    walletService.manualBalanceChange(userId, currencyId, amount);
    return new ResponseEntity<>(HttpStatus.OK);

  }


  @RequestMapping(value = "/2a8fy7b07dxe44/commissions", method = RequestMethod.GET)
  public ModelAndView commissions() {
    ModelAndView modelAndView = new ModelAndView("admin/editCommissions");
    modelAndView.addObject("roleNames", BusinessUserRoleEnum.values());
    return modelAndView;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/getCommissionsForRole", method = RequestMethod.GET)
  @ResponseBody
  public List<CommissionShortEditDto> retrieveCommissionsForRole(@RequestParam String role, HttpServletRequest request) {
    return commissionService.getEditableCommissionsByRole(role, localeResolver.resolveLocale(request));

  }

  @RequestMapping(value = "/2a8fy7b07dxe44/getMerchantCommissions", method = RequestMethod.GET)
  @ResponseBody
  public List<MerchantCurrencyOptionsDto> retrieveMerchantCommissions() {
    return merchantService.findMerchantCurrencyOptions();

  }

  @RequestMapping(value = "/2a8fy7b07dxe44/commissions/editCommission", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Void> editCommission(@RequestParam("operationType") OperationType operationType,
                                             @RequestParam("userRole") String role,
                                             @RequestParam("commissionValue") BigDecimal value) {
    LOG.debug("operationType = " + operationType + ", userRole = " + role + ", value = " + value);
    commissionService.updateCommission(operationType, role, value);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/commissions/editMerchantCommission", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Void> editMerchantCommission(EditMerchantCommissionDto editMerchantCommissionDto) {
    commissionService.updateMerchantCommission(editMerchantCommissionDto);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/merchantAccess", method = RequestMethod.GET)
  public ModelAndView merchantAccess() {
    return new ModelAndView("admin/merchantAccess");
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/merchantAccess/data", method = RequestMethod.GET)
  @ResponseBody
  public List<MerchantCurrencyOptionsDto> merchantAccessData() {
    List<MerchantCurrencyOptionsDto> merchantCurrencyOptions = merchantService.findMerchantCurrencyOptions();
    LOG.debug(merchantCurrencyOptions);
    return merchantCurrencyOptions;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/merchantAccess/autoWithdrawParams", method = RequestMethod.POST, consumes = "application/json")
  @ResponseBody
  public void setAutoWithdrawParams(@RequestBody MerchantCurrencyOptionsDto merchantCurrencyOptionsDto) {
    if (merchantCurrencyOptionsDto.getWithdrawAutoEnabled() == null) {
      merchantCurrencyOptionsDto.setWithdrawAutoEnabled(false);
    }
    withdrawService.setAutoWithdrawParams(merchantCurrencyOptionsDto);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/merchantAccess/toggleBlock", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Void> toggleBlock(@RequestParam Integer merchantId,
                                          @RequestParam Integer currencyId,
                                          @RequestParam OperationType operationType) {
    LOG.debug("merchantId = " + merchantId + ", currencyId = " + currencyId + ", operationType = " + operationType);
    merchantService.toggleMerchantBlock(merchantId, currencyId, operationType);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/merchantAccess/setBlockForAll", method = RequestMethod.POST)
  @ResponseBody
  public ResponseEntity<Void> switchBlockStatusForAll(@RequestParam OperationType operationType,
                                                      @RequestParam boolean blockStatus) {
    merchantService.setBlockForAll(operationType, blockStatus);
    return new ResponseEntity<>(HttpStatus.OK);
  }


  @ResponseBody
  @RequestMapping(value = "/2a8fy7b07dxe44/phrases/{topic:.+}", method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Map<String, List<String>> getPhrases(
      @PathVariable String topic,
      @RequestParam String email) {
    Locale userLocale = Locale.forLanguageTag(userService.getPreferedLangByEmail(email));
    UserCommentTopicEnum userCommentTopic = UserCommentTopicEnum.convert(topic.toUpperCase());
    List<String> phrases = phraseTemplateService.getAllByTopic(userCommentTopic).stream()
        .map(e -> messageSource.getMessage(e, null, userLocale))
        .collect(Collectors.toList());
    return new HashMap<String, List<String>>() {{
      put("lang", Arrays.asList(userLocale.getISO3Language()));
      put("list", phrases);
    }};
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/editCurrencyPermissions/submit", method = RequestMethod.POST, consumes = "application/json")
  @ResponseBody
  public void editCurrencyPermissions(
      @RequestBody List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList,
      HttpSession httpSession,
      Principal principal) {
    userService.setCurrencyPermissionsByUserId(userCurrencyOperationPermissionDtoList);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/candleTable", method = RequestMethod.GET)
  public ModelAndView candleChartTable() {
    return new ModelAndView("/admin/candleTable", "currencyPairs", currencyService.getAllCurrencyPairs());
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/getCandleTableData", method = RequestMethod.GET)
  @ResponseBody
  public List<CandleChartItemDto> getCandleChartData(@RequestParam("currencyPair") Integer currencyPairId,
                                                     @RequestParam("interval") String interval,
                                                     @RequestParam("startTime") String startTimeString) {
    CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
    BackDealInterval backDealInterval = new BackDealInterval(interval);
    LocalDateTime startTime = LocalDateTime.parse(startTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    return orderService.getDataForCandleChart(currencyPair, backDealInterval, startTime);
  }

  private BitcoinService getBitcoinServiceByMerchantName(String merchantName) {
    String serviceBeanName = merchantService.findByName(merchantName).getServiceBeanName();
    IMerchantService merchantService = serviceContext.getMerchantService(serviceBeanName);
    if (merchantService == null || !(merchantService instanceof BitcoinService)) {
      throw new NoRequestedBeansFoundException(serviceBeanName);
    }
    return (BitcoinService) merchantService;
  }
  

  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}", method = RequestMethod.GET)
  public ModelAndView bitcoinWallet(@PathVariable String merchantName, Locale locale) {
    ModelAndView modelAndView = new ModelAndView("/admin/btcWallet");
    modelAndView.addObject("merchant", merchantName);
    String currency = merchantService.retrieveCoreWalletCurrencyNameByMerchant(merchantName);
    modelAndView.addObject("currency", currency);
    modelAndView.addObject("title", messageSource.getMessage(currency.toLowerCase() + "Wallet.title", null, locale));
    modelAndView.addObject("walletInfo", getBitcoinServiceByMerchantName(merchantName).getWalletInfo());
    return modelAndView;
  }
  
  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/transactions", method = RequestMethod.GET)
  @ResponseBody
  public List<BtcTransactionHistoryDto> getBtcTransactions(@PathVariable String merchantName) {
    return getBitcoinServiceByMerchantName(merchantName).listAllTransactions();
  }
  
  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/estimatedFee", method = RequestMethod.GET)
  @ResponseBody
  public BigDecimal getEstimatedFee(@PathVariable String merchantName) {
    return getBitcoinServiceByMerchantName(merchantName).estimateFee(6);
  }
  
  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/actualFee", method = RequestMethod.GET)
  @ResponseBody
  public BigDecimal getActualFee(@PathVariable String merchantName) {
    return getBitcoinServiceByMerchantName(merchantName).getActualFee();
  }
  
  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/setFee", method = RequestMethod.POST)
  @ResponseBody
  public void setFee(@PathVariable String merchantName, @RequestParam BigDecimal fee) {
    getBitcoinServiceByMerchantName(merchantName).setTxFee(fee);
  }
  
  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/unlock", method = RequestMethod.POST)
  @ResponseBody
  public void submitPassword(@PathVariable String merchantName, @RequestParam String password) {
    getBitcoinServiceByMerchantName(merchantName).submitWalletPassword(password);
  }
  
  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/sendToMany", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
          produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public Map<String, String> sendToMany(@PathVariable String merchantName,
                                        @RequestBody Map<String, BigDecimal> addresses, HttpServletRequest request) {
    LOG.debug(addresses);
    BitcoinService walletService = getBitcoinServiceByMerchantName(merchantName);
    String txId = walletService.sendToMany(addresses);
    Map<String, String> result = new HashMap<>();
    result.put("message", messageSource.getMessage("btcWallet.successResult", new Object[]{txId}, localeResolver.resolveLocale(request)));
    result.put("newBalance", walletService.getWalletInfo().getBalance());
    return result;
  }
  
  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/transaction/details", method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public Map<String, RefillRequestBtcInfoDto> getTransactionDetails(@PathVariable String merchantName,
                                        @RequestParam("currency") String currencyName,
                                        @RequestParam String hash,
                                        @RequestParam String address) {
   Optional<RefillRequestBtcInfoDto> dtoResult = refillService.findRefillRequestByAddressAndMerchantTransactionId(address, hash,
           merchantName, currencyName);
    return dtoResult.isPresent() ? Collections.singletonMap("result", dtoResult.get()) : Collections.EMPTY_MAP;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/bitcoinWallet/{merchantName}/transaction/create", method = RequestMethod.POST)
  @ResponseBody
  public void createBtcRefillRequest(@PathVariable String merchantName, @RequestParam Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
    LOG.debug(params);
    getBitcoinServiceByMerchantName(merchantName).processPayment(params);
  }
  

  @RequestMapping(value = "/2a8fy7b07dxe44/findReferral")
  @ResponseBody
  public RefsListContainer findUserReferral(@RequestParam("action") String action,
                                            @RequestParam(value = "userId", required = false) Integer userId,
                                            @RequestParam("profitUser") int profitUser,
                                            @RequestParam(value = "onPage", defaultValue = "20") int onPage,
                                            @RequestParam(value = "page", defaultValue = "1") int page,
                                            RefFilterData refFilterData) {
    LOG.error("filter data " + refFilterData);
    return referralService.getRefsContainerForReq(action, userId, profitUser, onPage, page, refFilterData);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/downloadRef")
  public void downloadUserRefferalStructure(@RequestParam("profitUser") int profitUser,
                                            RefFilterData refFilterData,
                                            HttpServletResponse response) throws IOException {
    response.setContentType("text/csv");
    String reportName =
        "referrals-"
            .concat(userService.getEmailById(profitUser))
            .concat(".csv");
    response.setHeader("Content-disposition", "attachment;filename=" + reportName);
    List<String> refsList = referralService.getRefsListForDownload(profitUser, refFilterData);
    OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
    try {
      for (String transaction : refsList) {
        writer.write(transaction);
      }
    } catch (IOException e) {
      LOG.error("error download transactions " + e);
    } finally {
      writer.flush();
      writer.close();
    }
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading", method = GET)
  public ModelAndView autoTrading() {
    ModelAndView modelAndView = new ModelAndView("/admin/autoTrading");
    botService.retrieveBotFromDB().ifPresent(bot -> {
      modelAndView.addObject("bot", bot);
      modelAndView.addObject("botUser", userService.getUserById(bot.getUserId()));
    });
    return modelAndView;
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/roleSettings", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public List<UserRoleSettings> getRoleSettings() {
    return userRoleService.retrieveSettingsForAllRoles();
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/roleSettings/update", method = POST/*, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE*/)
  @ResponseBody
  public void updateSettingsForRole(@RequestBody UserRoleSettings userRoleSettings) {
    userRoleService.updateSettingsForRole(userRoleSettings);
  }

  @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/bot/create", method = POST)
  @ResponseBody
  public void createBot(@RequestParam String nickname, @RequestParam String email, @RequestParam String password) {
    botService.createBot(nickname, email, password);
  }
  @RequestMapping(value = "/2a8fy7b07dxe44/autoTrading/bot/update", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  @ResponseBody
  public void updateBot(@RequestBody @Valid BotTrader botTrader) {
    botService.updateBot(botTrader);

  }



  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler(OrderDeletingException.class)
  @ResponseBody
  public ErrorInfo OrderDeletingExceptionHandler(HttpServletRequest req, Exception exception) {
    return new ErrorInfo(req.getRequestURL(), exception);
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ExceptionHandler(NoPermissionForOperationException.class)
  @ResponseBody
  public ErrorInfo userNotEnabledExceptionHandler(HttpServletRequest req, Exception exception) {
    return new ErrorInfo(req.getRequestURL(), exception);
  }
  
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler({NotEnoughMoneyException.class, NotEnoughUserWalletMoneyException.class, OrderCreationException.class,
          OrderAcceptionException.class, OrderCancellingException.class, NotAcceptableOrderException.class,
          NotCreatableOrderException.class})
  @ResponseBody
  public ErrorInfo orderExceptionHandler(HttpServletRequest req, Exception exception) {
    return new ErrorInfo(req.getRequestURL(), exception);
  }

  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseBody
  public ErrorInfo methodArgumentNotValidExceptionHandler(HttpServletRequest req, MethodArgumentNotValidException ex) {
    return new ErrorInfo(req.getRequestURL(), ex);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
    LOG.error(exception);
    exception.printStackTrace();
    return new ErrorInfo(req.getRequestURL(), exception);
  }
  
  


}