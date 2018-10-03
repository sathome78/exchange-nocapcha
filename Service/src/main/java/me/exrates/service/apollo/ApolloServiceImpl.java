package me.exrates.service.apollo;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.RefillRequest;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.CryptoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;


@Log4j2
@PropertySource("classpath:/merchants/apollo.properties")
@Service
public class ApolloServiceImpl implements ApolloService {

    private @Value("${apollo.url}")String SEVER_URL;
    private @Value("${apollo.main_address}")String MAIN_ADDRESS;
    private static final String APOLLO_MERCHANT_CURRENCY = "APL";
    private Merchant merchant;
    private Currency currency;
    /*todo*/
    private static final String DESTINATION_TAG_ERR_MSG = "";

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RefillService refillService;

    @PostConstruct
    public void init() {
        currency = currencyService.findByName(APOLLO_MERCHANT_CURRENCY);
        merchant = merchantService.findByName(APOLLO_MERCHANT_CURRENCY);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String destinationTag = generateUniqDestinationTag(request.getUserId());
        String message = messageSource.getMessage("merchants.refill.xlm",
                new Object[]{MAIN_ADDRESS, destinationTag}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address",  destinationTag);
            put("message", message);
            put("qr", MAIN_ADDRESS);
        }};
    }

    private String generateUniqDestinationTag(int userId) {
        Optional<Integer> id;
        String destinationTag;
        do {
            destinationTag = CryptoUtils.generateDestinationTag(userId, 9);
            id = refillService.getRequestIdReadyForAutoAcceptByAddressAndMerchantIdAndCurrencyId(destinationTag, currency.getId(), merchant.getId());
        } while (id.isPresent());
        log.debug("tag is {}", destinationTag);
        return destinationTag;
    }


    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        BigDecimal amount = new BigDecimal(params.get("amount"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        try {
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.debug("RefillRequestNotFountException: " + params);
            Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        }
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("not implemented");
    }
}
