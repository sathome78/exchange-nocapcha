package me.exrates.controller.merchants;

import com.google.gson.Gson;
import java.security.Principal;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import me.exrates.model.BlockchainPayment;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Email;
import me.exrates.model.Payment;
import me.exrates.service.EDRCService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.OK;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/edrcoin")
@PropertySource(value = "classpath:/${spring.profile.active}/mail.properties")
public class EDRCoinController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private EDRCService edrcService;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private SendMailService sendMailService;

    private @Value("${mail.user}") String mailUser;

    private static final Logger logger = LogManager.getLogger(EDRCoinController.class.getName());

    @RequestMapping(value = "/payment/prepare",method = RequestMethod.POST)
    public ResponseEntity<String> preparePayment(@RequestBody String body, Principal principal, Locale locale) {
        final Payment payment = new Gson().fromJson(body, Payment.class);
        final String email = principal.getName();
        logger.debug("Preparing payment: "+payment+" for: " + email);
        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, email);
        logger.debug("Prepared payment: "+creditsOperation);
        try {
            final BlockchainPayment blockchainPayment = creditsOperation
                .map(edrcService::createPaymentInvoice)
                .orElseThrow(InvalidAmountException::new);
            final String sumWithCurrency = blockchainPayment.getAmount().stripTrailingZeros() + " EDRC";
            final String notification = String.format(context
                .getMessage("merchants.makePay", null, locale), sumWithCurrency, blockchainPayment.getAddress()) +
                "<br>The transfer of money will will occur within next 48 hours"+
                "<br>Зачисление средств приозойдет в течении 48 часов";
            final Email mail = new Email();
            mail.setFrom(mailUser);
            mail.setTo(principal.getName());
            mail.setSubject("Exrates EDRC Payment Invoice");
            mail.setMessage(sumWithCurrency);
            try {
                sendMailService.sendMail(mail);
            } catch (MailException e) {
                logger.error(e);
            }
            logger.info("New pending EDRCoin payment :"+blockchainPayment);
            logger.info(notification);
            final HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "text/plain; charset=utf-8");
            return new ResponseEntity<>(notification, httpHeaders, OK);
        } catch (InvalidAmountException|RejectedPaymentInvoice e) {
            final String error = context.getMessage("merchants.incorrectPaymentDetails", null, locale);
            return new ResponseEntity<>(error,HttpStatus.NO_CONTENT);
        }
    }

    //// TODO: 3/26/16 Currentrly not using
    @RequestMapping(value = "/payment/provide",method = RequestMethod.POST)
    public RedirectView provideOutputPayment(final Payment payment,final Principal principal, final RedirectAttributes redir) {
        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        edrcService.provideOutputPayment(payment, creditsOperation.get());
        return null;
    }

    //// TODO: 3/30/16 Will be implemented
    @RequestMapping("/payment/received")
    public void paymentHandler(@RequestBody Map<String,String> response) {
        logger.info(response);
    }
}
