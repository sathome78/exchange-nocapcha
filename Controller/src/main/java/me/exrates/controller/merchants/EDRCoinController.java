package me.exrates.controller.merchants;

import com.google.gson.Gson;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.PendingPayment;
import me.exrates.service.EDRCService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RejectedPaymentInvoice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/edrcoin")
public class EDRCoinController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private EDRCService edrcService;

    @Autowired
    private ApplicationContext context;

    private static final Logger logger = LogManager.getLogger("merchant");

    @RequestMapping(value = "/payment/prepare", method = POST)
    public ResponseEntity<String> preparePayment(final @RequestBody String body,
        final Principal principal, final Locale locale)
    {
        final Payment payment = new Gson().fromJson(body, Payment.class);
        final String email = principal.getName();
        logger.debug("Preparing payment: " + payment + " for: " + email);
        final CreditsOperation creditsOperation = merchantService
            .prepareCreditsOperation(payment, email)
            .orElseThrow(InvalidAmountException::new);
        logger.debug("Prepared payment: "+ creditsOperation);
        try {
            final PendingPayment pendingPayment = edrcService
                .createPaymentInvoice(creditsOperation);
            final String notification = merchantService
                .sendDepositNotification(pendingPayment
                        .getAddress().orElseThrow(
                        ()->new MerchantInternalException("Address not presented"))
                    ,email ,locale, creditsOperation);
            logger.info("New pending EDRCoin payment :"+ pendingPayment);
            logger.info(notification);
            final HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "text/plain; charset=utf-8");
            return new ResponseEntity<>(notification, httpHeaders, OK);
        } catch (InvalidAmountException|RejectedPaymentInvoice e) {
            final String error = context.getMessage("merchants.incorrectPaymentDetails", null, locale);
            return new ResponseEntity<>(error,HttpStatus.NO_CONTENT);
        }
    }

    @RequestMapping(value = "payment/received",method = POST)
    public ResponseEntity<Void> paymentHandler (final @RequestParam Map<String,String> params) {
        logger.info("Recieved response from edrc-coin: " + params);
        final ResponseEntity<Void> response = new ResponseEntity<>(OK);
        final String xml = params.get("operation_xml");
        final String signature = params.get("signature");
        if (Objects.isNull(xml) || Objects.isNull(signature)) {
            return response;
        }
        if (edrcService.confirmPayment(xml,signature)) {
            return response;
        }
        return new ResponseEntity<>(BAD_REQUEST);
    }
}
