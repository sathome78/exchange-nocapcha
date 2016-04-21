package me.exrates.controller.merchants;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.model.enums.OperationType;
import me.exrates.service.MerchantService;
import me.exrates.service.Privat24Service;
import me.exrates.service.TransactionService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/merchants/privat24")
public class Privat24MerchantController {
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private Privat24Service privat24Service;

    @Autowired
    private TransactionService transactionService;

    private static final Logger LOG = LogManager.getLogger("merchant");

    @RequestMapping(value = "/payment/prepare", method = RequestMethod.POST)
    public RedirectView preparePayment(@Valid @ModelAttribute("payment") Payment payment,
                                       BindingResult result, Principal principal, RedirectAttributes redir) {

        LOG.debug("Begin method: preparePayment.");
        final String errorRedirectView = "/merchants/".concat(payment.getOperationType() == OperationType.INPUT ?
                "/input": "/output");

        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperation.isPresent()) {
            redir.addFlashAttribute("error", "merchants.invalidSum");
            return new RedirectView(errorRedirectView);
        }

        return privat24Service.preparePayment(creditsOperation.get(), principal.getName());

    }

    @RequestMapping(value = "payment/status",method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(final @RequestParam Map<String,String> params,
                                              final RedirectAttributes redir) {

        LOG.debug("Begin method: statusPayment.");
//        LOG.debug("params:" + params.toString());
        final ResponseEntity<Void> response = new ResponseEntity<>(OK);

        String signature = params.get("signature");
        String payment = params.get("payment");
        LOG.debug("Get status payment: " + payment);
        String[] arrayResponse = payment.split("&");
        Map<String,String> mapResponse = new HashMap<>();
        for (String value : arrayResponse){
            mapResponse.put(value.split("=")[0], value.split("=")[1]);
        }


        if (privat24Service.confirmPayment(mapResponse, signature, payment)) {
            return response;
        }


        return new ResponseEntity<>(BAD_REQUEST);
    }

    @RequestMapping(value = "payment/success",method = RequestMethod.POST)
    public RedirectView successPayment(@RequestParam Map<String,String> response, RedirectAttributes redir) {

        LOG.debug("Begin method: successPayment.");

        String signature = response.get("signature");
        String payment = response.get("payment");
        String[] arrayResponse = payment.split("&");
        Map<String,String> mapResponse = new HashMap<>();
        for (String value : arrayResponse){
            mapResponse.put(value.split("=")[0], value.split("=")[1]);
        }

        Transaction transaction;
        try{
            transaction = transactionService.findById(Integer.parseInt(mapResponse.get("order")));
            if (!transaction.isProvided()){
                if (!privat24Service.confirmPayment(mapResponse, signature, payment)) {

                    redir.addFlashAttribute("error", "merchants.authRejected");
                    return new RedirectView("/merchants/input");
                }
            }
        }catch (EmptyResultDataAccessException e){
            LOG.error(e);
            redir.addFlashAttribute("error", "merchants.incorrectPaymentDetails");
            return new RedirectView("/merchants/input");
        }

        merchantService.formatResponseMessage(transaction)
                .entrySet()
                .forEach(entry->redir.addFlashAttribute(entry.getKey(),entry.getValue()));
        final String message = "merchants.successfulBalanceDeposit";
        redir.addFlashAttribute("message", message);

        return new RedirectView("/mywallets");
    }

    }
