package me.exrates.controller.merchants;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.service.EDCService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Locale;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/edc")
public class EDCController {

    private final EDCService edcService;
    private final MerchantService merchantService;
    private final MessageSource messageSource;

    private final Logger LOG = LogManager.getLogger("merchant");

    @Autowired
    public EDCController(final EDCService edcService,
                         final MerchantService merchantService,
                         final MessageSource messageSource)
    {
        this.edcService = edcService;
        this.merchantService = merchantService;
        this.messageSource = messageSource;
    }

    @RequestMapping(value = "/payment/prepare", method = POST)
    public ResponseEntity<String> preparePayment(final @RequestBody Payment payment,
                                                 final Principal principal,
                                                 final Locale locale)
    {
        final String email = principal.getName();
        final CreditsOperation creditsOperation = merchantService
                .prepareCreditsOperation(payment, email)
                .orElseThrow(InvalidAmountException::new);
        try {
            final String account = edcService.createInvoice(creditsOperation);
            final String notification = merchantService
                    .sendDepositNotification(account,email ,locale, creditsOperation, "merchants.depositNotification.body");
            final HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "text/plain; charset=utf-8");
            return new ResponseEntity<>(notification, httpHeaders, OK);
        } catch (Exception e) {
            final String error = messageSource.getMessage("merchants.incorrectPaymentDetails", null, locale);
            LOG.error(e);
            return new ResponseEntity<>(error, NO_CONTENT);
        }
    }
}
