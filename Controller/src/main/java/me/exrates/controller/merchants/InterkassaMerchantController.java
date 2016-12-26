package me.exrates.controller.merchants;

import com.google.gson.Gson;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.service.InterkassaService;
import me.exrates.service.MerchantService;
import me.exrates.service.TransactionService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/merchants/interkassa")
public class InterkassaMerchantController {
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private InterkassaService interkassaService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    private static final Logger LOG = LogManager.getLogger("merchant");

    @RequestMapping(value = "payment/prepare",method = RequestMethod.POST)
    public ResponseEntity<Map<String,String>> preparePayment(@RequestBody String body, Principal principal, final Locale locale) {
        final Payment payment = new Gson().fromJson(body, Payment.class);
        if (!merchantService.checkInputRequestsLimit(payment.getMerchant(), principal.getName())){
            final Map<String,String> error = new HashMap<>();
            error.put("error", messageSource.getMessage("merchants.InputRequestsLimit", null, locale));

            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }

        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperation.isPresent()) {
            final Map<String, String> errors = new HashMap<String, String>() {
                {
                    put("error", messageSource.getMessage("merchants.incorrectPaymentDetails", null, locale));
                }
            };
            return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
        }

        final Map<String, String> params = interkassaService.preparePayment(creditsOperation.get(), principal.getName());;

        return new ResponseEntity<>(params,HttpStatus.OK);
    }

    @RequestMapping(value = "payment/status",method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(final @RequestParam Map<String,String> params,
                                              final RedirectAttributes redir) {

        LOG.debug("Begin method: statusPayment.");
        LOG.info("Response: " + params);
        final ResponseEntity<Void> response = new ResponseEntity<>(OK);

        if (interkassaService.confirmPayment(params)) {
            return response;
        }

        return new ResponseEntity<>(BAD_REQUEST);
    }

    @RequestMapping(value = "payment/success",method = RequestMethod.POST)
    public RedirectView successPayment(@RequestParam Map<String,String> response, RedirectAttributes redir, final HttpServletRequest request) {

        LOG.debug("Begin method: successPayment.");
        LOG.info("Response: " + response);

        Transaction transaction;
        try{
            transaction = transactionService.findById(Integer.parseInt(response.get("ik_pm_no")));
            if (!transaction.isProvided()){
                redir.addAttribute("errorNoty", messageSource.getMessage("merchants.authRejected", null, localeResolver.resolveLocale(request)));
                LOG.debug("Transaction is not provided.");
                return new RedirectView("/dashboard");
            }
        }catch (EmptyResultDataAccessException e){
            LOG.error(e);
            redir.addAttribute("errorNoty", messageSource.getMessage("merchants.incorrectPaymentDetails", null, localeResolver.resolveLocale(request)));
            return new RedirectView("/dashboard");
        }

        redir.addAttribute("successNoty", messageSource.getMessage("merchants.successfulBalanceDeposit",
                merchantService.formatResponseMessage(transaction).values().toArray(), localeResolver.resolveLocale(request)));


        LOG.debug("Method successful, redirect /dashboard.");
        return new RedirectView("/dashboard");
    }

    }
