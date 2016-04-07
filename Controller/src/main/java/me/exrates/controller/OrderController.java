package me.exrates.controller;


import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import me.exrates.controller.exception.*;
import me.exrates.controller.validator.OrderValidator;
import me.exrates.model.Currency;
import me.exrates.model.Order;
import me.exrates.model.User;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserStatus;
import me.exrates.service.CommissionService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    CommissionService commissionService;

    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @Autowired
    OrderValidator orderValidator;

    @RequestMapping(value = "/orders")
    public ModelAndView myOrders(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        Map<String, List<Order>> orderMap = orderService.getAllOrders(localeResolver.resolveLocale(request));
        model.setViewName("orders");
        model.addObject("orderMap", orderMap);
        Order order = new Order();
        getCurrenciesAndCommission(model, OperationType.SELL);
        model.addObject(order);
        return model;
    }

    /*CHECK FIN PASSWORD*/

    @RequestMapping(value = "/checkfinpass", method = RequestMethod.POST)
    @ResponseBody
    public void checkFinPassword(User user, HttpServletRequest request) {
        String enteredFinPassword = user.getFinpassword();
        User storedUser = userService.getUserById(userService.getIdByEmail(user.getEmail()));
        boolean isNotConfirmedToken = userService.getTokenByUserAndType(storedUser, TokenType.CHANGE_FIN_PASSWORD).size()>0;
        if (isNotConfirmedToken) {
            throw new NotConfirmedFinPasswordException(messageSource.getMessage("admin.notconfirmedfinpassword", null, localeResolver.resolveLocale(request)));
        }
        String currentFinPassword = storedUser.getFinpassword();
        if (currentFinPassword == null || currentFinPassword.isEmpty()) {
            throw new AbsentFinPasswordException(messageSource.getMessage("admin.absentfinpassword", null, localeResolver.resolveLocale(request)));
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean authSuccess = passwordEncoder.matches(enteredFinPassword, currentFinPassword);
        if (! authSuccess) {
            throw new WrongFinPasswordException(messageSource.getMessage("admin.wrongfinpassword", null, localeResolver.resolveLocale(request)));
        }
    }

   /*ACCEPT ORDER ...*/

    /* check if enough money to accept
    * */
    @RequestMapping(value = "/orders/submitaccept/check")
    @ResponseBody
    public void checkSubmitAcceptOrder(@RequestParam int id, Principal principal, HttpServletRequest request) {
        int userId = userService.getIdByEmail(principal.getName());
        Order order = orderService.getOrderById(id);
        int userWalletIdForBuy = walletService.getWalletId(userId, order.getCurrencyBuy());
        if ((userWalletIdForBuy == 0) || !walletService.ifEnoughMoney(userWalletIdForBuy, order.getAmountBuy())) {
            throw new NotEnoughMoneyException(messageSource.getMessage("validation.orderNotEnoughMoney", null, localeResolver.resolveLocale(request)));
        }
    }

    /* after success checking for enough money to accept
    to pass to form for control and submit accept
    * */
    @RequestMapping(value = "/orders/submitaccept")
    public ModelAndView submitAcceptOrder(@RequestParam int id, ModelAndView model, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Order order = orderService.getOrderById(id);
        model.setViewName("submitacceptorder");
        model.addObject("order", order);
        return model;
    }

    /* after succes checking for enough money for accept
    and after submit accept
    try to fix operation in db. It's possible that error occures (for example because balance has changed)
    * */
    @RequestMapping(value = "/orders/accept")
    @ResponseBody
    public void acceptOrder(@RequestParam int id, ModelAndView model, Principal principal, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        int userId = userService.getIdByEmail(principal.getName());
        if (!orderService.acceptOrder(userId, id)) {
            throw new NotAcceptableOrderException(messageSource.getMessage("dberror.text", null, localeResolver.resolveLocale(request)));
        }
    }

    /*show message form after success accept
    * */
    @RequestMapping(value = "/orders/acceptordersuccess")
    public ModelAndView acceptOrderSuccess(ModelAndView model) {
        model.setViewName("acceptordersuccess");
        return model;
    }

    /*... ACCEPT ORDER*/

    /*CREATE ORDER....*/

    /* to show form to create order (to fill param fields)
    * */
    @RequestMapping(value = "/order/new")
    public ModelAndView showNewOrderToSellForm(ModelAndView model, @RequestParam(required = false) String walletName) {
        getCurrenciesAndCommission(model, OperationType.SELL);
        Order order = new Order();
        order.setOperationType(OperationType.SELL);
        model.setViewName("newordertosell");
        model.addObject(order);
        model.addObject("walletName", walletName);
        return model;
    }


    /* after filling fileds of creation form on orders page or on page newordertosell
    show form to submit new order if all fields are filled correct
    * */
    @RequestMapping(value = "/order/submit", method = RequestMethod.POST)
    public ModelAndView submitNewOrderToSell(@Valid @ModelAttribute Order order, BindingResult result, ModelAndView model, Principal principal, HttpServletRequest request) {
        orderValidator.validate(order, result, principal);
        getCurrenciesAndCommission(model, order.getOperationType());
        if (result.hasErrors()) {
            model.setViewName("newordertosell");
        } else {
            model.setViewName("submitorder");
        }
        model.addObject("order", order);
        return model;
    }

    /* after submit create order
    to try to fix operation in db. It's possible that error occures (for example because balance has changed)
    * */
    @RequestMapping(value = "/orders/create")
    @ResponseBody
    public void recordOrderToDB(Order order, Principal principal, HttpServletRequest request) {
        int walletIdFrom = walletService.getWalletId(userService.getIdByEmail(principal.getName()), order.getCurrencySell());
        order.setWalletIdSell(walletIdFrom);
        if ((orderService.createOrder(order)) <= 0) {
            throw new NotCreatableOrderException(messageSource.getMessage("dberror.text", null, localeResolver.resolveLocale(request)));
        }
    }

    /*to show message form after success accept
    * */
    @RequestMapping(value = "/orders/createordersuccess")
    public ModelAndView createOrderSuccess(ModelAndView model) {
        model.setViewName("ordercreated");
        return model;
    }

    /* ... CREATE ORDER*/

    @RequestMapping(value = "/order/edit", method = RequestMethod.POST)
    public ModelAndView showEditOrderToSellForm(ModelAndView model, @ModelAttribute Order order) {
        model = new ModelAndView("editorder", "order", order);
        getCurrenciesAndCommission(model, order.getOperationType());
        return model;
    }

    @RequestMapping("/myorders")
    public ModelAndView showMyOrders(Principal principal, ModelAndView model, HttpServletRequest request) {
        String email = principal.getName();
        Map<String, List<Order>> orderMap = orderService.getMyOrders(email, localeResolver.resolveLocale(request));
        model.addObject("orderMap", orderMap);
        return model;
    }

    @RequestMapping("/myorders/submitdelete")
    public ModelAndView submitDeleteOrder(@RequestParam int id, RedirectAttributes redirectAttributes, ModelAndView model) {
        Order order = orderService.getOrderById(id);
        model.setViewName("submitdeleteorder");
        model.addObject("order", order);
        return model;
    }

    @RequestMapping("/myorders/delete")
    public String deleteOrder(@RequestParam int id, RedirectAttributes redirectAttributes) {
        String msg = null;
        if (orderService.cancellOrder(id)) {
            msg = "delete";
        } else {
            msg = "deletefailed";
        }
        redirectAttributes.addFlashAttribute("msg", msg);
        return "redirect:/myorders";
    }


    @RequestMapping("DBError")
    public String DBerror() {
        return "DBError";
    }

    private void getCurrenciesAndCommission(ModelAndView model, OperationType type) {
        List<Currency> currList = walletService.getCurrencyList();
        BigDecimal commission = commissionService.findCommissionByType(type).getValue();
        model.addObject("currList", currList);
        model.addObject("commission", commission);
    }

    /*
    error handlers for this controller
    * */

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotEnoughMoneyException.class)
    @ResponseBody
    public ErrorInfo notEnoughMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotAcceptableOrderException.class)
    @ResponseBody
    public ErrorInfo NotAcceptableOrderExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotCreatableOrderException.class)
    @ResponseBody
    public ErrorInfo NotCreatableOrderExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(WrongFinPasswordException.class)
    @ResponseBody
    public ErrorInfo WrongFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(AbsentFinPasswordException.class)
    @ResponseBody
    public ErrorInfo AbsentFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(NotConfirmedFinPasswordException.class)
    @ResponseBody
    public ErrorInfo NotConfirmedFinPasswordExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }
}

