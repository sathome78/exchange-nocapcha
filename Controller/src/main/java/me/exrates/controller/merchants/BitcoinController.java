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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
    public ResponseEntity<Map<String, String>> preparePayment(final @RequestBody Payment payment,
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
            Map<String,String> responseMap = new TreeMap<>();
            responseMap.put("notification", notification);
            responseMap.put("qr", "bitcoin:" + pendingPayment.getAddress().orElseThrow(
                            () ->new MerchantInternalException("Address not presented")) + "?amount="
                    + creditsOperation.getAmount().add(creditsOperation.getCommissionAmount()).doubleValue() + "&message=Donation%20for%20project%20Exrates");

            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } catch (final InvalidAmountException|RejectedPaymentInvoice e) {
            final Map<String,String> error = new HashMap<>();
            error.put("error", messageSource.getMessage("merchants.incorrectPaymentDetails", null, locale));
            LOG.warn(error);
            return new ResponseEntity<>(error, NO_CONTENT);
        }
    }

    @RequestMapping(value = "/payment/accept",method = GET)
    public RedirectView acceptPayment(@RequestParam int id, @RequestParam String hash,
                                      @RequestParam BigDecimal amount, RedirectAttributes redir){


        if (!bitcoinService.provideTransaction(id, hash, amount)){
            final String message = "merchants.internalError";
            redir.addFlashAttribute("message", message);
        }

        return new RedirectView("/admin/bitcoinConfirmation");
    }
}
