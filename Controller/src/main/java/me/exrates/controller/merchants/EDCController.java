package me.exrates.controller.merchants;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.Payment;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.service.*;
import me.exrates.service.exception.InvalidAmountException;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/edc")
public class EDCController {

    @Autowired
    private EDCService edcService;

    @Autowired
    private RefillService refillService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private MessageSource messageSource;

    private final Logger LOG = LogManager.getLogger("merchant");

    @RequestMapping(value = "payment/received",method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(@RequestBody Map<String,String> params, RedirectAttributes redir) {
        LOG.info("Response: " + params);
        edcService.checkTransactionByHistory(params);
        String merchantTransactionId = params.get("id");
        String address = params.get("address");
        Currency currency = currencyService.findByName("EDR");
        Merchant merchant = merchantService.findByNName("EDC");
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
            .address(address)
            .merchantId(merchant.getId())
            .currencyId(currency.getId())
            .amount(BigDecimal.valueOf(Double.parseDouble(params.get("amount"))))
            .merchantTransactionId(merchantTransactionId)
            .build();
        refillService.autoAcceptRefillRequest(requestAcceptDto);
        return new ResponseEntity<>(OK);
    }
}
