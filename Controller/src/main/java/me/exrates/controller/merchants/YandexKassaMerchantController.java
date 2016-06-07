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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.HashMap;
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

    private static final Logger LOG = LogManager.getLogger("merchant");

    @RequestMapping(value = "payment/prepare",method = RequestMethod.POST)
    public ResponseEntity<Map<String,String>> preparePayment(@RequestBody String body, Principal principal) {
        LOG.debug("Begin method: preparePayment.");

        final Payment payment = new Gson().fromJson(body, Payment.class);
        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperation.isPresent()) {
            final Map<String, String> errors = new HashMap<String, String>() {
                {
                    put("error", "merchants.invalidSum");
                }
            };
            return new ResponseEntity<>(errors, HttpStatus.NO_CONTENT);
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
    public RedirectView successPayment(@RequestParam final Map<String,String> response, final RedirectAttributes redir) {

        LOG.debug("Begin method: successPayment.");
        Transaction transaction = transactionService.findById(Integer.parseInt(response.get("orderNumber")));

        if (transaction.isProvided()){
            merchantService.formatResponseMessage(transaction)
                    .entrySet()
                    .forEach(entry->redir.addFlashAttribute(entry.getKey(),entry.getValue()));
            final String message = "merchants.successfulBalanceDeposit";
            redir.addFlashAttribute("message", message);

            return new RedirectView("/mywallets");

        }

        final String message = "merchants.internalError";
        redir.addFlashAttribute("message", message);

        return new RedirectView("/mywallets");
    }

    }
