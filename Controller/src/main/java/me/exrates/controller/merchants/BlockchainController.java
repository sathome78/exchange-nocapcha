package me.exrates.controller.merchants;

import com.google.gson.Gson;
import me.exrates.model.*;
import me.exrates.service.BlockchainService;
import me.exrates.service.MerchantService;
import me.exrates.service.SendMailService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.RejectedPaymentInvoice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/blockchain")
@PropertySource(value = "classpath:/${spring.profile.active}/mail.properties")
public class BlockchainController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private BlockchainService blockchainService;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private ApplicationContext context;

    private @Value("${mail.user}") String mailUser;

    private static final BigDecimal SATOSHI = BigDecimal.valueOf(100000000L);

    private static final int GRACEFUL_CONFIRMATIONS_NUMBER = 4;

    private static final Logger logger = LogManager.getLogger(BlockchainController.class);

    //// TODO: Provide mail subject & description
    @RequestMapping(value = "/payment/prepare",method = RequestMethod.POST)
    public ResponseEntity<String> preparePayment(@RequestBody String body, Principal principal, Locale locale) {
        final Payment payment = new Gson().fromJson(body, Payment.class);
        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        try {
            final BlockchainPayment blockchainPayment = creditsOperation
                    .flatMap(blockchainService::createPaymentInvoice)
                    .orElseThrow(InvalidAmountException::new);
            final String sumWithCurrency = blockchainPayment.getAmount() + "BTC";
            final String success = String.format(context
                    .getMessage("merchants.makePay", null, locale), sumWithCurrency,blockchainPayment.getAddress());
            final Email email = new Email();
            email.setFrom(mailUser);
            email.setTo(principal.getName());
            email.setSubject("Exrates BTC Payment Invoice");
            email.setMessage(sumWithCurrency);
            sendMailService.sendMail(email);
            logger.info(blockchainPayment.toString());
            return new ResponseEntity<>(success, HttpStatus.OK);
        } catch (InvalidAmountException|RejectedPaymentInvoice e) {
            final String error = context.getMessage("merchants.incorrectPaymentDetails", null, locale);
            return new ResponseEntity<>(error,HttpStatus.NO_CONTENT);
        }
    }

    @RequestMapping(value = "/payment/received/{invoiceId}")
    public ResponseEntity<String> paymentHandler(@PathVariable("invoiceId") int invoiceId,
                                                 @RequestParam Map<String,String> params) {
        final BlockchainPayment pendingPayment = blockchainService.findByInvoiceId(invoiceId);
        BigDecimal amount = new BigDecimal(params.get(params.get("value"))).divide(SATOSHI);
        if (!params.get("address").equals(pendingPayment.getAddress())) {
            return new ResponseEntity<>("Address is not correct", HttpStatus.BAD_REQUEST);
        }
        if (!params.get("secret").equals(pendingPayment.getSecret())) {
            return new ResponseEntity<>("Secret is invalid", HttpStatus.BAD_REQUEST);
        }
        if (!amount.equals(pendingPayment.getAmount())) {
            return new ResponseEntity<>("Amount is invalid", HttpStatus.BAD_REQUEST);
        }
        int confirmations = Integer.parseInt(params.get("confirmations"));
        if (confirmations>=GRACEFUL_CONFIRMATIONS_NUMBER) {
            BTCTransaction btcTransaction = new BTCTransaction();
            btcTransaction.setAmount(amount);
            btcTransaction.setTransactionId(pendingPayment.getInvoiceId());
            blockchainService.persistBlockchainTransaction(pendingPayment, btcTransaction);
            return new ResponseEntity<>("*ok*", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Waiting for confirmations", HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/payment/provide",method = RequestMethod.POST)
    public RedirectView outputPayment(Payment payment, Principal principal, RedirectAttributes redir) {
        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperation.isPresent()) {
            redir.addFlashAttribute("error", "merchants.invalidSum");
            return new RedirectView("/merchants/output");
        }
        blockchainService.provideOutputPayment(payment, creditsOperation.get());
        merchantService.formatResponseMessage(creditsOperation.get())
                .entrySet()
                .forEach(entry->redir.addFlashAttribute(entry.getKey(),entry.getValue()));
        final String message = "merchants.successfulBalanceWithdraw";
        redir.addFlashAttribute("message",message);
        return new RedirectView("/mywallets");
    }
}