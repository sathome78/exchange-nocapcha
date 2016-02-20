package me.exrates.controller.merchants;

import com.yandex.money.api.exceptions.InsufficientScopeException;
import com.yandex.money.api.exceptions.InvalidRequestException;
import com.yandex.money.api.exceptions.InvalidTokenException;
import com.yandex.money.api.methods.BaseRequestPayment;
import com.yandex.money.api.methods.ProcessPayment;
import com.yandex.money.api.methods.RequestPayment;
import com.yandex.money.api.methods.params.P2pTransferParams;
import com.yandex.money.api.net.DefaultApiClient;
import com.yandex.money.api.net.OAuth2Session;
import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
import me.exrates.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/yandexmoney")
public class YandexMoneyMerchantController {

    @Autowired
    private YandexMoneyService yandexMoneyService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private CompanyWalletService companyWalletService;

    @Autowired
    private CompanyTransactionService companyTransactionService;

    @Autowired
    private TransactionService transactionService;

    private static final Logger logger = LogManager.getLogger(YandexMoneyMerchantController.class);

    private static final String merchantInputErrorPage = "redirect:/merchants/input";

    private static final String merchantOutputErrorPage = "redirect:/merchants/output";

    @RequestMapping(value = "/token/authorization", method = RequestMethod.GET)
    public String yandexMoneyTemporaryAuthorizationCodeRequest() {
        final String temporaryAuthCode = yandexMoneyService.getTemporaryAuthCode();
        return "redirect:" + temporaryAuthCode;
    }

    @RequestMapping(value = "/token/access")
    public ModelAndView yandexMoneyAccessTokenRequest(@RequestParam(value = "code") String code,RedirectAttributes redirectAttributes) {
        ModelAndView errorModelAndView = new ModelAndView(merchantInputErrorPage);
        redirectAttributes.addFlashAttribute("error", "merchants.authRejected");
        final Optional<String> accessToken = yandexMoneyService.getAccessToken(code);
        accessToken.orElseThrow(InvalidToke);
        return new ModelAndView("redirect:/merchants/yandexmoney/payment/process").addObject("token", token.accessToken);
    }

    @RequestMapping(value = "/payment/prepare", method = RequestMethod.POST)
    public ModelAndView preparePayment(@Valid @ModelAttribute("payment") Payment payment, BindingResult result, Principal principal, HttpSession httpSession) {
        if (result.hasErrors()) {
            return new ModelAndView("redirect:/merchants/input", result.getModel());
        }
        final int userId = userService.getIdByEmail(principal.getName());
        final BigDecimal exratesCommission = BigDecimal.valueOf(commissionService.getCommissionByType(OperationType.INPUT));
        final BigDecimal paymentSum = BigDecimal.valueOf(payment.getSum()).setScale(9, BigDecimal.ROUND_CEILING);
        final BigDecimal commission = paymentSum.multiply(exratesCommission).divide(BigDecimal.valueOf(100),BigDecimal.ROUND_CEILING).setScale(9, BigDecimal.ROUND_CEILING);
        final BigDecimal sumToPay = paymentSum.add(commission).setScale(9, BigDecimal.ROUND_CEILING);
        ModelMap modelMap = new ModelMap()
                .addAttribute("userId", userId)
                .addAttribute("currency", payment.getCurrency())
                .addAttribute("amount", paymentSum.setScale(2, BigDecimal.ROUND_CEILING))
                .addAttribute("commission", commission.setScale(2, BigDecimal.ROUND_CEILING))
                .addAttribute("sumToPay", sumToPay.setScale(2, BigDecimal.ROUND_CEILING))
                .addAttribute("merchant", payment.getMerchant());
        synchronized (httpSession) {
            httpSession.setAttribute("paymentPrepareData", modelMap);
        }
        return new ModelAndView("redirect:/merchants/yandexmoney/token/authorization");
    }

    @RequestMapping(value = "/payment/process")
    public ModelAndView processPayment(Principal principal, @ModelAttribute(value = "token") String token, HttpSession httpSession,RedirectAttributes redir) throws InvalidTokenException, InsufficientScopeException, InvalidRequestException, IOException {
        System.out.println(token + "   TOKEN");
        ModelMap paymentData;
        synchronized (httpSession) {
            paymentData = (ModelMap) httpSession.getAttribute("paymentPrepareData");
            httpSession.removeAttribute("paymentPrepareData");
        }
        if (paymentData == null) {
            return new ModelAndView("redirect:/merchants/input");
        }
        DefaultApiClient apiClient = new DefaultApiClient(yandexMoneyProperties.clientId(), true);
        OAuth2Session oAuth2Session = new OAuth2Session(apiClient);
        oAuth2Session.setAccessToken(token);
        String payInfo = "Purchase " + paymentData.get("amount") + paymentData.get("currency")
                + " from " + principal.getName() + ". Total transferred amount: " + paymentData.get("sumToPay")
                + ", Commission: " + paymentData.get("commission") + ", Amount to be credited to user wallet: " + paymentData.get("amount");
        logger.info(payInfo);
        P2pTransferParams p2pTransferParams = new P2pTransferParams.Builder(yandexMoneyProperties.companyYandexMoneyWalletId())
                .setAmount((BigDecimal) paymentData.get("sumToPay"))
                .setComment("Purchase " + paymentData.get("amount") + paymentData.get("currency") + " at the S.E. Birzha")
                .create();
        RequestPayment.Request request = RequestPayment.Request.newInstance(p2pTransferParams);
        RequestPayment execute;
        ModelAndView redirectToMerchantError = new ModelAndView(merchantInputErrorPage);
        try {
            execute = oAuth2Session.execute(request);
            BaseRequestPayment.Status responseStatus = execute.status;
            if (responseStatus.equals(BaseRequestPayment.Status.REFUSED)) {
                switch (execute.error) {
                    case NOT_ENOUGH_FUNDS:
                        redir.addFlashAttribute("error", "merchants.notEnoughMoney");
                        return redirectToMerchantError;
                    case AUTHORIZATION_REJECT:
                        redir.addFlashAttribute("error", "merchants.authRejected");
                        return redirectToMerchantError;
                    case LIMIT_EXCEEDED:
                        redir.addFlashAttribute("error", "merchants.limitExceed");
                        return redirectToMerchantError;
                    case ACCOUNT_BLOCKED:
                        return new ModelAndView("redirect:" + execute.accountUnblockUri);
                    case EXT_ACTION_REQUIRED:
                        return new ModelAndView("redirect:" + execute.extActionUri);
                    default:
                        logger.fatal(execute.error);
                        redir.addFlashAttribute("error", "merchants.internalError");
                        return redirectToMerchantError;
                }
            }
        } catch (IOException | InvalidRequestException | InsufficientScopeException | InvalidTokenException e) {
            logger.error(e.getMessage());
            redir.addFlashAttribute("error", "merchants.internalError");
            return redirectToMerchantError;
        }
        ProcessPayment processPayment = oAuth2Session.execute(new ProcessPayment.Request(execute.requestId));
        if (processPayment.status.equals(ProcessPayment.Status.SUCCESS)) {
            final int idByEmail = userService.getIdByEmail(principal.getName());
            final int currencyId = (Integer) paymentData.get("currency");
            int walletId = walletService.getWalletId(idByEmail, currencyId);
            if (walletId==0){
                Wallet wallet = new Wallet();
                wallet.setCurrencyId(currencyId);
                wallet.setUserId(idByEmail);
                walletId = walletService.createNewWallet(wallet);
            }
            walletService.setWalletABalance(walletId, ((BigDecimal) paymentData.get("amount")).doubleValue());
            CompanyTransaction companyTransaction = new CompanyTransaction();
            companyTransaction.setCurrencyId(currencyId);
            companyTransaction.setDate(LocalDateTime.now());
            companyTransaction.setMerchantId((Integer) paymentData.get("merchant"));
            companyTransaction.setOperationTypeId(OperationType.INPUT.type);
            companyTransaction.setSum((BigDecimal) paymentData.get("amount"));
            companyTransaction.setWalletId(companyWalletService.findByCurrencyId(currencyId).getId());
            companyTransaction = companyTransactionService.create(companyTransaction);
            logger.info(companyTransaction);
            Transaction transaction = new Transaction();
            transaction.setAmount(((BigDecimal) paymentData.get("sumToPay")).doubleValue());
            transaction.setCommissionId(commissionService.findCommissionByType(OperationType.INPUT).getId());
            transaction.setTransactionType(Payment.TransactionType.INPUT);
            transaction.setWalletId(walletId);
            transaction.setDate(LocalDateTime.now());
            transactionService.create(transaction);
            logger.info(transaction.toString());
            redir.addFlashAttribute("message","Спасибо! Вы успешно ввели "+((BigDecimal) paymentData.get("amount")).setScale(2,BigDecimal.ROUND_CEILING) + " " + walletService.getCurrencyName(currencyId));
            return new ModelAndView("redirect:/mywallets");
        } else if (processPayment.status.equals(ProcessPayment.Status.REFUSED)) {
            switch (processPayment.error) {
                case NOT_ENOUGH_FUNDS:
                    redir.addFlashAttribute("error", "merchants.notEnoughMoney");
                    return redirectToMerchantError;
                case ACCOUNT_BLOCKED:
                    return new ModelAndView("redirect:" + execute.accountUnblockUri);
            }
        }
        redir.addFlashAttribute("error", "merchants.internalError");
        return redirectToMerchantError;
    }

    @RequestMapping(value = "/payment/output")
    public ModelAndView processOutputPayment(Principal principal, CreditsWithdrawal creditsWithdrawal,RedirectAttributes redir) throws InvalidTokenException, InsufficientScopeException, InvalidRequestException, IOException {
        String email = principal.getName();
        final int currency = creditsWithdrawal.getCurrency();
        final int idByEmail = userService.getIdByEmail(email);
        final int walletId = walletService.getWalletId(idByEmail, currency);
        BigDecimal sumToWithdraw = (BigDecimal.valueOf(commissionService.getCommissionByType(OperationType.OUTPUT)).divide(BigDecimal.valueOf(100L), BigDecimal.ROUND_CEILING)).add(BigDecimal.valueOf(creditsWithdrawal.getSum())).setScale(2, BigDecimal.ROUND_CEILING);
        ModelAndView redirectToMerchantError = new ModelAndView(merchantOutputErrorPage);
        if (walletService.ifEnoughMoney(walletId, sumToWithdraw.doubleValue())) {
            DefaultApiClient apiClient = new DefaultApiClient(yandexMoneyProperties.clientId(), true);
            OAuth2Session oAuth2Session = new OAuth2Session(apiClient);
            oAuth2Session.setAccessToken(yandexMoneyProperties.accessToken());
            P2pTransferParams p2pTransferParams = new P2pTransferParams.Builder(creditsWithdrawal.getMeansOfPaymentId())
                    .setAmount(BigDecimal.valueOf(creditsWithdrawal.getSum()))
                    .create();
            RequestPayment.Request request = RequestPayment.Request.newInstance(p2pTransferParams);
            RequestPayment execute;
            try {
                execute = oAuth2Session.execute(request);
                BaseRequestPayment.Status responseStatus = execute.status;
                if (responseStatus.equals(BaseRequestPayment.Status.REFUSED)) {
                    switch (execute.error) {
                        default:
                            logger.error(execute.error);
                            redir.addFlashAttribute("error", "merchants.authRejected");
                            return redirectToMerchantError;
                    }
                }
            } catch (IOException | InvalidRequestException | InsufficientScopeException | InvalidTokenException e) {
                logger.error(e.getMessage());
                redir.addFlashAttribute("error", "merchants.internalError");
                return redirectToMerchantError;
            }
            ProcessPayment processPayment = oAuth2Session.execute(new ProcessPayment.Request(execute.requestId));
            if (processPayment.status.equals(ProcessPayment.Status.SUCCESS)) {
                walletService.setWalletABalance(walletId, sumToWithdraw.negate().doubleValue());
                Transaction transaction = new Transaction();
                transaction.setAmount(sumToWithdraw.doubleValue());
                transaction.setCommissionId(commissionService.findCommissionByType(OperationType.OUTPUT).getId());
                transaction.setDate(LocalDateTime.now());
                transaction.setTransactionType(Payment.TransactionType.OUTPUT);
                transaction.setWalletId(walletId);
                transactionService.create(transaction);
                redir.addFlashAttribute("message","Вы успешно вывели средства : " + BigDecimal.valueOf(creditsWithdrawal.getSum()).setScale(2,BigDecimal.ROUND_CEILING) + " " + walletService.getCurrencyName(currency));
                return new ModelAndView("redirect:/mywallets");
            } else if (processPayment.status.equals(ProcessPayment.Status.REFUSED)) {
                switch (processPayment.error) {
                    default:
                        redir.addFlashAttribute("error", "merchants.internalError");
                        return redirectToMerchantError;
                }
            }
        }
        redir.addFlashAttribute("error", "merchants.notEnoughWalletMoney");
        return redirectToMerchantError;
    }
}