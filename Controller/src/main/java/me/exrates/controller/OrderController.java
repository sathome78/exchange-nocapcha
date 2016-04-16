package me.exrates.controller;


import me.exrates.controller.exception.*;
import me.exrates.controller.validator.OrderValidator;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.Order;
import me.exrates.model.User;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderListDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TokenType;
import me.exrates.service.CommissionService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

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
    public ModelAndView myOrders(Principal principal, HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        model.setViewName("orders");
        //
        List<OrderListDto> sellOrdersList = orderService.getOrdersSell();
        List<OrderListDto> buyOrdersList = orderService.getOrdersBuy();
        model.addObject("sellOrdersList", sellOrdersList);
        model.addObject("buyOrdersList", buyOrdersList);
        //
        int userId = userService.getIdByEmail(principal.getName());
        int currencyId1 = ((CurrencyPair) request.getSession().getAttribute("currentCurrencyPair")).getCurrency1().getId();
        int currencyId2 = ((CurrencyPair) request.getSession().getAttribute("currentCurrencyPair")).getCurrency2().getId();
        int walletIdCurrency1 = walletService.getWalletId(userId, currencyId1);
        int walletIdCurrency2 = walletService.getWalletId(userId, currencyId2);
        //
        OrderCreateDto orderCreateDto = new OrderCreateDto();
        orderCreateDto.setCurrencyPair((CurrencyPair) request.getSession().getAttribute("currentCurrencyPair"));
        orderCreateDto.setWalletIdCurrency1(walletIdCurrency1);
        orderCreateDto.setBalance1(walletService.getWalletABalance(walletIdCurrency1));
        orderCreateDto.setWalletIdCurrency2(walletIdCurrency2);
        orderCreateDto.setBalance2(walletService.getWalletABalance(walletIdCurrency2));
        orderCreateDto.setComissionForBuy(commissionService.findCommissionByType(OperationType.BUY).getValue());
        orderCreateDto.setComissionForSell(commissionService.findCommissionByType(OperationType.SELL).getValue());
        model.addObject("orderCreateDto", orderCreateDto);

        return model;
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

    /* after filling fields of creation form on orders page or on page newordertosell
    shows form to submit new order if all fields are filled correct
    * */
    @RequestMapping(value = "/order/submit", method = RequestMethod.POST)
    public ModelAndView submitNewOrderToSell(@Valid @ModelAttribute OrderCreateDto orderCreateDto,
                                             BindingResult result, ModelAndView model) {
        orderValidator.validate(orderCreateDto, result);
        if (result.hasErrors()) {
            model.setViewName("newordertosell");
        } else {
            //final amounts calculated here (not by javascript) and transfere to submit form
            OrderValidator.OrderSum orderSum = orderValidator.getCalculatedSum(orderCreateDto);
            orderCreateDto.setTotal(orderSum.total);
            orderCreateDto.setComission(orderSum.comission);
            orderCreateDto.setTotalWithComission(orderSum.totalWithComission);
            model.addObject("orderCreateDto", orderCreateDto);
            //
            model.setViewName("submitorder");
        }
        model.addObject("orderCreateDto", orderCreateDto);
        return model;
    }

    /* after submit create order
    to try to fix operation in db. It's possible that error occures (for example because balance has changed)
    * */
    @RequestMapping(value = "/orders/create")
    @ResponseBody
    public void recordOrderToDB(OrderCreateDto orderCreateDto, Principal principal, HttpServletRequest request) {
        CurrencyPair currencyPair = orderCreateDto.getCurrencyPair();
        Currency currencyForBuy = (orderCreateDto.getOperationType() == OperationType.BUY) ?
                currencyPair.getCurrency1() :
                currencyPair.getCurrency2();
        Currency currencyForSell = currencyPair.getAnotherCurrency(currencyForBuy);

        Order order = new Order();
        order.setOperationType(orderCreateDto.getOperationType());
        order.setExrate(orderCreateDto.getExchangeRate());
        order.setCurrencyBuy(currencyForBuy.getId());
        order.setCurrencySell(currencyForSell.getId());
        if (orderCreateDto.getOperationType() == OperationType.BUY) {
            order.setWalletIdBuy(orderCreateDto.getWalletIdCurrency1());
            order.setWalletIdSell(orderCreateDto.getWalletIdCurrency2());
            order.setAmountBuy(orderCreateDto.getAmount());
            order.setAmountSell(orderCreateDto.getTotal());
            order.setCommissionAmountBuy(orderCreateDto.getComission());
            //
            order.setCommissionAmountSell(orderCreateDto.getTotal().multiply(orderCreateDto.getComissionForSell().divide(new BigDecimal(100))));
        } else {
            order.setWalletIdSell(orderCreateDto.getWalletIdCurrency1());
            order.setWalletIdBuy(orderCreateDto.getWalletIdCurrency2());
            order.setAmountSell(orderCreateDto.getAmount());
            order.setAmountBuy(orderCreateDto.getTotal());
            order.setCommissionAmountBuy(orderCreateDto.getComission());
            //
            order.setCommissionAmountSell(orderCreateDto.getAmount().multiply(orderCreateDto.getComissionForBuy().divide(new BigDecimal(100))));
        }
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

    /*
    if need to edit created order before final submit
    * */
    @RequestMapping(value = "/order/edit", method = RequestMethod.POST)
    public ModelAndView showEditOrderToSellForm(@Valid @ModelAttribute OrderCreateDto orderCreateDto, ModelAndView model) {
        model.setViewName("newordertosell");
        model.addObject("orderCreateDto", orderCreateDto);
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

