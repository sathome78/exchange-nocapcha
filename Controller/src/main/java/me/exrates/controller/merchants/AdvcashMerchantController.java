package me.exrates.controller.merchants;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.model.enums.OperationType;
import me.exrates.service.AdvcashService;
import me.exrates.service.MerchantService;
import me.exrates.service.TransactionService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/merchants/advcash")
public class AdvcashMerchantController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private AdvcashService advcashService;

    @Autowired
    private TransactionService transactionService;

    private static final Logger logger = LogManager.getLogger(AdvcashMerchantController.class);

    private static final String merchantInputErrorPage = "redirect:/merchants/input";

    @RequestMapping(value = "/payment/prepare", method = RequestMethod.POST)
    public RedirectView preparePayment(@Valid @ModelAttribute("payment") Payment payment,
                                       BindingResult result, Principal principal, RedirectAttributes redir) {

        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperation.isPresent()) {
            redir.addFlashAttribute("error", "merchants.invalidSum");
            return new RedirectView("/dashboard");
        }

        final OperationType operationType = creditsOperation.get().getOperationType();

        if (operationType==OperationType.INPUT){
            return advcashService.preparePayment(creditsOperation.get(), principal.getName());
        }else {
            // TODO questions about output
//            url = "/advcash/output";
            return new RedirectView("/dashboard");
        }

        }

    @RequestMapping(value = "payment/success",method = RequestMethod.POST)
    public RedirectView successPayment(@RequestParam Map<String,String> response, RedirectAttributes redir) {

        Transaction transaction = transactionService.findById(Integer.parseInt(response.get("ac_order_id")));

        if (response.get("ac_transaction_status").equals("COMPLETED") && advcashService.checkHashTransactionByTransactionId(transaction.getId(), response.get("transaction_hash"))){
            merchantService.formatResponseMessage(transaction)
                    .entrySet()
                    .forEach(entry->redir.addFlashAttribute(entry.getKey(),entry.getValue()));
            final String message = "merchants.successfulBalanceDeposit";
            redir.addFlashAttribute("message", message);
            advcashService.provideTransaction(transaction);

            return new RedirectView("/dashboard");

        }
//        advcashService.invalidateTransaction(transaction);

        final String message = "merchants.internalError";
        redir.addFlashAttribute("message", message);

        return new RedirectView("/dashboard");
        }
    }
