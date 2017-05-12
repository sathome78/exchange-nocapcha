package me.exrates.controller.merchants;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.ripple.RippleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by maks on 11.05.2017.
 */

@Log4j2
@Controller
@RequestMapping("/merchants/ripple")
public class RippleController {

    @Autowired
    private RippleService rippleService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    MerchantService merchantService;

    @RequestMapping(value = "/payment/prepare", method = POST)
    public ResponseEntity<Map<String, String>> preparePayment(final @RequestBody Payment payment,
                                                              final Principal principal,
                                                              final Locale locale)
    {
        if (!merchantService.checkInputRequestsLimit(payment.getCurrency(), principal.getName())){
            final Map<String,String> error = new HashMap<>();
            error.put("error", messageSource.getMessage("merchants.InputRequestsLimit", null, locale));

            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }

        final String email = principal.getName();
        final CreditsOperation creditsOperation = merchantService
                .prepareCreditsOperation(payment, email)
                .orElseThrow(InvalidAmountException::new);
        try {
            final String account = rippleService.createAddress(creditsOperation);

            final String notification = merchantService
                    .sendDepositNotification(account,email ,locale, creditsOperation, "merchants.depositNotification.body");
            final HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "text/plain; charset=utf-8");
            Map<String,String> responseMap = new TreeMap<>();
            responseMap.put("notification", notification);
            responseMap.put("qr", account + "/"
                    + creditsOperation.getAmount().add(creditsOperation.getCommissionAmount()).doubleValue()
                    + "/image.png");

            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } catch (Exception e) {
            final Map<String,String> error = new HashMap<>();
            error.put("error", messageSource.getMessage("merchants.incorrectPaymentDetails", null, locale));
            log.error(e);
            return new ResponseEntity<>(error, NOT_FOUND);
        }
    }
}



