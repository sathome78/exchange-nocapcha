package me.exrates.controller.merchants;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.service.InvoiceService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.RejectedPaymentInvoice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.Locale;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@Controller
@RequestMapping("/merchants/invoice")
public class InvoiceController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private MessageSource messageSource;

    private static final Logger LOG = LogManager.getLogger("merchant");

    @RequestMapping(value = "/payment/prepare",method = POST)
    public ResponseEntity<String> preparePayment(final @RequestBody Payment payment,
                                                 final Principal principal,
                                                 final Locale locale)
    {
        final String email = principal.getName();
        LOG.debug("Preparing payment: " + payment + " for: " + email);
        final CreditsOperation creditsOperation = merchantService
            .prepareCreditsOperation(payment, email)
            .orElseThrow(InvalidAmountException::new);
            LOG.debug("Prepared payment: "+creditsOperation);
            try {
                final Transaction transaction = invoiceService.createPaymentInvoice(creditsOperation);
                final String notification = merchantService
                        .sendDepositNotification("123456789",
                                email , locale, creditsOperation, "merchants.depositNotificationWithCurrency.body");
                final HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Content-Type", "text/plain; charset=utf-8");
                return new ResponseEntity<>(notification, httpHeaders, OK);
            } catch (final InvalidAmountException|RejectedPaymentInvoice e) {
                final String error = messageSource.getMessage("merchants.incorrectPaymentDetails", null, locale);
                LOG.warn(error);
                return new ResponseEntity<>(error, NOT_FOUND);
            }
    }

    @RequestMapping(value = "/payment/accept",method = GET)
    public RedirectView acceptPayment(@RequestParam int id, RedirectAttributes redir){

        if (!invoiceService.provideTransaction(id)){
            final String message = "merchants.internalError";
            redir.addFlashAttribute("message", message);
        }

        return new RedirectView("/transaction_invoice");
    }
}
