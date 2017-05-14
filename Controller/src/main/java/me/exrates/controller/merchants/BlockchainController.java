package me.exrates.controller.merchants;

import me.exrates.service.BlockchainService;
import me.exrates.service.MerchantService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

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

    @RequestMapping(value = "/payment/received", method = GET)
    public ResponseEntity<String> paymentHandler(final @RequestParam Map<String,String> params) {
        /*final HttpHeaders httpHeaders = new HttpHeaders();
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
        return response;*/
        return new ResponseEntity<>(BAD_REQUEST);
    }
}
