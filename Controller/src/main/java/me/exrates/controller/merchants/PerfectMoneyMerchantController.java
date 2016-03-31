package me.exrates.controller.merchants;

import com.google.gson.Gson;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.model.enums.OperationType;
import me.exrates.service.MerchantService;
import me.exrates.service.PerfectMoneyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpSession;
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

    private static final Logger logger = LogManager.getLogger("merchant");

    @RequestMapping(value = "payment/provide",method = RequestMethod.POST)
    public RedirectView outputPayment(Payment payment,Principal principal,RedirectAttributes redir) {
        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperation.isPresent()) {
            redir.addFlashAttribute("error", "merchants.invalidSum");
            return new RedirectView("/merchants/output");
        }
        perfectMoneyService.provideOutputPayment(payment, creditsOperation.get());
        merchantService.formatResponseMessage(creditsOperation.get())
                .entrySet()
                .forEach(entry->redir.addFlashAttribute(entry.getKey(),entry.getValue()));
        final String message = "merchants.successfulBalanceWithdraw";
            redir.addFlashAttribute("message",message);
        return new RedirectView("/mywallets");
    }

    @RequestMapping(value = "payment/prepare",method = RequestMethod.POST)
    public ResponseEntity<Map<String,String>> preparePayment(@RequestBody String body, Principal principal, HttpSession httpSession) {
        final Payment payment = new Gson().fromJson(body, Payment.class);
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
        logger.info("Response: " + response);
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
            merchantService.formatResponseMessage(openTransaction)
                    .entrySet()
                    .forEach(entry->redir.addFlashAttribute(entry.getKey(),entry.getValue()));
            final String message = openTransaction.getOperationType() == OperationType.INPUT ? "merchants.successfulBalanceDeposit"
                    : "merchants.successfulBalanceWithdraw";
            redir.addFlashAttribute("message", message);
            return new RedirectView("/mywallets");
        }
        perfectMoneyService.invalidateTransaction(openTransaction);
        synchronized (mutex) {
            httpSession.setAttribute("error", "merchants.incorrectPaymentDetails");
        }
        return new RedirectView("/merchants/input");
    }

    @RequestMapping(value = "payment/status",method = RequestMethod.POST)
    public void prepare(@RequestParam Map<String,String> response) {
        System.out.println(response);
    }

    @RequestMapping(value = "payment/failure",method = RequestMethod.POST)
    public RedirectView failurePayment(@RequestBody String body,HttpSession httpSession,RedirectAttributes redir) {
        final Transaction openTransaction;
        final Object mutex = WebUtils.getSessionMutex(httpSession);
        synchronized (mutex) {
            openTransaction = (Transaction) httpSession.getAttribute("transaction");
            httpSession.removeAttribute("transaction");
            httpSession.removeAttribute("payeeParams");
        }
        perfectMoneyService.invalidateTransaction(openTransaction);
        redir.addFlashAttribute("error", "merchants.authRejected");
        return new RedirectView("/merchants/input");
    }
}