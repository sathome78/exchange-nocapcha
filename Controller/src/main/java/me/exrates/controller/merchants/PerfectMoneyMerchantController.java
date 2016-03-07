package me.exrates.controller.merchants;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.model.enums.OperationType;
import me.exrates.service.MerchantService;
import me.exrates.service.PerfectMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/perfectmoney")
public class PerfectMoneyMerchantController {

    @Autowired
    private PerfectMoneyService perfectMoneyService;

    @Autowired
    private MerchantService merchantService;

    @RequestMapping(value = "payment/provide",method = RequestMethod.POST)
    public ModelAndView outputPayment(Payment payment,Principal principal,RedirectAttributes redir) {
        System.out.println(payment);
        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperation.isPresent()) {
            redir.addFlashAttribute("error", "merchants.invalidSum");
            return new ModelAndView("redirect:/mywallets");
        }
        final Transaction transaction = perfectMoneyService.preparePaymentTransactionRequest(creditsOperation.get());
        final String response = perfectMoneyService.provideOutputPayment(payment.getDestination(), transaction);
        if (response.equalsIgnoreCase("OK")) {
            perfectMoneyService.provideTransaction(transaction);
            final String sumCurrency = transaction.getAmount().setScale(2, BigDecimal.ROUND_CEILING) + " " + transaction.getCurrency().getName();
            final String message = transaction.getOperationType() == OperationType.INPUT ? "merchants.successfulBalanceWithdraw"
                    : "merchants.successfulBalanceWithdraw";
            redir.addFlashAttribute("message",message);
            redir.addFlashAttribute("sumCurrency",sumCurrency);
            return new ModelAndView("redirect:/mywallets");
        }
        if (response.equalsIgnoreCase("INTERNAL_ERROR")) {
            redir.addFlashAttribute("error", "merchants.internalError");
        } else if (response.equalsIgnoreCase("INVALID_AMOUNT")) {
            redir.addFlashAttribute("error","merchants.incorrectPaymentDetails");
        }
        return new ModelAndView("redirect:/merchants/input");
    }

    @RequestMapping(value = "payment/prepare",method = RequestMethod.POST,
            headers = {"Content-type=application/json"})
    public ResponseEntity<Map<String,String>> preparePayment(Payment payment, Principal principal,
                                                             HttpSession httpSession) {
        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperation.isPresent()) {
            final Map<String, String> errors = new HashMap<String, String>() {
                {
                    put("error", "merchants.invalidSum");
                }
            };
            return new ResponseEntity<>(errors, HttpStatus.NO_CONTENT);
        }
        final Transaction transaction = perfectMoneyService.preparePaymentTransactionRequest(creditsOperation.get());
        final Map<String, String> params = perfectMoneyService.getPerfectMoneyParams(transaction);
        final Object mutex = WebUtils.getSessionMutex(httpSession);
        synchronized (mutex) {
            httpSession.setAttribute("transaction",transaction);
            httpSession.setAttribute("payeeParams", params);
        }
        return new ResponseEntity<>(params,HttpStatus.OK);
    }

    @RequestMapping(value = "payment/success",method = RequestMethod.POST)
    public RedirectView successPayment(@RequestParam Map<String,String> response, HttpSession httpSession,
                                       RedirectAttributes redir) {
        final Object mutex = WebUtils.getSessionMutex(httpSession);
        final Map<String,String> payeeParams;
        final Transaction openTransaction;
        synchronized (mutex) {
            openTransaction = (Transaction) httpSession.getAttribute("transaction");
            payeeParams = (Map<String,String>)httpSession.getAttribute("payeeParams");
            httpSession.removeAttribute("transaction");
            httpSession.removeAttribute("payeeParams");
        }
        perfectMoneyService.consumePerfectMoneyResponse(response,payeeParams);
        final String hash = perfectMoneyService.computePaymentHash(payeeParams);
        if (response.get("V2_HASH").equals(hash)) {
            perfectMoneyService.provideTransaction(openTransaction);
            final String sumCurrency = openTransaction.getAmount().setScale(2, BigDecimal.ROUND_CEILING) + " " + openTransaction.getCurrency().getName();
            final String message = openTransaction.getOperationType() == OperationType.INPUT ? "merchants.successfulBalanceDeposit"
                    : "merchants.successfulBalanceWithdraw";
            redir.addFlashAttribute("message",message);
            redir.addFlashAttribute("sumCurrency",sumCurrency);
            return new RedirectView("/mywallets");
        }
        perfectMoneyService.invalidateTransaction(openTransaction);
        redir.addFlashAttribute("error", "merchants.incorrectPaymentDetails");
        return new RedirectView("/merchants/".concat(
                openTransaction.getOperationType() == OperationType.INPUT ? "input" : "output"));
    }

    @RequestMapping(value = "payment/status",method = RequestMethod.POST)
    public void payStatus(@RequestBody String body) {
        System.out.println(body);
    }

    @RequestMapping(value = "payment/failure",method = RequestMethod.POST)
    public void failurePayment(@RequestBody String body) {
        System.out.println(body);
    }
}