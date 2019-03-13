package me.exrates.controller.advice;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.InvalidNumberParamException;
import me.exrates.model.UserFile;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.security.exception.BannedIpException;
import me.exrates.security.exception.MissingHeaderException;
import me.exrates.service.UserService;
import me.exrates.service.exception.AlreadyAcceptedOrderException;
import me.exrates.service.exception.AuthenticationNotAvailableException;
import me.exrates.service.exception.CallBackUrlAlreadyExistException;
import me.exrates.service.exception.CurrencyPairNotFoundException;
import me.exrates.service.exception.IncorrectCurrentUserException;
import me.exrates.service.exception.InsufficientCostsForAcceptionException;
import me.exrates.service.exception.NoPermissionForOperationException;
import me.exrates.service.exception.NotCreatableOrderException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.OrderAcceptionException;
import me.exrates.service.exception.OrderCancellingException;
import me.exrates.service.exception.OrderCreationException;
import me.exrates.service.exception.OrderDeletingException;
import me.exrates.service.exception.OrderNotFoundException;
import me.exrates.service.exception.UserNotFoundException;
import me.exrates.service.exception.UserOperationAccessException;
import me.exrates.service.exception.WalletCreationException;
import me.exrates.service.exception.WalletNotFoundException;
import me.exrates.service.exception.WalletPersistException;
import me.exrates.service.exception.api.CancelOrderException;
import me.exrates.service.exception.api.CommissionsNotFoundException;
import me.exrates.service.exception.api.CurrencyPairLimitNotFoundException;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.exception.api.InvalidCurrencyPairFormatException;
import me.exrates.service.exception.api.OpenApiError;
import me.exrates.service.exception.api.OrderParamsWrongException;
import me.exrates.service.exception.api.UserRoleNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


@Log4j2
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    private final MessageSource messageSource;
    private final UserService userService;

    @Autowired
    public GlobalControllerExceptionHandler(final MessageSource messageSource, final UserService userService) {
        this.messageSource = messageSource;
        this.userService = userService;
    }

    @ExceptionHandler(MultipartException.class)
    public ModelAndView handleMultipartConflict(final Locale locale) {
        final User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        final me.exrates.model.User user = userService.findByEmail(principal.getUsername());
        final ModelAndView mav = new ModelAndView("settings");
        final List<UserFile> userFiles = userService.findUserDoc(user.getId());
        mav.addObject("user", user);
        mav.addObject("errorNoty", messageSource.getMessage("admin.errorUploadFiles", null, locale));
        mav.addObject("userFiles", userFiles);
        return mav;
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(OrderDeletingException.class)
    @ResponseBody
    public ErrorInfo orderDeletingExceptionHandler(HttpServletRequest req, OrderDeletingException exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(NoPermissionForOperationException.class)
    @ResponseBody
    public ErrorInfo userNotEnabledExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingHeaderException.class)
    @ResponseBody
    public ErrorInfo handleMissingHeaderException(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NgResponseException.class)
    @ResponseBody
    public ErrorInfo handleNgUserAuthenticationException(HttpServletRequest req, NgResponseException exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    public ModelAndView OtherErrorsHandler(HttpServletRequest req, Exception exception) {
//        log.error("URL: " + req.getRequestURL() + " | Exception " + exception);
//        exception.printStackTrace();
//        return new ModelAndView("errorPages/generalErrorPage");
//    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(BannedIpException.class)
    @ResponseBody
    public ErrorInfo ipBannedExceptionHandler(HttpServletRequest request, BannedIpException exception) {
        String clientIp = request.getHeader("client_ip");
        String message = "Banned ip exception . Banned ip address " + clientIp + " for " + exception.getBanDurationSeconds() + " seconds";
        return new ErrorInfo(message, exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ErrorInfo HZExceptionHandler(Throwable exception) {
        String message = "Cause: " + exception.getLocalizedMessage();
        return new ErrorInfo(message, exception);
    }

    //Handlers for open API

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidCurrencyPairFormatException.class)
    @ResponseBody
    public OpenApiError invalidCurrencyPairFormatExceptionHandler(HttpServletRequest req, InvalidCurrencyPairFormatException exception) {
        return new OpenApiError(ErrorCode.INVALID_CURRENCY_PAIR_FORMAT, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidNumberParamException.class)
    @ResponseBody
    public OpenApiError invalidNumberParamExceptionHandler(HttpServletRequest req, InvalidNumberParamException exception) {
        return new OpenApiError(ErrorCode.INVALID_NUNBER_FORMAT, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(AlreadyAcceptedOrderException.class)
    @ResponseBody
    public OpenApiError alreadyAcceptedOrderExceptionHandler(HttpServletRequest req, AlreadyAcceptedOrderException exception) {
        return new OpenApiError(ErrorCode.ALREADY_ACCEPTED_ORDER, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(CurrencyPairNotFoundException.class)
    @ResponseBody
    public OpenApiError currencyPairNotFoundExceptionHandler(HttpServletRequest req, CurrencyPairNotFoundException exception) {
        return new OpenApiError(ErrorCode.CURRENCY_PAIR_NOT_FOUND, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public OpenApiError userNotFoundExceptionHandler(HttpServletRequest req, UserNotFoundException exception) {
        return new OpenApiError(ErrorCode.USER_NOT_FOUND, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(CommissionsNotFoundException.class)
    @ResponseBody
    public OpenApiError commissionsNotFoundExceptionHandler(HttpServletRequest req, CommissionsNotFoundException exception) {
        return new OpenApiError(ErrorCode.COMMISSIONS_NOT_FOUND, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(WalletNotFoundException.class)
    @ResponseBody
    public OpenApiError walletNotFoundExceptionHandler(HttpServletRequest req, WalletNotFoundException exception) {
        return new OpenApiError(ErrorCode.WALLET_NOT_FOUND, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(UserRoleNotFoundException.class)
    @ResponseBody
    public OpenApiError userRoleNotFoundExceptionHandler(HttpServletRequest req, UserRoleNotFoundException exception) {
        return new OpenApiError(ErrorCode.USER_ROLE_NOT_FOUND, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseBody
    public OpenApiError orderNotFoundExceptionHandler(HttpServletRequest req, OrderNotFoundException exception) {
        return new OpenApiError(ErrorCode.ORDER_NOT_FOUND, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(CurrencyPairLimitNotFoundException.class)
    @ResponseBody
    public OpenApiError currencyPairLimitNotFoundExceptionHandler(HttpServletRequest req, CurrencyPairLimitNotFoundException exception) {
        return new OpenApiError(ErrorCode.CURRENCY_PAIR_LIMIT_NOT_FOUND, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(AuthenticationNotAvailableException.class)
    @ResponseBody
    public OpenApiError authenticationNotAvailableExceptionHandler(HttpServletRequest req, AuthenticationNotAvailableException exception) {
        return new OpenApiError(ErrorCode.FAILED_AUTHENTICATION, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public OpenApiError missingServletRequestParameterHandler(HttpServletRequest req, MissingServletRequestParameterException exception) {
        return new OpenApiError(ErrorCode.MISSING_REQUIRED_PARAM, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class, OrderParamsWrongException.class, MethodArgumentTypeMismatchException.class})
    @ResponseBody
    public OpenApiError mismatchArgumentsErrorHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INVALID_PARAM_VALUE, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(OrderAcceptionException.class)
    @ResponseBody
    public OpenApiError orderAcceptionExceptionHandler(HttpServletRequest req, OrderAcceptionException exception) {
        return new OpenApiError(ErrorCode.ACCEPTING_ORDER_ERROR, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(OrderCreationException.class)
    @ResponseBody
    public OpenApiError orderCreationExceptionHandler(HttpServletRequest req, OrderCreationException exception) {
        return new OpenApiError(ErrorCode.CREATING_ORDER_ERROR, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(OrderCancellingException.class)
    @ResponseBody
    public OpenApiError orderCancellingExceptionHandler(HttpServletRequest req, OrderCancellingException exception) {
        return new OpenApiError(ErrorCode.CANCELING_ORDER_ERROR, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(WalletCreationException.class)
    @ResponseBody
    public OpenApiError walletCreationExceptionHandler(HttpServletRequest req, WalletCreationException exception) {
        return new OpenApiError(ErrorCode.CREATING_WALLET_ERROR, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(CallBackUrlAlreadyExistException.class)
    @ResponseBody
    public OpenApiError callBackExistException(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.CALL_BACK_URL_ALREADY_EXISTS, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public OpenApiError jsonMappingExceptionHandler(HttpServletRequest req, HttpMessageNotReadableException exception) {
        return new OpenApiError(ErrorCode.REQUEST_NOT_READABLE, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(value = AccessDeniedException.class)
    public OpenApiError accessDeniedExceptionHandler(HttpServletRequest req, AccessDeniedException exception) {
        return new OpenApiError(ErrorCode.ACCESS_DENIED, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(value = IncorrectCurrentUserException.class)
    public OpenApiError incorrectCurrentUserExceptionHandler(HttpServletRequest req, IncorrectCurrentUserException exception) {
        return new OpenApiError(ErrorCode.USER_MISMATCH, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(value = {NotEnoughUserWalletMoneyException.class, InsufficientCostsForAcceptionException.class})
    public OpenApiError notEnoughUserWalletMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INVALID_PAYMENT_AMOUNT, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(UserOperationAccessException.class)
    @ResponseBody
    public OpenApiError userOperationAccessExceptionHandler(HttpServletRequest req, UserOperationAccessException exception) {
        return new OpenApiError(ErrorCode.BLOCKED_TRADING, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(NotCreatableOrderException.class)
    @ResponseBody
    public OpenApiError notCreatableOrderExceptionHandler(HttpServletRequest req, NotCreatableOrderException exception) {
        return new OpenApiError(ErrorCode.ORDER_NOT_CREATABLE, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(WalletPersistException.class)
    @ResponseBody
    public OpenApiError walletPersistExceptionHandler(HttpServletRequest req, WalletPersistException exception) {
        return new OpenApiError(ErrorCode.WALLET_UPDATE_ERROR, req.getRequestURL(), exception.getMessage());
    }

    @ResponseStatus(NO_CONTENT)
    @ExceptionHandler(CancelOrderException.class)
    @ResponseBody
    public OpenApiError CancelOrderExceptionHandler(HttpServletRequest req, CancelOrderException exception) {
        return new OpenApiError(ErrorCode.NOT_CANCELLED, req.getRequestURL(), exception);
    }
}
