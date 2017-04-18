package me.exrates.controller.merchants;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.service.EDCMerchantService;
import me.exrates.service.EDCService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static org.springframework.http.HttpStatus.*;
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
    private final EDCMerchantService edcMerchantService;

    private final Logger LOG = LogManager.getLogger("merchant");

    @Autowired
    public EDCController(final EDCService edcService,
                         final MerchantService merchantService,
                         final MessageSource messageSource,
                         final EDCMerchantService edcMerchantService)
    {
        this.edcService = edcService;
        this.merchantService = merchantService;
        this.messageSource = messageSource;
        this.edcMerchantService = edcMerchantService;
    }

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
//            final String account = edcService.createInvoice(creditsOperation); // node
            final String account = edcMerchantService.createAddress(creditsOperation); // merchant
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
            LOG.error(e);
            return new ResponseEntity<>(error, NOT_FOUND);
        }
    }

    @RequestMapping(value = "payment/received",method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(@RequestBody Map<String,String> params, RedirectAttributes redir) {

        final ResponseEntity<Void> responseOK = new ResponseEntity<>(OK);
        LOG.info("Response: " + params);

        try {
            boolean isEmpty = edcMerchantService.checkMerchantTransactionIdIsEmpty(params.get("id"));
            if (isEmpty){
                edcMerchantService.createAndProvideTransaction(params);
                return responseOK;
            }else {
                return responseOK;
            }
        }catch (Exception e){
            LOG.error(e);
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }
}
