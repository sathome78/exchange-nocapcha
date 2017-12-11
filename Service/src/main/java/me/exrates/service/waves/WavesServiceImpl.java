package me.exrates.service.waves;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.waves.WavesPayment;
import me.exrates.model.dto.merchants.waves.WavesTransaction;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.WavesPaymentProcessingException;
import me.exrates.service.exception.WavesRestException;
import me.exrates.service.exception.WithdrawRequestPostException;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.util.ParamMapUtils;
import org.bitcoinj.core.Base58;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2(topic = "waves_log")
@Service("wavesServiceImpl")
@PropertySource("classpath:/merchants/waves.properties")
public class WavesServiceImpl implements WavesService {

    @Autowired
    private WavesRestClient restClient;

    @Autowired
    private RefillService refillService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private MessageSource messageSource;

    private @Value("${waves.min.confirmations}") Integer minConfirmations;
    private @Value("${waves.main.account}") String mainAccount;

    private final int WAVES_AMOUNT_SCALE = 8;
    private final long WAVES_DEFAULT_FEE = 100000L;


    private final String currencyName = "WAVES";
    private final String merchantName = "Waves";

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String address = restClient.generateNewAddress();
        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{address}, request.getLocale());
        return new HashMap<String, String>() {{
            put("message", message);
            put("address", address);
            put("qr", address);
        }};
    }

    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(this::processWavesTransactionsForKnownAddresses, 30L, 120L, TimeUnit.SECONDS);
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = ParamMapUtils.getIfNotNull(params, "address");
        String txId = ParamMapUtils.getIfNotNull(params, "txId");
        Currency currency = currencyService.findByName(currencyName);
        Merchant merchant = merchantService.findByName(merchantName);
        int blockHeight = restClient.getCurrentBlockHeight();
        WavesTransaction wavesTransaction = restClient.getTransactionById(txId).orElseThrow(() ->
                new WavesPaymentProcessingException("Transaction not found"));
        if (!address.equals(wavesTransaction.getRecipient())) {
            throw new WavesPaymentProcessingException(String.format("Transaction with id %s has different recipient!", txId));
        }
        processWavesPayment(wavesTransaction, blockHeight, merchant.getId(), currency.getId());
    }

    private void processWavesPayment(WavesTransaction transaction, int lastBlockHeight, Integer merchantId, Integer currencyId) {
        log.debug("Processing tx: " + transaction);
        Optional<RefillRequestFlatDto> refillRequestResult =
                refillService.findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(transaction.getRecipient(),
                        merchantId, currencyId, transaction.getId());
        int confirmations = lastBlockHeight - transaction.getHeight();
        BigDecimal requestAmount = scaleFromWavelets(transaction.getAmount() - WAVES_DEFAULT_FEE);

        if (!refillRequestResult.isPresent()) {
            Integer requestId = refillService.createRefillRequestByFact(RefillRequestAcceptDto.builder()
                    .address(transaction.getRecipient())
                    .amount(requestAmount)
                    .merchantId(merchantId)
                    .currencyId(currencyId)
                    .merchantTransactionId(transaction.getId()).build());
            if (confirmations >= 0 && confirmations < minConfirmations) {
                try {
                    log.debug("put on bch exam {}", transaction);
                    refillService.putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto.builder()
                            .requestId(requestId)
                            .merchantId(merchantId)
                            .currencyId(currencyId)
                            .address(transaction.getRecipient())
                            .amount(requestAmount)
                            .hash(transaction.getId()).build());
                } catch (RefillRequestAppropriateNotFoundException e) {
                    log.error(e);
                }
            } else {
                changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto.builder()
                        .requestId(requestId)
                        .address(transaction.getRecipient())
                        .amount(requestAmount)
                        .confirmations(confirmations)
                        .currencyId(currencyId)
                        .merchantId(merchantId)
                        .hash(transaction.getId()).build());
            }
        } else {
            RefillRequestFlatDto flatDto = refillRequestResult.get();
            if (!flatDto.getStatus().isSuccessEndStatus()) {
                changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto.builder()
                        .requestId(refillRequestResult.get().getId())
                        .address(transaction.getRecipient())
                        .amount(requestAmount)
                        .confirmations(confirmations)
                        .currencyId(currencyId)
                        .merchantId(merchantId)
                        .hash(transaction.getId()).build());
            }

        }
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        if (!"WAVES".equalsIgnoreCase(withdrawMerchantOperationDto.getCurrency())) {
            throw new WithdrawRequestPostException("Currency not supported by merchant");
        }
        try {
            String txId = sendTransaction(mainAccount, withdrawMerchantOperationDto.getAccountTo(),
                    new BigDecimal(withdrawMerchantOperationDto.getAmount()));
            return Collections.singletonMap("hash", txId);
        } catch (WavesRestException e) {
            if (e.getCode() == 112) {
                throw new InsufficientCostsInWalletException(e);
            }
            if (e.getCode() == 102) {
                throw new InvalidAccountException(e);
            }
            throw new MerchantException(e);
        } catch (Exception e) {
            throw new MerchantException(e);
        }
    }

    private void processWavesTransactionsForKnownAddresses() {
        log.debug("Start checking WAVES transactions");
        Currency currency = currencyService.findByName(currencyName);
        Merchant merchant = merchantService.findByName(merchantName);
        int blockHeight = restClient.getCurrentBlockHeight();

        refillService.findAllAddresses(merchant.getId(), currency.getId()).parallelStream()
                .flatMap(address -> restClient.getTransactionsForAddress(address).stream()
                        .filter(transaction -> address.equals(transaction.getRecipient())))
                .map(transaction -> restClient.getTransactionById(transaction.getId()))
                .filter(Optional::isPresent).map(Optional::get)
                .forEach(transaction -> {
                    try {
                        processWavesPayment(transaction, blockHeight, merchant.getId(), currency.getId());
                    } catch (Exception e) {
                        log.error(e);
                    }
                });
    }

    private void changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto dto) {
        try {
            refillService.setConfirmationCollectedNumber(dto);
            if (dto.getConfirmations() >= minConfirmations) {
                log.debug("Providing transaction!");
                RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.of(dto);
                refillService.autoAcceptRefillRequest(requestAcceptDto);
                sendTransaction(dto.getAddress(), mainAccount, dto.getAmount());

            }
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }
    }

    private String sendTransaction(String senderAddress, String recipientAddress, BigDecimal amount) {
        WavesPayment payment = new WavesPayment();
        payment.setSender(senderAddress);
        payment.setRecipient(recipientAddress);
        payment.setAmount(unscaleToWavelets(amount));
        payment.setFee(WAVES_DEFAULT_FEE);
        return restClient.transferCosts(payment);
    }

    private BigDecimal scaleFromWavelets(Long unscaled) {
        return BigDecimal.valueOf(unscaled, WAVES_AMOUNT_SCALE);
    }

    private long unscaleToWavelets(BigDecimal scaledAmount) {
        return scaledAmount.scaleByPowerOfTen(WAVES_AMOUNT_SCALE).setScale(0, BigDecimal.ROUND_HALF_UP).longValueExact();
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }

    /*
    Use to create encode wallet seed

    public static void main(String[] args) {

        System.out.println(Base58.encode("box armed repair shoot grid give slide eagle kite excess fruit earn hill one legal".getBytes(Charset.forName("UTF-8"))));
    }*/

}
