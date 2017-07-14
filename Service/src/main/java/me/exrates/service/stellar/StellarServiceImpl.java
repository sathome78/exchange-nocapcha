package me.exrates.service.stellar;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.exception.DuplicatedMerchantTransactionIdOrAttemptToRewriteException;
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
import me.exrates.service.exception.WithdrawRequestPostException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.stellar.sdk.Memo;
import org.stellar.sdk.MemoId;
import org.stellar.sdk.responses.TransactionResponse;


import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by maks on 06.06.2017.
 */
@Log4j2
@Service
@PropertySource("classpath:/merchants/stellar.properties")
public class StellarServiceImpl implements StellarService {

    private @Value("${stellar.horizon.url}")String SEVER_URL;
    private @Value("${stellar.account.name}")String ACCOUNT_NAME;
    private @Value("${stellar.account.seed}")String ACCOUNT_SECRET;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RefillService refillService;
    @Autowired
    private StellarTransactionService stellarTransactionService;

    private Merchant merchant;
    private Currency currency;

    @PostConstruct
    public void init() {
        currency = currencyService.findByName("XLM");
        merchant = merchantService.findByName(XLM_MERCHANT);
    }


    private static final String XLM_MERCHANT = "Stellar";

    private static final int MAX_TAG_DESTINATION_DIGITS = 9;

    @Override
    public void manualCheckNotReceivedTransaction(String hash) {

    }

    @Override
    public boolean checkSendedTransaction(String hash, String additionalParams) {
        return false;
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        log.error("withdraw_XLM");
        if (!"XLM".equalsIgnoreCase(withdrawMerchantOperationDto.getCurrency())) {
            throw new WithdrawRequestPostException("Currency not supported by merchant");
        }
        return stellarTransactionService.withdraw(withdrawMerchantOperationDto, SEVER_URL, ACCOUNT_SECRET);
    }

    @Override
    public void onTransactionReceive(TransactionResponse payment, String amount) {
        log.debug("income transaction {} ", payment.getMemo() + " " + amount);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("hash", payment.getHash());
        MemoId memoid = (MemoId)payment.getMemo();
        Long destinationTag = memoid.getId();
        paramsMap.put("address", destinationTag.toString());
        paramsMap.put("amount", amount);
        if (checkTransactionForDuplicate(payment, destinationTag.toString())) {
            try {
                throw new DuplicatedMerchantTransactionIdOrAttemptToRewriteException(payment.getHash());
            } catch (DuplicatedMerchantTransactionIdOrAttemptToRewriteException e) {
                return;
            }
        }
        try {
            this.processPayment(paramsMap);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error("xlm refill address not found {}", payment);
        }
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        Integer destinationTag = generateUniqDestinationTag(request.getUserId());
        String message = messageSource.getMessage("merchants.refill.xlm",
                new Object[]{ACCOUNT_NAME, destinationTag}, request.getLocale());
        DecimalFormat myFormatter = new DecimalFormat("###.##");
        return new HashMap<String, String>() {{
            put("address",  myFormatter.format(destinationTag));
            put("message", message);
        }};
    }

    private boolean checkTransactionForDuplicate(TransactionResponse payment, String adress) {
        return refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(
                adress, merchant.getId(), currency.getId(), payment.getHash()).isPresent();
    }

    private Integer generateUniqDestinationTag(int userId) {
        Optional<Integer> id = null;
        int destinationTag;
        do {
            destinationTag = generateDestinationTag(userId);
            id = refillService.getRequestIdReadyForAutoAcceptByAddressAndMerchantIdAndCurrencyId(String.valueOf(destinationTag),
                    currency.getId(), merchant.getId());
        } while (id.isPresent());
        log.debug("tag is {}", destinationTag);
        return destinationTag;
    }

    private Integer generateDestinationTag(int userId) {
        String idInString = String.valueOf(userId);
        int randomNumberLength = MAX_TAG_DESTINATION_DIGITS - idInString.length();
        if (randomNumberLength < 0) {
            throw new MerchantInternalException("error generating new destination tag for stellar" + userId);
        }
        String randomIntInstring = String.valueOf(100000000 + new Random().nextInt(100000000));
        return Integer.valueOf(idInString.concat(randomIntInstring.substring(0, randomNumberLength)));
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        Currency currency = currencyService.findByName("XLM");
        Merchant merchant = merchantService.findByName(XLM_MERCHANT);
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
    public String getMainAddress() {
        return ACCOUNT_NAME;
    }
  //TODO remove after changes in mobile api
    @Override
    public String getPaymentMessage(String additionalTag, Locale locale) {
        return messageSource.getMessage("merchants.refill.xlm",
                new Object[]{ACCOUNT_NAME, additionalTag}, locale);
    }
}
