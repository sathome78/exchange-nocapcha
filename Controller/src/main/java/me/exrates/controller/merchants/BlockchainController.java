package me.exrates.controller.merchants;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.PendingPayment;
import me.exrates.service.BlockchainService;
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
import java.util.*;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

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
    private MessageSource messageSource;

    private static final Logger LOG = LogManager.getLogger("merchant");

    @RequestMapping(value = "/payment/prepare",method = POST)
    public ResponseEntity<String> preparePayment(final @RequestBody Payment payment,
                                                 final Principal principal,
                                                 final Locale locale)
    {
        if (!merchantService.checkInputRequestsLimit(payment.getCurrency(), principal.getName())){
            final Map<String,String> error = new HashMap<>();
            error.put("error", messageSource.getMessage("merchants.InputRequestsLimit", null, locale));

            return new ResponseEntity(error, HttpStatus.FORBIDDEN);
        }
        final String email = principal.getName();
        LOG.debug("Preparing payment: " + payment + " for: " + email);
        final CreditsOperation creditsOperation = merchantService
            .prepareCreditsOperation(payment, email)
            .orElseThrow(InvalidAmountException::new);
            LOG.debug("Prepared payment: "+creditsOperation);
            try {
                final PendingPayment pendingPayment = blockchainService
                    .createPaymentInvoice(creditsOperation);

                final String notification = merchantService
                    .sendDepositNotification(Optional.ofNullable(pendingPayment
                            .getAddress()).orElseThrow(
                            () ->new MerchantInternalException("Address not presented"))
                        ,email ,locale, creditsOperation, "merchants.depositNotification.body");
                LOG.info("New pending Blockchain payment :"+ pendingPayment);
                final HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Content-Type", "text/plain; charset=utf-8");
                return new ResponseEntity<>(notification, httpHeaders, OK);
            } catch (final InvalidAmountException|RejectedPaymentInvoice e) {
                final String error = messageSource.getMessage("merchants.incorrectPaymentDetails", null, locale);
                LOG.warn(error);
                return new ResponseEntity<>(error, NOT_FOUND);
            }
    }

    @RequestMapping(value = "/payment/received", method = GET)
    public ResponseEntity<String> paymentHandler(final @RequestParam Map<String,String> params) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "text/plain; charset=utf-8");
        if (Objects.isNull(params.get("invoice_id"))) {
            return new ResponseEntity<>("No invoice id_presented", httpHeaders, HttpStatus.BAD_REQUEST);
        }
        final int invoiceId = Integer.parseInt(params.get("invoice_id"));
        LOG.info("Received BTC on Blockchain Wallet. Invoice id #" + invoiceId + ".Request Body:" + params);
        final PendingPayment pendingPayment = blockchainService.findByInvoiceId(invoiceId);
        LOG.debug("Corresponding pending Blockchain payment (Invoice id #" +
                invoiceId + ") from database :" + pendingPayment);
        final ResponseEntity<String> response = blockchainService
            .notCorresponds(params, pendingPayment)
            .map(error -> new ResponseEntity<>(error, BAD_REQUEST))
            .orElseGet(() ->
                new ResponseEntity<>(
                    blockchainService
                        .approveBlockchainTransaction(pendingPayment, params), httpHeaders, OK)
            );
        LOG.info("Response to https://blockchain.info/ : "+response);
        return response;
    }
}
