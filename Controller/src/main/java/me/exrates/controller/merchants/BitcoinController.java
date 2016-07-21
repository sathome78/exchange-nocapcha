package me.exrates.controller.merchants;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.PendingPayment;
import me.exrates.service.BitcoinService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.MerchantInternalException;
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

import java.security.Principal;
import java.util.Locale;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/bitcoin")
public class BitcoinController {

    private final BitcoinService bitcoinService;
    private final MerchantService merchantService;
    private final MessageSource messageSource;

    private final Logger LOG = LogManager.getLogger("merchant");

    @Autowired
    public BitcoinController(final BitcoinService bitcoinService,
                             final MerchantService merchantService,
                             final MessageSource messageSource)
    {
        this.bitcoinService = bitcoinService;
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
            final PendingPayment pendingPayment = bitcoinService.createInvoice(creditsOperation);
            final String notification = merchantService
                    .sendDepositNotification(pendingPayment
                                    .getAddress().orElseThrow(
                                    () ->new MerchantInternalException("Address not presented"))
                            ,email ,locale, creditsOperation, "merchants.depositNotification.body");
            final HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "text/plain; charset=utf-8");
            return new ResponseEntity<>(notification, httpHeaders, OK);
        } catch (final InvalidAmountException|RejectedPaymentInvoice e) {
            final String error = messageSource.getMessage("merchants.incorrectPaymentDetails", null, locale);
            LOG.warn(error);
            return new ResponseEntity<>(error, NO_CONTENT);
        }
    }
}
