package me.exrates.service.ripple;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestIdNeededException;
import me.exrates.service.exception.WithdrawRequestPostException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * Created by maks on 11.05.2017.
 */
@Log4j2
@Service
@PropertySource("classpath:/merchants/ripple.properties")
public class RippleServiceImpl implements RippleService {

    private @Value("${ripple.account.address}") String systemAddress;

    @Autowired
    private RippleTransactionService rippleTransactionService;
    @Autowired
    private RippledNodeService rippledNodeService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RefillService refillService;

    private static final int MAX_TAG_DESTINATION_DIGITS = 10;


    /*method for admin manual check transaction by hash*/
    @Override
    public void manualCheckTransaction(String hash) {
        JSONObject response = rippledNodeService.getTransaction(hash);
        onTransactionReceive(response);
    }


    protected void onTransactionReceive(JSONObject result) {
        log.debug("income transaction {} ", result.toString());
        boolean validated = result.getBoolean("validated");
        Map<String, String> paramsMap = new HashMap<>();
        if (validated) {
            JSONObject transaction = result.getJSONObject("transaction");
            paramsMap.put("hash", transaction.getString("hash"));
            Integer destinationTag = transaction.getInt("DestinationTag");
            paramsMap.put("address", String.valueOf(destinationTag));
            paramsMap.put("amount", transaction.getString("Amount"));
        }
        try {
            this.processPayment(paramsMap);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error("xrp refill address not found {}", result.toString());
        }
    }

    @Override
    public String withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        if (!"XRP".equalsIgnoreCase(withdrawMerchantOperationDto.getCurrency())) {
            throw new WithdrawRequestPostException("Currency not supported by merchant");
        }
        return rippleTransactionService.withdraw(withdrawMerchantOperationDto);
    }

    /*generate max-10digits(Unsigned Integer) for identifying payment */
    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) throws RefillRequestIdNeededException {
        Integer destinationTag = generateUniqDestinationTag(request.getUserId());
        String message = messageSource.getMessage("merchants.refill.edr",
                new Object[]{systemAddress}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address", destinationTag.toString());
            put("message", message);
        }};
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        Currency currency = currencyService.findByName("XRP");
        Merchant merchant = merchantService.findByName("XRP");
        BigDecimal amount = rippleTransactionService.normalizeAmountToDecimal(params.get("amount"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .build();
        if(merchant.getToMainAccountTransferringConfirmNeeded()) {
            requestAcceptDto.setToMainAccountTransferringConfirmNeeded(true);
        }
        try {
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.debug("RefillRequestNotFountException: " + params);
            Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        }
    }

    private Integer generateUniqDestinationTag(int userId) {
        Currency currency = currencyService.findByName("XRP");
        Merchant merchant = merchantService.findByName("XRP");
        Optional<Integer> id = null;
        int destinationTag;
        do {
            destinationTag = generateDestinationTag(userId);
            id = refillService.getRequestIdReadyForAutoAcceptByAddressAndMerchantIdAndCurrencyId(String.valueOf(destinationTag),
                    currency.getId(), merchant.getId());
        } while (!id.isPresent());
        return destinationTag;
    }

    private Integer generateDestinationTag(int userId) {
        String idInString = String.valueOf(userId);
        int randomNumberLength = MAX_TAG_DESTINATION_DIGITS - idInString.length();
        if (randomNumberLength < 0 ) {
            throw new MerchantInternalException("error generating new destination tag for ripple" + userId);
        }
        String randomIntInstring = String.valueOf(100000000 + new Random().nextInt(100000000));
        return Integer.valueOf(idInString.concat(randomIntInstring.substring(0, randomNumberLength)));
    }

}
