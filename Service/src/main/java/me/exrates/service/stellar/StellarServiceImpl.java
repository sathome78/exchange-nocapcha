package me.exrates.service.stellar;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.SubmitTransactionResponse;
import org.stellar.sdk.responses.TransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private @Value("${stellar.account.secret}")String ACCOUNT_SECRET;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RefillService refillService;

    private static final Integer XRP_AMOUNT_MULTIPLIER = 1000000;
    private static final Integer XRP_DECIMALS = 6;
    private static final BigDecimal XRP_MIN_BALANCE = new BigDecimal(20);

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
        return null;
    }

    @Override
    public void onTransactionReceive(TransactionResponse payment, String amount) {
        log.debug("income transaction {} ", payment);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("hash", payment.getHash());
        Integer destinationTag = Integer.parseInt(payment.getMemo().toString());
        paramsMap.put("address", String.valueOf(destinationTag));
        paramsMap.put("amount", amount);
        try {
            this.processPayment(paramsMap);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error("xlm refill address not found {}", payment);
        }
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        Integer destinationTag = generateUniqDestinationTag(request.getUserId());
        String message = messageSource.getMessage("merchants.refill.xrp",
                new Object[]{ACCOUNT_NAME, destinationTag}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address", destinationTag.toString());
            put("message", message);
        }};
    }

    private Integer generateUniqDestinationTag(int userId) {
        Currency currency = currencyService.findByName("XRP");
        Merchant merchant = merchantService.findByName(XLM_MERCHANT);
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
            throw new MerchantInternalException("error generating new destination tag for ripple" + userId);
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
        BigDecimal amount = this.normalizeAmountToDecimal(params.get("amount"));
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

    private void constructTransaction(String destinationAccountId, long memo, BigDecimal amount)  {

        Network.useTestNetwork();
        Server server = new Server(SEVER_URL);

        KeyPair source = KeyPair.fromSecretSeed(ACCOUNT_SECRET);
        KeyPair destination = KeyPair.fromAccountId(destinationAccountId);

// First, check to make sure that the destination account exists.
// You could skip this, but if the account does not exist, you will be charged
// the transaction fee when the transaction fails.
// It will throw HttpResponseException if account does not exist or there was another error.
        try {
            server.accounts().account(destination);
        } catch (IOException e) {
            /*todo: here throw account not found exception*/
            return;
        }

// If there was no error, load up-to-date information on your account.
        AccountResponse sourceAccount = null;
        try {
            sourceAccount = server.accounts().account(source);
            AccountResponse.Balance[] balances =  sourceAccount.getBalances();

        } catch (IOException e) {
            /*todo: our account not found exception*/
            return;
        }

// Start building the transaction.
        Transaction transaction = new Transaction.Builder(sourceAccount)
                .addOperation(new PaymentOperation.Builder(destination, new AssetTypeNative(), normalizeAmountToString(amount)).build())
                // A memo allows you to add your own metadata to a transaction. It's
                // optional and does not affect how Stellar treats the transaction.
                .addMemo(Memo.id(memo))
                .build();
// Sign the transaction to prove you are actually the person sending it.
        transaction.sign(source);
// And finally, send it off to Stellar!
        try {
            SubmitTransactionResponse response = server.submitTransaction(transaction);
            if (response.isSuccess()) {
                String hash = Arrays.toString(transaction.hash());
            }
        } catch (Exception e) {
            log.error("error sending transaction {}", e.getMessage());
        }
    }


    private String normalizeAmountToString(BigDecimal amount) {
        return amount
                .setScale(XRP_DECIMALS, RoundingMode.HALF_DOWN)
                .multiply(new BigDecimal(XRP_AMOUNT_MULTIPLIER))
                .toBigInteger()
                .toString();
    }

    private BigDecimal normalizeAmountToDecimal(String amount) {
        return new BigDecimal(amount)
                .divide(new BigDecimal(XRP_AMOUNT_MULTIPLIER))
                .setScale(XRP_DECIMALS, RoundingMode.HALF_DOWN);
    }
}
