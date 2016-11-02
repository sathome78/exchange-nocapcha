package me.exrates.controller.mobile;

import me.exrates.controller.exception.NotEnoughMoneyException;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.MerchantInputResponseDto;
import me.exrates.model.dto.mobileApiDto.PaymentDto;
import me.exrates.model.dto.mobileApiDto.WithdrawDto;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.UserService;
import me.exrates.service.exception.CurrencyPairNotFoundException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.api.ApiError;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.merchantPayment.MerchantPaymentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.service.exception.api.ErrorCode.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by OLEG on 02.09.2016.
 */
@RestController
@RequestMapping("/api/payments")
public class MobileInputOutputController {

    private static final Logger LOGGER = LogManager.getLogger("mobileAPI");


    @Autowired
    private UserService userService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private Map<String, MerchantPaymentService> merchantPaymentServices;

    @Autowired
    private MessageSource messageSource;





    /**
     * @apiDefine InvalidAmountError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Invalid Payment Amount:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *           "errorCode": "INVALID_PAYMENT_AMOUNT",
     *           "url": "http://127.0.0.1:8080/api/payments/preparePayment",
     *           "cause": "InvalidAmountException",
     *           "detail": null
     *      }
     *
     * */

    /**
     * @apiDefine NotEnoughMoneyForWithdrawError
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Insufficient Costs:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *           "errorCode": "INSUFFICIENT_FUNDS",
     *           "url": "http://127.0.0.1:8080/api/payments/withdraw",
     *           "cause": "NotEnoughUserWalletMoneyException",
     *           "detail": "Not enough money to withdraw on user wallet Wallet{id=4277, currencyId=1, userId=golvazin@gmail.com,
     *           activeBalance=-10.000000000, reservedBalance=0E-9, name='RUB'}"
     *      }
     *
     * */




    /**
     * @api {get} /api/payments/merchantsAll Merchants info
     * @apiName findAllMerchants
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns list of all merchants (sf no currencyId) or by currency
     * @apiParam {Integer} currencyId - currency id (OPTIONAL)
     * @apiParamExample Request example
     * /api/payments/merchants?currencyId=7
     * @apiSuccess {Array} merchants List of available merchants
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.merchantId merchant id
     * @apiSuccess {Integer} data.currencyId currency id
     * @apiSuccess {String} data.name merchant name
     * @apiSuccess {String} data.detail merchant detail
     * @apiSuccess {Number} data.minSum minimal sum of payment
     * @apiSuccess {Array} data.merchantImageList List of merchant images
     * @apiSuccess {Object} merchantImage Merchant image
     * @apiSuccess {String} data.merchantImage.merchantId merchant id
     * @apiSuccess {String} data.merchantImage.currencyId currency id
     * @apiSuccess {String} data.merchantImage.image_name image name
     * @apiSuccess {String} data.merchantImage.image_path - path for image on server
     * @apiSuccess {String} data.merchantImage.id - merchant image id
     * @apiSuccessExample {json} Success-Response:
     *     HTTP/1.1 200 OK
     *   [
     *          {
     *              "merchantId": 12,
     *              "currencyId": 10,
     *              "name": "Invoice",
     *              "minInputSum": 0.01,
     *              "minOutputSum": 131340,
     *              "commission": 0,
     *              "listMerchantImage": [
     *                  {
     *                      "id": 63,
     *                      "imagePath": "/client/img/merchants/invoice.png"
     *                  }
     *              ]
     *          }
     *    ]
     *
     *
     *
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InternalServerError
     */
    @RequestMapping(value = "/merchants")
    public List<MerchantCurrencyApiDto> findAllMerchantCurrencies(@RequestParam(required = false) Integer currencyId) {
        return merchantService.findAllMerchantCurrencies(currencyId);
    }

    /**
     * @api {post} /api/payments/withdraw Withdraw
     * @apiName withdraw
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Submit request for costs withdrawal
     * @apiParam {Integer} currency currency id
     * @apiParam {Integer} merchant merchant id
     * @apiParam {Number} sum amount of payment
     * @apiParam {String} destination wallet to send money
     * @apiParam {Integer} merchantImage merchant image id (OPTIONAL)
     *
     * @apiParamExample {json} Request Example:
     *      {
     *          "currency": 2,
     *          "merchant": 10,
     *          "sum": 10.0,
     *          "destination": "11111111111",
     *          "merchantImage": 34
     *      }
     *
     * @apiSuccess {String} balance User's current balance for respective currency
     * @apiSuccess {String} success success notification
     * @apiSuccessExample {json} Success-Response:
     *     HTTP/1.1 200 OK
     *   {
     *      "balance": "USD 17801.04",
     *      "success": "Your withdrawal request #126,556 through system Interkassa has been accepted and it will be processed within 48 hours."
     *   }
     *
     *
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse NotEnoughMoneyForWithdrawError
     * @apiUse MessageNotReadableError
     * @apiUse InvalidAmountError
     * @apiUse InvalidParamError
     * @apiUse InternalServerError
     */
    @RequestMapping(value="/withdraw", method = POST)
    public ResponseEntity<Map<String,String>> withdraw(@RequestBody @Valid WithdrawDto withdrawDto) {


        Payment payment = new Payment();
        payment.setCurrency(withdrawDto.getCurrency());
        payment.setMerchant(withdrawDto.getMerchant());
        payment.setSum(withdrawDto.getSum());
        payment.setMerchantImage(withdrawDto.getMerchantImage());
        payment.setDestination(withdrawDto.getDestination());
        payment.setOperationType(OperationType.OUTPUT);


        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
            return merchantService.prepareCreditsOperation(payment, userEmail)
                    .map(creditsOperation -> merchantService.withdrawRequest(creditsOperation, userLocale, userEmail))
                    .map(response -> new ResponseEntity<>(response, OK))
                    .orElseThrow(InvalidAmountException::new);

    }

    /**
     * @api {post} /api/payments/preparePayment Input payment
     * @apiName preparePayment
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Submit request for costs input. Response depends on concrete merchant / payment system
     * (see <a href="https://drive.google.com/open?id=0Bx4pleRSZBP0YTdzT3h2RmZYU3c">link</a>)
     * @apiParam {Integer} currency currency id
     * @apiParam {Integer} merchant merchant id
     * @apiParam {Number} sum amount of payment
     * @apiParam {Integer} merchantImage merchant image id (OPTIONAL)
     *
     * @apiParamExample {json} Request Example:
     *      {
     *          "currency": 2,
     *          "merchant": 10,
     *          "sum": 10.0
     *      }
     *
     * @apiSuccess {String} notification Notification with payment details (for cryptocurrencies and invoice)
     * @apiSuccess {String} url URL to redirect or send POST request
     * @apiSuccess {Object} properties Request params
     *
     *
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InvalidParamError
     * @apiUse MessageNotReadableError
     * @apiUse InvalidAmountError
     * @apiUse InternalServerError
     */
    @RequestMapping(value = "/preparePayment", method = POST)
    public ResponseEntity<MerchantInputResponseDto> preparePayment(@RequestBody @Valid PaymentDto paymentDto) {
        Payment payment = new Payment();
        payment.setCurrency(paymentDto.getCurrency());
        payment.setMerchant(paymentDto.getMerchant());
        payment.setSum(paymentDto.getSum());
        payment.setMerchantImage(paymentDto.getMerchantImage());
        payment.setOperationType(OperationType.INPUT);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String merchantName = merchantService.findById(payment.getMerchant()).getName();
        String beanName = String.join("", merchantName.split("[\\s.]+")).concat( "PaymentService");
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        final MerchantInputResponseDto result = merchantPaymentServices.get(beanName).preparePayment(userEmail, payment,
                userLocale);
        return new ResponseEntity<>(result, OK);
    }

    @RequestMapping(value = "/merchantRedirect", method = GET)
    public ModelAndView getMerchantRedirectPage(@RequestParam Integer currencyId, @RequestParam Integer merchantId,
                                                @RequestParam BigDecimal amount, @RequestParam String token) {
        ModelAndView modelAndView = new ModelAndView("merchantApiInput");
        modelAndView.addObject("currency", currencyId);
        modelAndView.addObject("merchant", merchantId);
        modelAndView.addObject("amount", amount);
        modelAndView.addObject("authToken", token);
        return modelAndView;
    }

    @RequestMapping(value = "/preparePostPayment", method = POST)
    public ResponseEntity<Map<String,String>> preparePostPayment(@RequestBody @Valid PaymentDto paymentDto) {
        LOGGER.debug(paymentDto);
        Payment payment = new Payment();
        payment.setCurrency(paymentDto.getCurrency());
        payment.setMerchant(paymentDto.getMerchant());
        payment.setSum(paymentDto.getSum());
        payment.setMerchantImage(paymentDto.getMerchantImage());
        payment.setOperationType(OperationType.INPUT);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        final CreditsOperation creditsOperation = merchantService
                .prepareCreditsOperation(payment, userEmail)
                .orElseThrow(InvalidAmountException::new);
        String merchantName = merchantService.findById(payment.getMerchant()).getName();
        String beanName = String.join("", merchantName.split("[\\s.]+")).concat( "PaymentService");
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        final Map<String,String> result = merchantPaymentServices.get(beanName).preparePostPayment(userEmail, creditsOperation,
                userLocale);
        LOGGER.debug(result);
        return new ResponseEntity<>(result, OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ApiError httpMessageNotReadableExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(REQUEST_NOT_READABLE, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public ApiError missingServletRequestParameterHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.MISSING_REQUIRED_PARAM, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class})
    @ResponseBody
    public ApiError mismatchArgumentsErrorHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.INVALID_PARAM_VALUE, req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidAmountException.class)
    public ApiError invalidAmountExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(INVALID_PAYMENT_AMOUNT, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler({NotEnoughMoneyException.class, NotEnoughUserWalletMoneyException.class})
    public ApiError notEnoughMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(INSUFFICIENT_FUNDS, req.getRequestURL(), exception);
    }


    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(CurrencyPairNotFoundException.class)
    @ResponseBody
    public ApiError currencyPairNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.CURRENCY_PAIR_NOT_FOUND, req.getRequestURL(), exception);
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public Map<String, Object> NullPointerHandler(HttpServletRequest req, Exception exception) {
        Map<String, Object> result = new HashMap<>();
        result.put("stacktrace", Arrays.asList(exception.getStackTrace()));
        return result;
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiError OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.INTERNAL_SERVER_ERROR, req.getRequestURL(), exception);
    }


}
