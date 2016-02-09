package me.exrates.controller.merchants;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.yandex.money.api.exceptions.InsufficientScopeException;
import com.yandex.money.api.exceptions.InvalidRequestException;
import com.yandex.money.api.exceptions.InvalidTokenException;
import com.yandex.money.api.methods.BaseRequestPayment;
import com.yandex.money.api.methods.ProcessPayment;
import com.yandex.money.api.methods.RequestPayment;
import com.yandex.money.api.methods.Token;
import com.yandex.money.api.methods.params.P2pTransferParams;
import com.yandex.money.api.model.Scope;
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Authorization;
import com.yandex.money.api.net.OAuth2Session;
import com.yandex.money.api.utils.HttpHeaders;
import com.yandex.money.api.utils.Strings;
import me.exrates.YandexMoneyProperties;
import me.exrates.dao.WalletDao;
import me.exrates.model.Commission;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.model.Wallet;
import me.exrates.service.CommissionService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/yandexmoney")
public class YandexMoneyMerchantController {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private YandexMoneyProperties yandexMoneyProperties;

    private static final Logger logger = LogManager.getLogger(YandexMoneyMerchantController.class);

    private static final String merchantErrorPage = "redirect:/merchants/yandexmoney/error";

    @RequestMapping("/test")
    public @ResponseBody
    List<Wallet> get() {
        Wallet wallet = new Wallet();
        wallet.setCurrId(1);
        wallet.setName("USD" );
        wallet.setUserId(1);
        final int newWallet = walletService.createNewWallet(wallet);
        walletService.setWalletABalance(newWallet,123);
        walletService.setWalletRBalance(newWallet,321);
        return walletService.getAllWallets(newWallet);
    }

    @RequestMapping(value = "/token/authorization",method =  RequestMethod.POST)
    public ModelAndView yandexMoneyTemporaryAuthorizationCodeRequest(ModelAndView modelAndView) {
        DefaultApiClient apiClient = new DefaultApiClient(yandexMoneyProperties.clientId(),true);
        OAuth2Session session = new OAuth2Session(apiClient);
        OAuth2Authorization oAuth2Authorization = session.createOAuth2Authorization();
        com.squareup.okhttp.OkHttpClient httpClient = apiClient.getHttpClient();
        session.setDebugLogging(true);
        byte[] params = oAuth2Authorization.getAuthorizeParams()
                .addScope(Scope.PAYMENT_P2P)
                .setRedirectUri(yandexMoneyProperties.redirectURI())
                .setResponseType(yandexMoneyProperties.responseType())
                .build();
        Request request = new Request.Builder()
                .url(oAuth2Authorization.getAuthorizeUrl())
                .post(RequestBody.create(yandexMoneyProperties.mediaType(),params))
                .build();
        Response response;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            return new ModelAndView(merchantErrorPage,new ModelMap().addAttribute("error","merchants.internalError"));
        }
        modelAndView.setViewName("redirect:"+response.header(HttpHeaders.LOCATION));
        return modelAndView;
    }

    @RequestMapping(value = "/token/access")
    public ModelAndView yandexMoneyAccessTokenRequest(@RequestParam(value = "code",required = false) String code, Principal principal) {
        ModelAndView errorModelAndView = new ModelAndView(merchantErrorPage, new ModelMap().addAttribute("error", "merchants.internalError"));
        if (Strings.isNullOrEmpty(code)) {
            return errorModelAndView;
        }
        final Token.Request request = new Token.Request(code, yandexMoneyProperties.clientId(), yandexMoneyProperties.redirectURI());
        OAuth2Session session = new OAuth2Session(new DefaultApiClient(yandexMoneyProperties.clientId()));
        Token token;
        try {
            token = session.execute(request);
        } catch (IOException e) {
            return errorModelAndView;
        } catch (InvalidTokenException | InvalidRequestException | InsufficientScopeException e) {
            return errorModelAndView;
        }
        return new ModelAndView("redirect:/merchants/yandexmoney/payment/process").addObject("token",token.accessToken);
    }

    @RequestMapping(value = "/payment/prepare",method = RequestMethod.POST)
    public ModelAndView preparePayment(@ModelAttribute("payment") Payment payment, BindingResult result, Principal principal, HttpSession httpSession) {
        if (result.hasErrors()) {
            System.out.println(result.getModel().toString());
            return new ModelAndView("redirect:/merchants/input",result.getModel());
        }
        final int userId = userService.getIdByEmail(principal.getName());
        final BigDecimal exratesCommission = BigDecimal.valueOf(commissionService.getCommissionByType(Commission.OperationType.INPUT.type));
        final BigDecimal paymentSum= BigDecimal.valueOf(payment.getSum()).setScale(9,BigDecimal.ROUND_CEILING);
        final BigDecimal commission = paymentSum.multiply(exratesCommission).setScale(9,BigDecimal.ROUND_CEILING);
        final BigDecimal sumToPay = paymentSum.add(commission).setScale(9,BigDecimal.ROUND_CEILING);
        ModelMap modelMap = new ModelMap()
                .addAttribute("userId",userId)
                .addAttribute("currency",payment.getCurrency())
                .addAttribute("amount",paymentSum.setScale(2,BigDecimal.ROUND_CEILING))
                .addAttribute("commission",commission.setScale(2,BigDecimal.ROUND_CEILING))
                .addAttribute("sumToPay",sumToPay.setScale(2,BigDecimal.ROUND_CEILING));
        synchronized (httpSession) {
            httpSession.setAttribute("paymentPrepareData",modelMap);
        }
        return new ModelAndView("assertpayment").addAllObjects(modelMap);
    }

    @RequestMapping(value = "/payment/process")
    public ModelAndView processPayment(Principal principal,@ModelAttribute(value = "token") String token,HttpSession httpSession) throws InvalidTokenException, InsufficientScopeException, InvalidRequestException, IOException {
        String email = principal.getName();
        ModelMap paymentData;
        synchronized (httpSession) {
            paymentData = (ModelMap) httpSession.getAttribute("paymentPrepareData");
            httpSession.removeAttribute("paymentPrepareData");
        }
        if (paymentData==null) {
            return new ModelAndView("redirect:/merchants/input");
        }
        final int userId = userService.getIdByEmail(email);
        final int currencyId = (int) paymentData.get("currency");
        final int walletId = walletService.getWalletId(userId, currencyId);
//        Wallet wallet = new Wallet();
//        if (walletId==0) {
//            walletService.create()
//        } else {
//            wallet  = walletService.get
//        }
        OAuth2Session oAuth2Session = new OAuth2Session(new DefaultApiClient(yandexMoneyProperties.clientId(),true));
        oAuth2Session.setAccessToken(token);
        String payInfo = "Purchase "+ paymentData.get("amount") + paymentData.get("currency")
                + " from " + principal.getName() + ". Total transferred amount: " + paymentData.get("sumToPay")
                + ", Commission: "+paymentData.get("commission") + ", Amount to be credited to user wallet: " + paymentData.get("amount");
        logger.info(payInfo);
        P2pTransferParams p2pTransferParams = new P2pTransferParams.Builder(yandexMoneyProperties.companyYandexMoneyWalletId())
                .setAmount((BigDecimal) paymentData.get("sumToPay"))
                .setComment("Purchase "+ paymentData.get("amount")+ paymentData.get("currency")+" at the Exrates")
                .create();
        System.out.println(p2pTransferParams.toString());
        RequestPayment.Request request = RequestPayment.Request.newInstance(p2pTransferParams);
        ModelAndView redirectToMerchantError = new ModelAndView(merchantErrorPage);
        RequestPayment execute;
        try {
            execute = oAuth2Session.execute(request);
            System.out.println(execute.toString());
            BaseRequestPayment.Status responseStatus = execute.status;
            if (responseStatus.equals(BaseRequestPayment.Status.REFUSED)) {
                switch (execute.error) {
                    case NOT_ENOUGH_FUNDS:
                        return redirectToMerchantError.addObject("error","merchants.notEnoughMoney");
                    case AUTHORIZATION_REJECT:
                        return redirectToMerchantError.addObject("error","merchants.authRejected");
                    case LIMIT_EXCEEDED:
                        return redirectToMerchantError.addObject("error","merchants.limitExceed");
                    case ACCOUNT_BLOCKED:
                        return new ModelAndView("redirect:"+execute.accountUnblockUri);
                    case EXT_ACTION_REQUIRED:
                        return new ModelAndView("redirect:"+execute.extActionUri);
                    default:
                        logger.fatal(execute.error);
                        return redirectToMerchantError.addObject("error","merchants.internalError");
                }
            }
        } catch (IOException | InvalidRequestException | InsufficientScopeException | InvalidTokenException e) {
            logger.error(e.getMessage());
            return redirectToMerchantError.addObject("error","merchants.internalError");
        }
        ProcessPayment processPayment = oAuth2Session.execute(new ProcessPayment.Request(execute.requestId));
        if (processPayment.status.equals(ProcessPayment.Status.SUCCESS)) {
//            final int idByEmail = userService.getIdByEmail(principal.getName());
//            final int walletId = walletService.getWalletId(idByEmail, (Integer) paymentData.get("currency"));
            walletService.setWalletABalance(walletId,((BigDecimal)paymentData.get("amount")).doubleValue());
            Transaction transaction = new Transaction();
            transaction.setAmount(((BigDecimal)paymentData.get("amount")).doubleValue());
            transaction.setCommissionId(Commission.OperationType.INPUT.type);
            transaction.setTransactionType(Payment.TransactionType.INPUT);
            transaction.setWalletId(walletId);
            transaction.setDate(LocalDateTime.now());
            transactionService.create(transaction);
            logger.info(transaction.toString());
            final Token.Revoke revoke = new Token.Revoke();
            final Token.Revoke execute1 = oAuth2Session.execute(revoke);
            System.out.println(execute1);
            return new ModelAndView("redirect:/mywallets");
        } else if (processPayment.status.equals(ProcessPayment.Status.REFUSED)){
            switch (processPayment.error) {
                case NOT_ENOUGH_FUNDS:
                    return redirectToMerchantError.addObject("error","merchants.notEnoughMoney");
                case ACCOUNT_BLOCKED:
                    return new ModelAndView("redirect:"+execute.accountUnblockUri);
            }
        }
        return redirectToMerchantError.addObject("error","merchants.internalError");
    }
}