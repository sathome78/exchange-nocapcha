package me.exrates.controller.merchants;

import com.google.gson.Gson;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.PendingPayment;
import me.exrates.service.BlockchainService;
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
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/blockchain")
public class BlockchainController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    private ApplicationContext context;

    private static final Logger logger = LogManager.getLogger("merchant");

    @RequestMapping(value = "/payment/prepare",method = POST)
    public ResponseEntity<String> preparePayment(final @RequestBody String body,
        final Principal principal, final Locale locale) {
            final Payment payment = new Gson().fromJson(body, Payment.class);
            final String email = principal.getName();
            logger.debug("Preparing payment: "+payment+" for: " + email);
        final CreditsOperation creditsOperation = merchantService
            .prepareCreditsOperation(payment, email)
            .orElseThrow(InvalidAmountException::new);
            logger.debug("Prepared payment: "+creditsOperation);
            try {
                final PendingPayment pendingPayment = blockchainService
                    .createPaymentInvoice(creditsOperation);
                final String notification = merchantService
                    .sendDepositNotification(pendingPayment
                        .getAddress().orElseThrow(
                            ()->new MerchantInternalException("Address not presented"))
                        ,email ,locale, creditsOperation);
                logger.info("New pending Blockchain payment :"+ pendingPayment);
                final HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Content-Type", "text/plain; charset=utf-8");
                return new ResponseEntity<>(notification, httpHeaders, OK);
            } catch (final InvalidAmountException|RejectedPaymentInvoice e) {
                final String error = context
                    .getMessage("merchants.incorrectPaymentDetails", null, locale);
                logger.warn(error);
                return new ResponseEntity<>(error, NO_CONTENT);
            }
    }

    @RequestMapping(value = "/payment/received", method = GET)
    public ResponseEntity<String> paymentHandler(final @RequestParam Map<String,String> params) {
        if (Objects.isNull(params.get("invoice_id"))) {
            return new ResponseEntity<>("No invoice id_presented", HttpStatus.BAD_REQUEST);
        }
        final int invoiceId = Integer.parseInt(params.get("invoice_id"));
        logger.info("Received BTC on Blockchain Wallet. Invoice id #"+invoiceId+
            ".Request Body:"+params);
        final PendingPayment pendingPayment = blockchainService.findByInvoiceId(invoiceId);
        logger.debug("Corresponding pending Blockchain payment (Invoice id #"
            +invoiceId+") from database :"+pendingPayment);
        final ResponseEntity<String> response = blockchainService
            .notCorresponds(params, pendingPayment)
            .map(error -> new ResponseEntity<>(error, BAD_REQUEST))
            .orElseGet(() ->
                new ResponseEntity<>(
                    blockchainService
                        .approveBlockchainTransaction(pendingPayment, params), OK)
            );
        logger.info("Response to https://blockchain.info/ : "+response);
        return response;
    }

    @RequestMapping(value = "/payment/provide",method = POST)
    public RedirectView outputPayment(Payment payment, Principal principal, RedirectAttributes redir) {
//        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
//        if (!creditsOperation.isPresent()) {
//            redir.addFlashAttribute("error", "merchants.invalidSum");
//            return new RedirectView("/merchants/output");
//        }
//        blockchainService.provideOutputPayment(payment, creditsOperation.get());
//        merchantService.formatResponseMessage(creditsOperation.get())
//                .entrySet()
//                .forEach(entry->redir.addFlashAttribute(entry.getKey(),entry.getValue()));
//        final String message = "merchants.successfulBalanceWithdraw";
//        redir.addFlashAttribute("message",message);
//        return new RedirectView("/mywallets");
        throw new UnsupportedOperationException();
    }
}
