package me.exrates.controller.merchants;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.service.MerchantService;
import me.exrates.service.NixMoneyService;
import me.exrates.service.TransactionService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/merchants/nixmoney")
public class NixMoneyMerchantController {
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private NixMoneyService nixMoneyService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    private static final Logger logger = LogManager.getLogger(NixMoneyMerchantController.class);

    @RequestMapping(value = "/payment/prepare", method = RequestMethod.POST)
    public RedirectView preparePayment(@Valid @ModelAttribute("payment") Payment payment,
                                       BindingResult result, Principal principal, RedirectAttributes redir, final HttpServletRequest request) {

        if (!merchantService.checkInputRequestsLimit(payment.getCurrency(), principal.getName())){
            redir.addAttribute("errorNoty", messageSource.getMessage("merchants.InputRequestsLimit", null, localeResolver.resolveLocale(request)));
            return new RedirectView("/dashboard");
        }

        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperation.isPresent()) {
            redir.addAttribute("errorNoty", messageSource.getMessage("merchants.incorrectPaymentDetails", null, localeResolver.resolveLocale(request)));
            return new RedirectView("/dashboard");
        }

        return nixMoneyService.preparePayment(creditsOperation.get(), principal.getName());

    }

    @RequestMapping(value = "payment/status",method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(@RequestParam Map<String,String> params, RedirectAttributes redir) {

        final ResponseEntity<Void> response = new ResponseEntity<>(OK);
        logger.info("Response: " + params);

        if (nixMoneyService.confirmPayment(params)) {
            return response;
        }


        return new ResponseEntity<>(BAD_REQUEST);
    }

    @RequestMapping(value = "payment/success",method = RequestMethod.POST)
    public RedirectView successPayment(@RequestParam Map<String,String> response, RedirectAttributes redir, final HttpServletRequest request) {

        Transaction transaction;
        logger.info("Response: " + response);
        try{
            transaction = transactionService.findById(Integer.parseInt(response.get("PAYMENT_ID")));
            if (!transaction.isProvided()){
                redir.addAttribute("errorNoty", messageSource.getMessage("merchants.authRejected", null, localeResolver.resolveLocale(request)));
                return new RedirectView("/dashboard");
            }
        }catch (EmptyResultDataAccessException e){
            logger.error(e);
            redir.addAttribute("errorNoty", messageSource.getMessage("merchants.incorrectPaymentDetails", null, localeResolver.resolveLocale(request)));

            return new RedirectView("/dashboard");
        }

        redir.addAttribute("successNoty", messageSource.getMessage("merchants.successfulBalanceDeposit",
                merchantService.formatResponseMessage(transaction).values().toArray(), localeResolver.resolveLocale(request)));

        return new RedirectView("/dashboard");
    }

    @RequestMapping(value = "payment/failure",method = RequestMethod.POST)
    public RedirectView failurePayment(@RequestParam Map<String,String> response, RedirectAttributes redir, final HttpServletRequest request) {

        redir.addAttribute("errorNoty", messageSource.getMessage("merchants.authRejected", null, localeResolver.resolveLocale(request)));

        try {
            Transaction transaction = transactionService.findById(Integer.parseInt(response.get("PAYMENT_ID")));
            nixMoneyService.invalidateTransaction(transaction);
        } catch (EmptyResultDataAccessException e) {
            logger.error(e);
            return new RedirectView("/dashboard");
        }

        return new RedirectView("/dashboard");

        }

    }
