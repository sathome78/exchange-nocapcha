package me.exrates.service.decred;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.decred.rpc.Api;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Log4j2(topic = "decred")
@Service
public class DecredServiceImpl implements DecredService {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private DecredGrpcService decredGrpcService;


    private Merchant merchant;
    private Currency currency;

    private static final String MERCHANT_name = "DCR";
    private static final int MAX_DIGITS = 12;

    @PostConstruct
    public void init() {
        currency = currencyService.findByName("DCR");
        merchant = merchantService.findByName(MERCHANT_name);
    }


    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        Api.NextAddressResponse response = decredGrpcService.getNewAddress();
        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{response.getAddress()}, request.getLocale());
        return new HashMap<String, String>() {
            {
                put("address", response.getAddress());
                put("pubKey", response.getPublicKey());
                put("message", message);
                put("qr", response.getAddress());
            }
        };
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("Not implemented ");
    }


}
