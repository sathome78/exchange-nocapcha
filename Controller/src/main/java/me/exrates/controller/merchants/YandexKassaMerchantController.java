package me.exrates.controller.merchants;

import com.google.gson.Gson;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.service.MerchantService;
import me.exrates.service.TransactionService;
import me.exrates.service.YandexKassaService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/merchants/yandex_kassa")
public class YandexKassaMerchantController {
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private YandexKassaService yandexKassaService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    private static final Logger LOG = LogManager.getLogger("merchant");

    @RequestMapping(value = "payment/prepare",method = RequestMethod.POST)
    public ResponseEntity<Map<String,String>> preparePayment(@RequestBody String body, Principal principal, final Locale locale) {
        LOG.debug("Begin method: preparePayment.");

        final Payment payment = new Gson().fromJson(body, Payment.class);
        if (!merchantService.checkInputRequestsLimit(payment.getMerchant(), principal.getName())){
            final Map<String,String> error = new HashMap<>();
            error.put("error", messageSource.getMessage("merchants.InputRequestsLimit", null, locale));

            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }

        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperation.isPresent()) {
            final Map<String, String> errors = new HashMap<String, String>() {
                {
                    put("error", messageSource.getMessage("merchants.incorrectPaymentDetails", null, locale));
                }
            };
            return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
        }

        final Map<String, String> params = yandexKassaService.preparePayment(creditsOperation.get(), principal.getName());;

        return new ResponseEntity<>(params,HttpStatus.OK);
    }

    @RequestMapping(value = "payment/status",method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(final @RequestParam Map<String,String> params) {

        LOG.debug("Begin method: statusPayment.");
        final ResponseEntity<Void> response = new ResponseEntity<>(OK);

        if (yandexKassaService.confirmPayment(params)) {
            return response;
        }

        return new ResponseEntity<>(BAD_REQUEST);
    }

    @RequestMapping(value = "payment/success",method = RequestMethod.GET)
    public RedirectView successPayment(@RequestParam final Map<String,String> response, final RedirectAttributes redir, final HttpServletRequest request) {

        LOG.debug("Begin method: successPayment.");
        Transaction transaction = transactionService.findById(Integer.parseInt(response.get("orderNumber")));

        if (transaction.isProvided()){

            redir.addAttribute("successNoty", messageSource.getMessage("merchants.successfulBalanceDeposit",
                    merchantService.formatResponseMessage(transaction).values().toArray(), localeResolver.resolveLocale(request)));

            return new RedirectView("/dashboard");

        }

        redir.addAttribute("errorNoty", messageSource.getMessage("merchants.internalError", null, localeResolver.resolveLocale(request)));

        return new RedirectView("/dashboard");
    }

    }
