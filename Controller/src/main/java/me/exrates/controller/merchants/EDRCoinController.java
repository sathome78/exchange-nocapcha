package me.exrates.controller.merchants;

import com.google.gson.Gson;
import java.security.Principal;
import java.util.Locale;
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
import org.springframework.http.HttpStatus;
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

    private static final Logger logger = LogManager.getLogger(EDRCoinController.class);

    @RequestMapping(value = "/payment/prepare",method = RequestMethod.POST)
    public ResponseEntity<String> preparePayment(@RequestBody String body, Principal principal, Locale locale) {
        final Payment payment = new Gson().fromJson(body, Payment.class);
        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        try {
            final BlockchainPayment blockchainPayment = creditsOperation
                .map(edrcService::createPaymentInvoice)
                .orElseThrow(InvalidAmountException::new);
            final String sumWithCurrency = blockchainPayment.getAmount() + "EDRC";
            final String success = String.format(context
                .getMessage("merchants.makePay", null, locale), sumWithCurrency,blockchainPayment.getAddress());
            final Email email = new Email();
            email.setFrom(mailUser);
            email.setTo(principal.getName());
            email.setSubject("Exrates EDRC Payment Invoice");
            email.setMessage(sumWithCurrency);
            try {
                sendMailService.sendMail(email);
            } catch (MailException e) {
                logger.error(e);
            }
            logger.info(blockchainPayment.toString());
            System.out.println(success);
            return new ResponseEntity<>(success, HttpStatus.OK);
        } catch (InvalidAmountException|RejectedPaymentInvoice e) {
            final String error = context.getMessage("merchants.incorrectPaymentDetails", null, locale);
            return new ResponseEntity<>(error,HttpStatus.NO_CONTENT);
        }
    }

    @RequestMapping(value = "/payment/provide",method = RequestMethod.POST)
    public RedirectView provideOutputPayment(final Payment payment,final Principal principal, final RedirectAttributes redir) {
        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        edrcService.provideOutputPayment(payment, creditsOperation.get());
        System.out.println("Money sended");
        return null;
    }
}