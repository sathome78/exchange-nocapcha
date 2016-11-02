package me.exrates.controller;


import me.exrates.controller.exception.*;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.OrderCreateSummaryDto;
import me.exrates.model.dto.OrderInfoDto;
import me.exrates.model.dto.WalletsAndCommissionsForOrderCreationDto;
import me.exrates.model.enums.OperationType;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class OrderControllerRest {
    private static final Logger LOGGER = LogManager.getLogger(OrderControllerRest.class);



    @Autowired
    OrderService orderService;

    @Autowired
    CommissionService commissionService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    WalletService walletService;

    @Autowired
    UserService userService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    LocaleResolver localeResolver;

    @RequestMapping("/order/submitnew/{orderType}")
    public OrderCreateSummaryDto newOrderToSell(@PathVariable OperationType orderType,
                                                Principal principal,
                                                BigDecimal amount,
                                                BigDecimal rate,
                                                HttpServletRequest request) {
        long before = System.currentTimeMillis();
        try {
            OrderCreateSummaryDto orderCreateSummaryDto;
            if (amount == null) amount = BigDecimal.ZERO;
            if (rate == null) rate = BigDecimal.ZERO;
            CurrencyPair activeCurrencyPair = (CurrencyPair) request.getSession().getAttribute("currentCurrencyPair");
            OrderCreateDto orderCreateDto = orderService.prepareNewOrder(activeCurrencyPair, orderType, principal.getName(), amount, rate);
        /**/
            Map<String, Object> result = orderService.validateOrder(orderCreateDto);
            orderCreateSummaryDto = new OrderCreateSummaryDto(orderCreateDto, localeResolver.resolveLocale(request));
            if (!result.isEmpty()) {
                for (Map.Entry<String, Object> pair : result.entrySet()) {
                    pair.setValue(messageSource.getMessage((String) pair.getValue(), null, localeResolver.resolveLocale(request)));
                }
                result.put("order", orderCreateSummaryDto);
                request.getSession().setAttribute("orderCreationError", result);
                throw new OrderParamsWrongException();
            } else {
            /*protect orderCreateDto*/
                request.getSession().setAttribute("/order/submitnew/orderCreateDto", orderCreateDto);
            }
            return orderCreateSummaryDto;
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw e;
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }

    }

    @RequestMapping(value = "/order/create", produces = "application/json;charset=utf-8")
    public String recordOrderToDB(HttpServletRequest request) {
        long before = System.currentTimeMillis();
        try {
            try {
            /*restore protected orderCreateDto*/
                OrderCreateDto orderCreateDto = (OrderCreateDto) request.getSession().getAttribute("/order/submitnew/orderCreateDto");
            /*clear orderCreateDto: it's necessary to prevent re-creating order from the current submit form */
                request.getSession().removeAttribute("/order/submitnew/orderCreateDto");
                if (orderCreateDto == null) {
                    /*it may be if user twice click the create button from the current submit form. After first click orderCreateDto wil be cleaned*/
                    throw new OrderCreationException(messageSource.getMessage("order.recreateerror", null, localeResolver.resolveLocale(request)));
                }
                Optional<String> autoAcceptResult = orderService.autoAccept(orderCreateDto, localeResolver.resolveLocale(request));
                if (autoAcceptResult.isPresent()) {
                    return autoAcceptResult.get();
                }
                if ((orderService.createOrder(orderCreateDto)) <= 0) {
                    throw new NotCreatableOrderException(messageSource.getMessage("dberror.text", null, localeResolver.resolveLocale(request)));
                }
                return "{\"result\":\"" + messageSource.getMessage("createdorder.text", null, localeResolver.resolveLocale(request)) + "\"}";
            } catch (NotEnoughUserWalletMoneyException e) {
                throw new NotEnoughUserWalletMoneyException(messageSource.getMessage("validation.orderNotEnoughMoney", null, localeResolver.resolveLocale(request)));
            } catch (OrderCreationException e) {
                throw new OrderCreationException(messageSource.getMessage("order.createerror", new Object[]{e.getLocalizedMessage()}, localeResolver.resolveLocale(request)));
            } catch (NotCreatableOrderException e) {
                throw e;
            }
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw e;
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }
    }

    @RequestMapping(value = "/order/accept", produces = "application/json;charset=utf-8")
    public String acceptOrder(@RequestBody String ordersListString, Principal principal, HttpServletRequest request) {
        long before = System.currentTimeMillis();
        try {
            List<Integer> ordersList = Arrays.asList(ordersListString.split(" ")).stream().map(e -> Integer.valueOf(e)).collect(Collectors.toList());
            try {
                int userId = userService.getIdByEmail(principal.getName());
                orderService.acceptOrdersList(userId, ordersList, localeResolver.resolveLocale(request));
            } catch (Exception e) {
                throw e;
            }
            return "{\"result\":\"" + messageSource.getMessage("order.acceptsuccess", new Integer[]{ordersList.size()}, localeResolver.resolveLocale(request)) + "\"}";
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw e;
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }
    }


    @RequestMapping("/order/submitdelete/{orderId}")
    public OrderCreateSummaryDto submitDeleteOrder(@PathVariable Integer orderId, HttpServletRequest request) {
        long before = System.currentTimeMillis();
        try {
            OrderCreateDto orderCreateDto = orderService.getMyOrderById(orderId);
            if (orderCreateDto == null) {
                throw new OrderNotFoundException(messageSource.getMessage("orders.getordererror", new Object[]{orderId}, localeResolver.resolveLocale(request)));
            }
            request.getSession().setAttribute("/order/submitdelete/orderCreateDto", orderCreateDto);
            OrderCreateSummaryDto orderForDelete = new OrderCreateSummaryDto(orderCreateDto, localeResolver.resolveLocale(request));
            return orderForDelete;
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw e;
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }
    }

    @RequestMapping(value = "/order/delete", produces = "application/json;charset=utf-8")
    public String deleteOrder(HttpServletRequest request) {
        long before = System.currentTimeMillis();
        try {
            OrderCreateDto orderCreateDto = (OrderCreateDto) request.getSession().getAttribute("/order/submitdelete/orderCreateDto");
            request.getSession().removeAttribute("/order/submitdelete/orderCreateDto");
            if (orderCreateDto == null) {
                throw new OrderCreationException(messageSource.getMessage("order.redeleteerror", null, localeResolver.resolveLocale(request)));
            }
            if (!orderService.cancellOrder(new ExOrder(orderCreateDto), localeResolver.resolveLocale(request))) {
                throw new OrderCancellingException(messageSource.getMessage("myorders.deletefailed", null, localeResolver.resolveLocale(request)));
            }
            return "{\"result\":\"" + messageSource.getMessage("myorders.deletesuccess", null, localeResolver.resolveLocale(request)) + "\"}";
        } catch (Exception e) {
            long after = System.currentTimeMillis();
            LOGGER.error("error... ms: " + (after - before) + " : " + e);
            throw e;
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }
    }

    @RequestMapping(value = "/order/orderinfo", method = RequestMethod.GET)
    public OrderInfoDto getOrderInfo(@RequestParam int id, HttpServletRequest request) {
        long before = System.currentTimeMillis();
        try {
            return orderService.getOrderInfo(id, localeResolver.resolveLocale(request));
        } finally {
            long after = System.currentTimeMillis();
            LOGGER.debug("completed... ms: " + (after - before));
        }
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(OrderParamsWrongException.class)
    @ResponseBody
    public Map orderParamsWrongExceptionHandler(HttpServletRequest req, Exception exception) {
        return (Map) req.getSession().getAttribute("orderCreationError");
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
    @ExceptionHandler(NotEnoughUserWalletMoneyException.class)
    @ResponseBody
    public ErrorInfo NotEnoughUserWalletMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(OrderCreationException.class)
    @ResponseBody
    public ErrorInfo OrderCreationExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(OrderAcceptionException.class)
    @ResponseBody
    public ErrorInfo OrderAcceptionExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(OrderCancellingException.class)
    @ResponseBody
    public ErrorInfo OrderCancellingExceptionHandler(HttpServletRequest req, Exception exception) {
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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseBody
    public ErrorInfo OrderNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

}

