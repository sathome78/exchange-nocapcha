package me.exrates.controller.mobile;

import me.exrates.controller.exception.*;
import me.exrates.model.*;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestParamsDto;
import me.exrates.model.dto.WithdrawRequestCreateDto;
import me.exrates.model.dto.WithdrawRequestParamsDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.MerchantInputResponseDto;
import me.exrates.model.dto.mobileApiDto.UserTransferDto;
import me.exrates.model.enums.MerchantApiResponseType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.WithdrawData;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import me.exrates.service.exception.api.ApiError;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.exception.invoice.IllegalInvoiceStatusException;
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
import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_USER;
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
    private WalletService walletService;

    @Autowired
    private InputOutputService inputOutputService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    WithdrawService withdrawService;

    @Autowired
    RefillService refillService;
    
    
    
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
     * @apiDefine IllegalInvoiceStatusException
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Illegal Invoice Status:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *           "errorCode": "BAD_INVOICE_STATUS",
     *           "url": "http://localhost:8080/api/payments/invoice/confirm",
     *           "cause": "UnsupportedInvoiceStatusForActionException",
     *           "detail": "current state: EXPIRED action: CONFIRM_USER"
     *      }
     *
     * */
    
    /**
     * @apiDefine InvoiceNotFoundException
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Invoice Not Found:
     * HTTP/1.1 404 Not Found
     *      {
     *           "errorCode": "INVOICE_NOT_FOUND",
     *           "url": "http://localhost:8080/api/payments/invoice/confirm",
     *           "cause": "InvoiceNotFoundException",
     *           "detail": "invoice id: 23423"
     *      }
     *
     * */
    
    
    
    /**
     * @apiDefine MerchantCurrencyBlockedException
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Merchant and Currency Blocked:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *           "errorCode": "BLOCKED_CURRENCY_FOR_MERCHANT",
     *           "url": "http://127.0.0.1:8080/api/payments/preparePayment",
     *           "cause": "MerchantCurrencyBlockedException",
     *           "detail": "Operation INPUT is blocked for this currency! "
     *      }
     *
     * */
    
    /**
     * @apiDefine InputRequestLimitExceededException
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Insufficient Costs:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *           "errorCode": "INPUT_REQUEST_LIMIT_EXCEEDED",
     *           "url": "http://127.0.0.1:8080/api/payments/preparePayment",
     *           "cause": "InputRequestLimitExceededException",
     *           "detail": "Ваш дневной лимит ввода для этой валюты превышен. Пожалуйста, попробуйте завтра."
     *      }
     *
     * */
    
    /**
     * @apiDefine OutputRequestLimitExceededException
     * @apiError (406) {String} errorCode error code
     * @apiError (406) {String} url request URL
     * @apiError (406) {String} cause name of root exception
     * @apiError (406) {String} details detail of root exception
     * @apiErrorExample {json} Insufficient Costs:
     * HTTP/1.1 406 Not Acceptable
     *      {
     *           "errorCode": "OUTPUT_REQUEST_LIMIT_EXCEEDED",
     *           "url": "http://127.0.0.1:8080/api/payments/withdraw",
     *           "cause": "RequestLimitExceededException",
     *           "detail": "Ваш дневной лимит вывода для этой валюты превышен. Пожалуйста, попробуйте завтра."
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
    public ResponseEntity<Map<String,String>> withdraw(@RequestBody @Valid WithdrawRequestParamsDto requestParamsDto) {


        Payment payment = new Payment();
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(requestParamsDto.getMerchant());
        payment.setSum(requestParamsDto.getSum().doubleValue());
        payment.setDestination(requestParamsDto.getDestination());
        payment.setOperationType(OperationType.OUTPUT);

        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, userEmail).orElseThrow(InvalidAmountException::new);
        WithdrawStatusEnum beginStatus = (WithdrawStatusEnum) WithdrawStatusEnum.getBeginState();
        WithdrawRequestCreateDto withdrawRequestCreateDto = new WithdrawRequestCreateDto(requestParamsDto, creditsOperation, beginStatus);
        Map<String, String> response = withdrawService.createWithdrawalRequest(withdrawRequestCreateDto, userLocale);
        return new ResponseEntity<>(response, OK);

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
     * @apiParam {Integer} recipientBankId ID of destination bank - for invoice
     * @apiParam {String} userFullName full name of user - for invoice
     * @apiParam {String} remark additional remark - for invoice (OPTIONAL)
     *
     *
     * @apiParamExample {json} Request Example:
     *      {
     *          "currency": 2,
     *          "merchant": 10,
     *          "sum": 10.0,
     *          "recipientBankId": 3,
     *          "userFullName": John Smith,
     *          "remark": qwerty qwerty
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
    public MerchantInputResponseDto preparePayment(@RequestBody @Valid RefillRequestParamsDto requestParamsDto, HttpServletRequest request) {
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        if (!refillService.checkInputRequestsLimit(requestParamsDto.getCurrency(), userEmail)){
            throw new InputRequestLimitExceededException(messageSource.getMessage("merchants.InputRequestsLimit", null, userLocale));
        }
        Merchant merchant = merchantService.findById(requestParamsDto.getMerchant());
        MerchantInputResponseDto responseDto = new MerchantInputResponseDto();
        if ("CRYPTO".equals(merchant.getProcessType()) || "INVOICE".equals(merchant.getProcessType())) {
            responseDto.setType(MerchantApiResponseType.NOTIFY);
            if (requestParamsDto.getRecipientBankId() != null && requestParamsDto.getAddress() == null) {
                InvoiceBank bank = refillService.findInvoiceBankById(requestParamsDto.getRecipientBankId()).orElseThrow(InvoiceBankNotFoundException::new);
                requestParamsDto.setAddress(bank.getAccountNumber());
                requestParamsDto.setRecipientBankName(bank.getName());
            }
            RefillStatusEnum beginStatus = (RefillStatusEnum) RefillStatusEnum.X_STATE.nextState(CREATE_BY_USER);
            Payment payment = new Payment(INPUT);
            payment.setCurrency(requestParamsDto.getCurrency());
            payment.setMerchant(requestParamsDto.getMerchant());
            payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());
            CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, userEmail)
                    .orElseThrow(InvalidAmountException::new);
            RefillRequestCreateDto refillRequest = new RefillRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, userLocale);
            Map<String, Object> result = refillService.createRefillRequest(refillRequest);
            Map<String, String> params = (Map<String, String>)result.get("params");
            String message = (String) result.get("message");
            if (message != null) {
                message = message.replaceAll("<button.*>", "").replaceAll("<.*?>", "");
            }
            responseDto.setData(message);
            responseDto.setQr(params.get("qr"));
            responseDto.setWalletNumber("CRYPTO".equals(merchant.getProcessType()) ? params.get("address") : params.get("walletNumber"));
        } else {
            responseDto.setType(MerchantApiResponseType.REDIRECT);
            String rootUrl = String.join("", request.getScheme(), "://", request.getServerName(), ":",
                    String.valueOf(request.getServerPort()), "/api/payments/merchantRedirect?" );
            String params = new HashMap<String, Object>() {{
                put("currencyId", requestParamsDto.getCurrency());
                put("merchantId", requestParamsDto.getMerchant());
                put("amount", requestParamsDto.getSum());
            }}.entrySet().stream().map((entry -> entry.getKey() + "=" + entry.getValue())).collect(Collectors.joining("&"));
            responseDto.setData(rootUrl + params);
        }
        
        return responseDto;
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
    public ResponseEntity<Void> confirmInvoice(@Valid InvoiceConfirmData invoiceConfirmData) throws Exception {
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        refillService.confirmRefillRequest(invoiceConfirmData, userLocale);
        return new ResponseEntity<>(OK);
    }
    
    /**
     * @api {post} /api/payments/invoice/revoke Revoke invoice
     * @apiName revokeInvoice
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Revoke invoice by ID
     * @apiParam {Integer} invoiceId invoice id
     *
     * @apiParamExample {json} Request Example:
     *      {
     *          "invoiceId": 130720
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
     * @apiUse IllegalInvoiceStatusException
     * @apiUse InvoiceNotFoundException
     * @apiUse InternalServerError
     */
    @RequestMapping(value = "/invoice/revoke", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> revokeInvoice(@RequestBody Map<String, String> params) throws Exception {
        String invoiceIdString = RestApiUtils.retrieveParamFormBody(params, "invoiceId", true);
        Integer invoiceId = Integer.parseInt(invoiceIdString);
        InvoiceConfirmData invoiceConfirmData = new InvoiceConfirmData();
        invoiceConfirmData.setInvoiceId(invoiceId);
        refillService.revokeRefillRequest(invoiceId);
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
        return refillService.findBanksForCurrency(currencyId);
    }

    @RequestMapping(value = "/invoice/clientBanks", method = GET)
    public List<ClientBank> getClientBanksByCurrency(@RequestParam Integer currencyId) {
        return withdrawService.findClientBanksForCurrency(currencyId);
    }


    /*
    //TODO REFILL
    @RequestMapping(value = "/invoice/details", method = GET)
    public InvoiceDetailsDto findInvoiceRequestDetails(@RequestParam Integer invoiceId, HttpServletRequest request) {
        Optional<InvoiceRequest> invoiceRequestResult = invoiceService.findRequestById(invoiceId);
        if (!invoiceRequestResult.isPresent()) {
            throw new InvoiceNotFoundException(String.format("Invoice with id %s not found", invoiceId));
        }
        InvoiceRequest invoiceRequest = invoiceRequestResult.get();
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/rest";
        return new InvoiceDetailsDto(invoiceRequest, baseUrl);
    }
*/
    @RequestMapping(value = "/invoice/withdraw", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map<String, String>> withdrawInvoice(@RequestBody @Valid WithdrawRequestParamsDto requestParamsDto) {
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        if (!withdrawService.checkOutputRequestsLimit(requestParamsDto.getCurrency(), userEmail)) {
            throw new RequestLimitExceededException(messageSource.getMessage("merchants.OutputRequestsLimit", null, userLocale));
        }
        Payment payment = new Payment();
        payment.setSum(requestParamsDto.getSum().doubleValue());
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(merchantService.findByName("Invoice").getId());
        payment.setOperationType(OperationType.OUTPUT);
        payment.setDestination(requestParamsDto.getWalletNumber());

        WithdrawData withdrawData = new WithdrawData();
        withdrawData.setRecipientBankName(requestParamsDto.getRecipientBankName());
        withdrawData.setRecipientBankCode(requestParamsDto.getRecipientBankCode());
        withdrawData.setUserFullName(requestParamsDto.getUserFullName());
        withdrawData.setRemark(requestParamsDto.getRemark());



        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, userEmail).orElseThrow(InvalidAmountException::new);
        WithdrawStatusEnum beginStatus = (WithdrawStatusEnum) WithdrawStatusEnum.getBeginState();
        WithdrawRequestCreateDto withdrawRequestCreateDto = new WithdrawRequestCreateDto(requestParamsDto, creditsOperation, beginStatus);
        Map<String, String> response = withdrawService.createWithdrawalRequest(withdrawRequestCreateDto, userLocale);
        
        return new ResponseEntity<>(response, OK);
    }
    
    /**
     * @api {post} /api/payments/withdraw/revoke Revoke withdraw request
     * @apiName revokeWithdrawRequest
     * @apiGroup Input-Output
     * @apiUse TokenHeader
     * @apiPermission user
     * @apiDescription Revoke withdraw request by ID
     * @apiParam {Integer} invoiceId invoice id
     *
     * @apiParamExample {json} Request Example:
     *      {
     *          "invoiceId": 130720
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
     * @apiUse IllegalInvoiceStatusException
     * @apiUse InvoiceNotFoundException
     * @apiUse InternalServerError
     */
    @RequestMapping(value = "/withdraw/revoke", method = POST)
    @ResponseBody
    public ResponseEntity<Void> revokeWithdrawRequest(@RequestBody Map<String, String> params) {
        Integer id = Integer.parseInt(RestApiUtils.retrieveParamFormBody(params, "invoiceId", true));
        withdrawService.revokeWithdrawalRequest(id);
        return new ResponseEntity<>(OK);
    }


    @RequestMapping(value = "/merchantRedirect", method = GET)
    public ModelAndView getMerchantRedirectPage(@RequestParam Integer currencyId, @RequestParam Integer merchantId,
                                                @RequestParam BigDecimal amount, @RequestParam String token) {
        ModelAndView modelAndView = new ModelAndView("merchantApiInput");
        modelAndView.addObject("currency", currencyId);
        modelAndView.addObject("merchant", merchantId);
        modelAndView.addObject("amount", amount);
        modelAndView.addObject("authToken", token);
        modelAndView.addObject("operationType", OperationType.INPUT);
        return modelAndView;
    }

    @RequestMapping(value = "/preparePostPayment", method = POST)
    public Map<String,Object> preparePostPayment(@Valid RefillRequestParamsDto requestParamsDto) {
        LOGGER.debug(requestParamsDto);
        Payment payment = new Payment(INPUT);
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(requestParamsDto.getMerchant());
        payment.setSum(requestParamsDto.getSum() == null ? 0 :requestParamsDto.getSum().doubleValue());
        String userEmail = getAuthenticatedUserEmail();
        Locale userLocale = userService.getUserLocaleForMobile(userEmail);
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, userEmail)
                .orElseThrow(InvalidAmountException::new);
        RefillStatusEnum beginStatus = (RefillStatusEnum) RefillStatusEnum.X_STATE.nextState(CREATE_BY_USER);
        RefillRequestCreateDto refillRequest = new RefillRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, userLocale);
        final Map<String,Object> result = refillService.createRefillRequest(refillRequest);
        LOGGER.debug(result);
        return result;
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
                userTransferDto.getAmount(), userLocale, false);
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
  @ExceptionHandler({IllegalInvoiceStatusException.class})
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
    @ExceptionHandler({InvoiceNotFoundException.class, me.exrates.service.exception.invoice.InvoiceNotFoundException.class})
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

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(RequestLimitExceededException.class)
    @ResponseBody
    public ApiError outputRequestLimitExceededExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.OUTPUT_REQUEST_LIMIT_EXCEEDED, req.getRequestURL(), exception);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiError OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ApiError(ErrorCode.INTERNAL_SERVER_ERROR, req.getRequestURL(), exception);
    }


}
