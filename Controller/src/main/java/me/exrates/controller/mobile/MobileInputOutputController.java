package me.exrates.controller.mobile;

import me.exrates.controller.exception.InputRequestLimitExceededException;
import me.exrates.controller.exception.InvalidNicknameException;
import me.exrates.controller.exception.InvoiceNotFoundException;
import me.exrates.controller.exception.NotEnoughMoneyException;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.dto.mobileApiDto.*;
import me.exrates.model.*;
import me.exrates.model.dto.mobileApiDto.*;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserActionOnInvoiceEnum;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.InvoiceData;
import me.exrates.model.vo.WithdrawData;
import me.exrates.service.*;
import me.exrates.service.WalletService;
import me.exrates.service.exception.*;
import me.exrates.service.exception.api.ApiError;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.exception.invoice.IllegalInvoiceRequestStatusException;
import me.exrates.service.merchantPayment.MerchantPaymentService;
import me.exrates.service.util.RestApiUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

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

    private static int INVOICE_MERCHANT_ID = 12;
    private static int INVOICE_MERCHANT_IMAGE_ID = 16;


    @Autowired
    private UserService userService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private Map<String, MerchantPaymentService> merchantPaymentServices;

    @Autowired
    private WalletService walletService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private UserFilesService userFilesService;

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
    @RequestMapping(value="/withdraw", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String,String>> withdraw(@RequestBody @Valid WithdrawDto withdrawDto) {


        Payment payment = new Payment();
        payment.setCurrency(withdrawDto.getCurrency());
        payment.setMerchant(withdrawDto.getMerchant());
        payment.setSum(withdrawDto.getSum());
        payment.setMerchantImage(withdrawDto.getMerchantImage());
        payment.setDestination(withdrawDto.getDestination());
        payment.setOperationType(OperationType.OUTPUT);


        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
            return merchantService.prepareCreditsOperation(payment, userEmail)
                    .map(creditsOperation -> merchantService.withdrawRequest(creditsOperation, new WithdrawData(), userEmail, userLocale))
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
    @RequestMapping(value = "/preparePayment", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<MerchantInputResponseDto> preparePayment(@RequestBody @Valid PaymentDto paymentDto) {
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        if (!merchantService.checkInputRequestsLimit(paymentDto.getMerchant(), userEmail)){
            throw new InputRequestLimitExceededException(messageSource.getMessage("merchants.InputRequestsLimit", null, userLocale));
        }

        Payment payment = new Payment();
        payment.setCurrency(paymentDto.getCurrency());
        payment.setMerchant(paymentDto.getMerchant());
        payment.setSum(paymentDto.getSum());
        payment.setMerchantImage(paymentDto.getMerchantImage());
        payment.setOperationType(OperationType.INPUT);
        String merchantName = merchantService.findById(payment.getMerchant()).getName();
        String beanName = String.join("", merchantName.split("[\\s.]+")).concat( "PaymentService");
        final MerchantInputResponseDto result = merchantPaymentServices.get(beanName).preparePayment(userEmail, payment,
                userLocale);
        return new ResponseEntity<>(result, OK);
    }

    /**
     * @api {post} /api/payments/invoice/prepare Prepare invoice
     * @apiName prepareInvoice
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Submit request input through Invoice
     * @apiParam {Integer} currency currency id
     * @apiParam {Number} amount amount of payment
     * @apiParam {Integer} bankId ID of destination bank
     * @apiParam {String} userFullName full name of user
     * @apiParam {String} remark additional remark (OPTIONAL)
     *
     * @apiParamExample {json} Request Example:
     *      {
     *          "currency": 2,
     *          "amount": 10.0,
     *          "bankId": 3,
     *          "userFullName": John Smith,
     *          "remark": qwerty qwerty
     *      }
     *
     * @apiSuccess {Integer} invoiceId  id of created invoice request
     * @apiSuccess {String} walletNumber Number of wallet
     * @apiSuccess {String} notification Notification with payment details
     *
     * @apiSuccessExample {json} Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *          "invoiceId": 130721,
     *          "walletNumber": "0483087786",
     *          "notification": "Заявка на ввод средств создана. Пожалуйста, оплатите 1 013 000 000 000 IDR на расчетный счет  и подтвердите заявку "
     *     }
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
    @RequestMapping(value = "/invoice/prepare", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<InvoiceResponseDto> prepareInvoice(@RequestBody @Valid InvoicePaymentDto paymentDto) {
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        Payment payment = new Payment();
        payment.setCurrency(paymentDto.getCurrencyId());
        payment.setMerchant(merchantService.findByNName("Invoice").getId());
        payment.setSum(paymentDto.getAmount().doubleValue());
        payment.setOperationType(OperationType.INPUT);
        final CreditsOperation creditsOperation = merchantService
                .prepareCreditsOperation(payment, userEmail)
                .orElseThrow(InvalidAmountException::new);
        InvoiceData invoiceData = new InvoiceData();
        invoiceData.setCreditsOperation(creditsOperation);
        invoiceData.setBankId(paymentDto.getBankId());
        invoiceData.setUserFullName(paymentDto.getUserFullName());
        invoiceData.setRemark(paymentDto.getRemark());
        final Transaction transaction = invoiceService.createPaymentInvoice(invoiceData);
        final String notification = merchantService
                .sendDepositNotification("",
                        userEmail , userLocale, creditsOperation, "merchants.depositNotificationWithCurrency" +
                                creditsOperation.getCurrency().getName() +
                                ".body");
        InvoiceResponseDto dto = new InvoiceResponseDto();
        dto.setNotification(notification);
        dto.setWalletNumber(invoiceService.findBankById(paymentDto.getBankId()).getAccountNumber());
        dto.setInvoiceId(transaction.getId());
        return new ResponseEntity<>(dto, OK);
    }


    /**
     * @api {post} /api/payments/invoice/confirm Confirm invoice
     * @apiName confirmInvoice
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Confirm invoice by ID
     * @apiParam {Integer} invoiceId invoice id
     * @apiParam {String} payerBankName name of payer bank
     * @apiParam {String} userAccount payer account number
     * @apiParam {String} userFullName full name of user
     * @apiParam {String} remark additional remark (OPTIONAL)
     *
     * @apiParamExample {json} Request Example:
     *      {
     *          "invoiceId": 130720,
     *          "payerBankName": "AAA BANK",
     *          "userAccount": "6541325465",
     *          "userFullName": "Talalai Talalaenko",
     *          "remark": "alala ololo"
     *      }
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
    @RequestMapping(value = "/invoice/confirm", method = POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> confirmInvoice(@Valid InvoiceConfirmData invoiceConfirmData) throws me.exrates.service.exception.invoice.InvoiceNotFoundException, IllegalInvoiceRequestStatusException {
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        invoiceService.userActionOnInvoice(invoiceConfirmData, UserActionOnInvoiceEnum.CONFIRM, userLocale);
        return new ResponseEntity<>(OK);
    }
    @RequestMapping(value = "/invoice/revoke", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> revokeInvoice(@RequestBody Map<String, String> params) throws me.exrates.service.exception.invoice.InvoiceNotFoundException, IllegalInvoiceRequestStatusException {
        String invoiceIdString = RestApiUtils.retrieveParamFormBody(params, "invoiceId", true);
        Integer invoiceId = Integer.parseInt(invoiceIdString);
        InvoiceConfirmData invoiceConfirmData = new InvoiceConfirmData();
        invoiceConfirmData.setInvoiceId(invoiceId);
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        invoiceService.userActionOnInvoice(invoiceConfirmData, UserActionOnInvoiceEnum.REVOKE, userLocale);
        return new ResponseEntity<>(OK);
    }


    /**
     * @api {get} /api/payments/invoice/banks Banks info
     * @apiName getBanksByCurrency
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription returns list of banks by currency
     * @apiParam {Integer} currencyId - currency id
     * @apiParamExample Request example
     * /api/payments/invoice/banks?currencyId=10
     * @apiSuccess {Array} banks List of banks
     * @apiSuccess {Object} data Container object
     * @apiSuccess {Integer} data.id bank id
     * @apiSuccess {Integer} data.currencyId currency id
     * @apiSuccess {String} data.name bank name
     * @apiSuccess {String} data.accountNumber bank account number
     * @apiSuccess {Number} data.recipient recipient's full name
     * @apiSuccessExample {json} Success-Response:
     *     HTTP/1.1 200 OK
     *   [
     *      {
     *          "id": 1,
     *          "currencyId": 10,
     *          "name": "BCA",
     *          "accountNumber": "3150963141",
     *          "recipient": "Nanda Rizal Pahlewi"
     *      },
     *      {
     *          "id": 2,
     *          "currencyId": 10,
     *          "name": "MANDIRI",
     *          "accountNumber": "1440099965557",
     *          "recipient": "Nanda Rizal Pahlewi"
     *       }
     *   ]
     *
     *
     *
     * @apiUse ExpiredAuthenticationTokenError
     * @apiUse MissingAuthenticationTokenError
     * @apiUse InvalidAuthenticationTokenError
     * @apiUse AuthenticationError
     * @apiUse InternalServerError
     */
    @RequestMapping(value = "/invoice/banks", method = GET)
    public List<InvoiceBank> getBanksByCurrency(@RequestParam Integer currencyId) {
        return invoiceService.findBanksForCurrency(currencyId);
    }

    @RequestMapping(value = "/invoice/clientBanks", method = GET)
    public List<ClientBank> getClientBanksByCurrency(@RequestParam Integer currencyId) {
        return invoiceService.findClientBanksForCurrency(currencyId);
    }

    @RequestMapping(value = "/invoice/requests", method = GET)
    public List<InvoiceRequest> findInvoiceRequestsForUser() {
        return invoiceService.findAllRequestsForUser(getAuthenticatedUserEmail());

    }

   /* @RequestMapping(value = "/invoice/details", method = GET)
    public InvoiceDetailsDto findInvoiceRequestDetails(@RequestParam Integer invoiceId) {
        Optional<InvoiceRequest> invoiceRequestResult = invoiceService.findRequestById(invoiceId);
        if (!invoiceRequestResult.isPresent()) {
            throw new InvoiceNotFoundException(String.format("Invoice with id %s not found", invoiceId));
        }  {
            InvoiceRequest invoiceRequest = invoiceRequestResult.get();
        }



    }*/

    @RequestMapping(value = "/invoice/withdraw", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, String>> withdrawInvoice(@RequestBody @Valid WithdrawInvoiceDto withdrawInvoiceDto) {
        LOGGER.debug(withdrawInvoiceDto);
        Payment payment = new Payment();
        payment.setSum(withdrawInvoiceDto.getSum());
        payment.setCurrency(withdrawInvoiceDto.getCurrency());
        payment.setMerchant(INVOICE_MERCHANT_ID);
        payment.setMerchantImage(INVOICE_MERCHANT_IMAGE_ID);
        payment.setOperationType(OperationType.INPUT);
        payment.setDestination(withdrawInvoiceDto.getWalletNumber());

        WithdrawData withdrawData = new WithdrawData();
        withdrawData.setRecipientBankName(withdrawInvoiceDto.getRecipientBankName());
        withdrawData.setRecipientBankCode(withdrawInvoiceDto.getRecipientBankCode());
        withdrawData.setUserFullName(withdrawInvoiceDto.getUserFullName());
        withdrawData.setRemark(withdrawInvoiceDto.getRemark());


        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        return merchantService.prepareCreditsOperation(payment, userEmail)
                .map(creditsOperation -> merchantService.withdrawRequest(creditsOperation, withdrawData, userEmail, userLocale))
                .map(response -> new ResponseEntity<>(response, OK))
                .orElseThrow(InvalidAmountException::new);
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

    @RequestMapping(value = "/preparePostPayment", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String,String>> preparePostPayment(@RequestBody @Valid PaymentDto paymentDto) {
        LOGGER.debug(paymentDto);
        Payment payment = new Payment();
        payment.setCurrency(paymentDto.getCurrency());
        payment.setMerchant(paymentDto.getMerchant());
        payment.setSum(paymentDto.getSum());
        payment.setMerchantImage(paymentDto.getMerchantImage());
        payment.setOperationType(OperationType.INPUT);
        String userEmail = getAuthenticatedUserEmail();
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

    private String getAuthenticatedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }




    /**
     * @api {post} /api/payments/transfer/submit Submit transfer
     * @apiName submitTransfer
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Send transfer to other user
     * @apiParam {Integer} walletId wallet id
     * @apiParam {Integer} nickname nickname of receiver
     * @apiParam {Number} amount amount of transfer
     *
     * @apiParamExample {json} Request Example:
     *      {
     *          "walletId": 6280,
     *          "nickname": "qwerty123",
     *          "sum": 10.0
     *      }
     *
     * @apiSuccessExample {json} Success-Response:
     *     HTTP/1.1 200 OK
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
    @RequestMapping(value = "/transfer/submit", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> submitTransfer(@RequestBody UserTransferDto userTransferDto) {
        Locale userLocale = userService.getUserLocaleForMobile(SecurityContextHolder.getContext().getAuthentication().getName());
        String principalNickname = userService.findByEmail(getAuthenticatedUserEmail()).getNickname();
        if (userTransferDto.getNickname().equals(principalNickname)) {
            throw new InvalidNicknameException(messageSource.getMessage("transfer.selfNickname", null, userLocale));
        }
        walletService.transferCostsToUser(userTransferDto.getWalletId(), userTransferDto.getNickname(),
                userTransferDto.getAmount(), userLocale);
        return new ResponseEntity<>(OK);

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

  @ResponseStatus(NOT_ACCEPTABLE)
  @ExceptionHandler({IllegalInvoiceRequestStatusException.class})
  public ApiError illegalInvoiceRequestStatusExceptionHandler(HttpServletRequest req, Exception exception) {
    return new ApiError(BAD_INVOICE_STATUS, req.getRequestURL(), exception);
  }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(CurrencyPairNotFoundException.class)
    @ResponseBody
    public ApiError currencyPairNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.CURRENCY_PAIR_NOT_FOUND, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(InvoiceNotFoundException.class)
    @ResponseBody
    public ApiError invoiceNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.INVOICE_NOT_FOUND, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public ApiError userNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.USER_NOT_FOUND, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidNicknameException.class)
    @ResponseBody
    public ApiError invalidNicknameExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.SELF_TRANSFER_NOT_ALLOWED, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(MerchantCurrencyBlockedException.class)
    @ResponseBody
    public ApiError merchantCurrencyBlockedExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.BLOCKED_CURRENCY_FOR_MERCHANT, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(InputRequestLimitExceededException.class)
    @ResponseBody
    public ApiError inputRequestLimitExceededExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.INPUT_REQUEST_LIMIT_EXCEEDED, req.getRequestURL(), exception);
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
