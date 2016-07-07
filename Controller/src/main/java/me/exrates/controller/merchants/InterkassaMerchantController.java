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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.HashMap;
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

    private static final Logger LOG = LogManager.getLogger("merchant");

    @RequestMapping(value = "payment/prepare",method = RequestMethod.POST)
    public ResponseEntity<Map<String,String>> preparePayment(@RequestBody String body, Principal principal) {
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

        final Map<String, String> params = interkassaService.preparePayment(creditsOperation.get(), principal.getName());;

        return new ResponseEntity<>(params,HttpStatus.OK);
    }

    @RequestMapping(value = "payment/status",method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(final @RequestParam Map<String,String> params,
                                              final RedirectAttributes redir) {

        LOG.debug("Begin method: statusPayment.");
        final ResponseEntity<Void> response = new ResponseEntity<>(OK);

        if (interkassaService.confirmPayment(params)) {
            return response;
        }

        return new ResponseEntity<>(BAD_REQUEST);
    }

    @RequestMapping(value = "payment/success",method = RequestMethod.POST)
    public RedirectView successPayment(@RequestParam Map<String,String> response, RedirectAttributes redir) {

        LOG.debug("Begin method: successPayment.");

        Transaction transaction;
        try{
            transaction = transactionService.findById(Integer.parseInt(response.get("ik_pm_no")));
            if (!transaction.isProvided()){
                redir.addFlashAttribute("error", "merchants.authRejected");
                LOG.debug("Transaction is not provided.");
                return new RedirectView("/dashboard");
            }
        }catch (EmptyResultDataAccessException e){
            LOG.error(e);
            redir.addFlashAttribute("error", "merchants.incorrectPaymentDetails");
            return new RedirectView("/dashboard");
        }

        merchantService.formatResponseMessage(transaction)
                .entrySet()
                .forEach(entry->redir.addFlashAttribute(entry.getKey(),entry.getValue()));
        final String message = "merchants.successfulBalanceDeposit";
        redir.addFlashAttribute("message", message);

        LOG.debug("Method successful, redirect /dashboard.");
        return new RedirectView("/dashboard");
    }

    }
