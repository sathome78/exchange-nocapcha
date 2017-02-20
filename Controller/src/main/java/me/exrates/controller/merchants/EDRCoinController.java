package me.exrates.controller.merchants;

import com.google.gson.Gson;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.PendingPayment;
import me.exrates.service.EDRCService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.invoice.RejectedPaymentInvoice;
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

import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
    private MessageSource source;

    private static final Logger LOG = LogManager.getLogger("merchant");

    @RequestMapping(value = "/payment/prepare", method = POST)
    public ResponseEntity<String> preparePayment(final @RequestBody String body,
        final Principal principal, final Locale locale)
    {
        final Payment payment = new Gson().fromJson(body, Payment.class);
        final String error;

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "text/plain; charset=utf-8");

        if (!merchantService.checkInputRequestsLimit(payment.getMerchant(), principal.getName())){
            error = source.getMessage("merchants.InputRequestsLimit", null, locale);

            return new ResponseEntity<>(error, httpHeaders, HttpStatus.FORBIDDEN);
        }
        final String email = principal.getName();
        final CreditsOperation creditsOperation = merchantService
            .prepareCreditsOperation(payment, email)
            .orElseThrow(InvalidAmountException::new);

        try {
            final PendingPayment pendingPayment = edrcService
                .createPaymentInvoice(creditsOperation);
            final String notification = merchantService
                .sendDepositNotification(pendingPayment
                        .getAddress().orElseThrow(() -> new MerchantInternalException("Address not presented")),
                        email , locale, creditsOperation, "merchants.depositNotification.body");
            return new ResponseEntity<>(notification, httpHeaders, OK);
        } catch (final InvalidAmountException|RejectedPaymentInvoice e) {
            LOG.error(e);
            error = source.getMessage("merchants.incorrectPaymentDetails", null, locale);
        }
        catch (final MerchantInternalException e) {
            LOG.error(e);
            error = source.getMessage("merchants.internalError", null, locale);
        }
        return new ResponseEntity<>(error, httpHeaders, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "payment/received",method = POST)
    public ResponseEntity<Void> paymentHandler (final @RequestParam Map<String,String> params) {
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
