package me.exrates.controller.merchants;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.enums.OperationType;
import me.exrates.service.MerchantService;
import me.exrates.service.TransactionService;
import me.exrates.service.YandexKassaService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/merchants/yandex_kassa")
public class YandexKassaMerchantController {
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private YandexKassaService yandexKassaService;

    @Autowired
    private TransactionService transactionService;

    private static final Logger logger = LogManager.getLogger(YandexKassaMerchantController.class);

    @RequestMapping(value = "/payment/prepare", method = RequestMethod.POST)
    public RedirectView preparePayment(@Valid @ModelAttribute("payment") Payment payment,
                                       BindingResult result, Principal principal, RedirectAttributes redir) {

        final String errorRedirectView = "/merchants/".concat(payment.getOperationType() == OperationType.INPUT ?
                "/input": "/output");

        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());

        if (!creditsOperation.isPresent()) {
            redir.addFlashAttribute("error", "merchants.invalidSum");
            return new RedirectView(errorRedirectView);
        }

        return yandexKassaService.preparePayment(creditsOperation.get(), principal.getName());

    }

    }
