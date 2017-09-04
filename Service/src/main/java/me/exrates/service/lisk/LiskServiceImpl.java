package me.exrates.service.lisk;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.lisk.LiskAccount;
import me.exrates.model.dto.merchants.lisk.LiskSendTxDto;
import me.exrates.model.dto.merchants.lisk.LiskTransaction;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.LiskCreateAddressException;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.ParamMapUtils;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;

@Log4j2
@Service
@PropertySource("classpath:/merchants/lisk.properties")
public class LiskServiceImpl implements LiskService {

    @Autowired
    private RefillService refillService;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private LiskRestClient liskRestClient;

    @Autowired
    private MessageSource messageSource;

    private final String merchantName = "Lisk";
    private final String currencyName = "LSK";
    private @Value("${lisk.main.address}") String mainAddress;
    private @Value("${lisk.main.secret}") String mainSecret;

    private @Value("${lisk.min.confirmations}") Integer minConfirmations;


    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        try {
            String secret = String.join(" ", MnemonicCode.INSTANCE.toMnemonic(SecureRandom.getSeed(16)));
            LiskAccount account = createNewLiskAccount(secret);
            String address = account.getAddress();

            String message = messageSource.getMessage("merchants.refill.btc",
                    new Object[]{address}, request.getLocale());
            Map<String, String> result = new HashMap<String, String>() {{
               put("message", message);
               put("address", address);
               put("pubKey", account.getPublicKey());
               put("brainPrivKey", secret);
               put("qr", address);
            }};
            return result;
        } catch (MnemonicException.MnemonicLengthException e) {
            throw new LiskCreateAddressException(e);
        }



    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        Optional<String> refillRequestIdResult = Optional.ofNullable(params.get("requestId"));
        Integer currencyId = Integer.parseInt(ParamMapUtils.getIfNotNull(params, "currencyId"));
        Integer merchantId = Integer.parseInt(ParamMapUtils.getIfNotNull(params, "merchantId"));
        String address = ParamMapUtils.getIfNotNull(params, "address");
        String txId = ParamMapUtils.getIfNotNull(params, "txId");
        LiskTransaction transaction = getTransactionById(txId);

        if (!refillRequestIdResult.isPresent()) {
            Integer requestId = refillService.createRefillRequestByFact(RefillRequestAcceptDto.builder()
                    .address(address)
                    .amount(transaction.getScaledAmount())
                    .merchantId(merchantId)
                    .currencyId(currencyId)
                    .merchantTransactionId(txId).build());
            if (transaction.getConfirmations() >= 0 && transaction.getConfirmations() < minConfirmations) {
                try {
                    log.debug("put on bch exam {}", transaction);
                    refillService.putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto.builder()
                            .requestId(requestId)
                            .merchantId(merchantId)
                            .currencyId(currencyId)
                            .address(address)
                            .amount(transaction.getScaledAmount())
                            .hash(txId)
                            .blockhash(transaction.getBlockId()).build());
                } catch (RefillRequestAppropriateNotFoundException e) {
                    log.error(e);
                }
            } else {
                changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto.builder()
                        .requestId(requestId)
                        .address(address)
                        .amount(transaction.getScaledAmount())
                        .confirmations(transaction.getConfirmations())
                        .currencyId(currencyId)
                        .merchantId(merchantId)
                        .hash(txId)
                        .blockhash(transaction.getBlockId()).build());
            }
        } else {
            Integer requestId = Integer.parseInt(refillRequestIdResult.get());
            changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto.builder()
                    .requestId(requestId)
                    .address(address)
                    .amount(transaction.getScaledAmount())
                    .confirmations(transaction.getConfirmations())
                    .currencyId(currencyId)
                    .merchantId(merchantId)
                    .hash(txId)
                    .blockhash(transaction.getBlockId()).build());
        }

    }

    private void changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto dto) {
        try {
            refillService.setConfirmationCollectedNumber(dto);
            if (dto.getConfirmations() >= minConfirmations) {
                log.debug("Providing transaction!");
                RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                        .requestId(dto.getRequestId())
                        .address(dto.getAddress())
                        .amount(dto.getAmount())
                        .currencyId(dto.getCurrencyId())
                        .merchantId(dto.getMerchantId())
                        .merchantTransactionId(dto.getHash())
                        .build();
                refillService.autoAcceptRefillRequest(requestAcceptDto);
                RefillRequestFlatDto flatDto = refillService.getFlatById(dto.getRequestId());
                sendTransaction(flatDto.getBrainPrivKey(), dto.getAmount(), mainAddress);

            }
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }

    }


    @Override
    @Scheduled(initialDelay = 1000, fixedDelay = 10 * 60 * 1000)
    public void processTransactionsForKnownAddresses() {
        log.debug("Start checking Lisk transactions");
        Currency currency = currencyService.findByName(currencyName);
        Merchant merchant = merchantService.findByName(merchantName);
        refillService.findAllAddresses(merchant.getId(), currency.getId()).forEach(address -> {
            int offset = refillService.getTxOffsetForAddress(address);
            List<LiskTransaction> userTransactions = liskRestClient.getAllTransactionsByRecipient(address, offset);
            log.debug("Address {}, Transactions found: {}", address, userTransactions);
            boolean containsUnconfirmedTransactions = false;
            int newOffset = offset;
            for (LiskTransaction transaction : userTransactions) {
                Optional<RefillRequestFlatDto> refillRequestResult = refillService.findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(transaction.getRecipientId(),
                        merchant.getId(), currency.getId(), transaction.getId());
                if ((refillRequestResult.isPresent() && refillRequestResult.get().getStatus().isSuccessEndStatus())) {
                    if (!containsUnconfirmedTransactions) {
                        newOffset++;
                    }
                } else {
                    if (!containsUnconfirmedTransactions) {
                        containsUnconfirmedTransactions = true;
                    }
                    Map<String, String> params = new HashMap<String, String>() {{
                        put("merchantId", String.valueOf(merchant.getId()));
                        put("currencyId", String.valueOf(currency.getId()));
                        put("address", transaction.getRecipientId());
                        put("txId", transaction.getId());
                    }};
                    refillRequestResult.ifPresent(request -> params.put("requestId", String.valueOf(request.getId())));

                    try {
                        processPayment(params);
                    } catch (RefillRequestAppropriateNotFoundException e) {
                        log.error(e);
                    }
                }
            }
            if (newOffset != offset) {
                refillService.updateTxOffsetForAddress(address, newOffset);
            }
        });
    }


    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        String txId = sendTransaction(mainSecret, new BigDecimal(withdrawMerchantOperationDto.getAmount()), withdrawMerchantOperationDto.getAccountTo());
        return Collections.singletonMap("hash", txId);
    }

    @Override
    public LiskTransaction getTransactionById(String txId) {
        return liskRestClient.getTransactionById(txId);
    }

    @Override
    public List<LiskTransaction> getTransactionsByRecipient(String recipientAddress) {
        return liskRestClient.getTransactionsByRecipient(recipientAddress);
    }

    @Override
    public String sendTransaction(String secret, Long amount, String recipientId) {
        long fee = liskRestClient.getFee();
        long amountToSend = amount - fee;
        LiskSendTxDto dto = new LiskSendTxDto();
        dto.setSecret(secret);
        dto.setAmount(amountToSend);
        dto.setRecipientId(recipientId);
        return liskRestClient.sendTransaction(dto);
    }

    @Override
    public String sendTransaction(String secret, BigDecimal amount, String recipientId) {
        return sendTransaction(secret, LiskTransaction.unscaleAmountToLiskFormat(amount), recipientId);
    }



    @Override
    public LiskAccount createNewLiskAccount(String secret) {
       return liskRestClient.createAccount(secret);
    }

    @Override
    public LiskAccount getAccountByAddress(String address) {
        return liskRestClient.getAccountByAddress(address);
    }

}
