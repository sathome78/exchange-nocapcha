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
import me.exrates.service.exception.*;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.util.ParamMapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2(topic = "waves_log")
@Service("wavesServiceImpl")
// @PropertySource("classpath:/merchants/waves.properties")
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

    private Integer minConfirmations;
    private String mainAccount;

    private final int WAVES_AMOUNT_SCALE = 8;
    private final long WAVES_DEFAULT_FEE = 100000L;

    private Map<String, MerchantCurrencyBasicInfoDto> tokenMerchantCurrencyMap;


    private Currency currencyBase;
    private Merchant merchantBase;

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
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream("merchants/waves.properties"));
            this.minConfirmations = Integer.parseInt(props.getProperty("waves.min.confirmations"));
            this.mainAccount = props.getProperty("waves.main.account");
            initAssets(props);

            scheduler.scheduleAtFixedRate(this::processWavesTransactionsForKnownAddresses, 1L, 30L, TimeUnit.MINUTES);

        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = ParamMapUtils.getIfNotNull(params, "address");
        String txId = ParamMapUtils.getIfNotNull(params, "txId");
        int blockHeight = restClient.getCurrentBlockHeight();
        WavesTransaction wavesTransaction = restClient.getTransactionById(txId).orElseThrow(() ->
                new WavesPaymentProcessingException("Transaction not found"));
        if (!address.equals(wavesTransaction.getRecipient())) {
            throw new WavesPaymentProcessingException(String.format("Transaction with id %s has different recipient!", txId));
        }
        processWavesPayment(wavesTransaction, blockHeight);
    }

    private void processWavesPayment(WavesTransaction transaction, int lastBlockHeight/*, Integer merchantId, Integer currencyId*/) {
        log.debug("Processing tx: " + transaction);
        int merchantId;
        int currencyId;
        String assetId = transaction.getAssetId();
        if (assetId == null) {
            merchantId = merchantBase.getId();
            currencyId = currencyBase.getId();
        } else {
            MerchantCurrencyBasicInfoDto assetInfo = tokenMerchantCurrencyMap.get(assetId);
            if (assetInfo == null) {
                throw new UnknownAssetIdException("Unknown asset: " + assetId);
            }
            merchantId = assetInfo.getMerchantId();
            currencyId = assetInfo.getCurrencyId();
        }
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
        /*if (!"WAVES".equalsIgnoreCase(withdrawMerchantOperationDto.getCurrency())) {
            throw new WithdrawRequestPostException("Currency not supported by merchant");
        }*/
        try {
            String assetId = tokenMerchantCurrencyMap.entrySet().stream()
                    .filter(entry -> entry.getValue().getCurrencyName().equals(withdrawMerchantOperationDto.getCurrency()))
                    .map(Map.Entry::getKey).findFirst().orElse(null);

            String txId = sendTransaction(mainAccount, withdrawMerchantOperationDto.getAccountTo(),
                    new BigDecimal(withdrawMerchantOperationDto.getAmount()), assetId);
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

    @Override
    public void processWavesTransactionsForKnownAddresses() {
        log.debug("Start checking WAVES transactions");

        int blockHeight = restClient.getCurrentBlockHeight();

        refillService.findAllAddresses(merchantBase.getId(), currencyBase.getId()).parallelStream()
                .flatMap(address -> restClient.getTransactionsForAddress(address).stream()
                        .filter(transaction -> address.equals(transaction.getRecipient())))
                .map(transaction -> restClient.getTransactionById(transaction.getId()))
                .filter(Optional::isPresent).map(Optional::get)
                .forEach(transaction -> {
                    try {
                        processWavesPayment(transaction, blockHeight);
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
                String assetId = tokenMerchantCurrencyMap.entrySet().stream()
                        .filter(entry -> entry.getValue().getCurrencyId().equals(dto.getCurrencyId()) &&
                        entry.getValue().getMerchantId().equals(dto.getMerchantId()))
                        .map(Map.Entry::getKey).findFirst().orElse(null);

                sendTransaction(dto.getAddress(), mainAccount, dto.getAmount(), assetId);

            }
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }
    }

    private String sendTransaction(String senderAddress, String recipientAddress, BigDecimal amount, @Nullable String assetId) {
        WavesPayment payment = new WavesPayment();
        payment.setAssetId(assetId);
        payment.setSender(senderAddress);
        payment.setRecipient(recipientAddress);
        payment.setAmount(unscaleToWavelets(amount));
        payment.setFee(WAVES_DEFAULT_FEE);
        payment.setFeeAssetId(assetId);
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

    private void initAssets(Properties wavesProps) {
        currencyBase = currencyService.findByName("WAVES");
        merchantBase = merchantService.findByName("Waves");
        List<MerchantCurrencyBasicInfoDto> tokenMerchants = merchantService.findTokenMerchantsByParentId(merchantBase.getId());
        Map<String, MerchantCurrencyBasicInfoDto> tokenMap = new HashMap<>();
        for (MerchantCurrencyBasicInfoDto tokenMerchant : tokenMerchants) {
            String assetId = wavesProps.getProperty(String.format("waves.token.%s.id", tokenMerchant.getMerchantName()));
            if (assetId != null) {
                tokenMap.put(assetId, tokenMerchant);
            }
        }
        this.tokenMerchantCurrencyMap = Collections.unmodifiableMap(tokenMap);
    }

    /*
    Use to create encode wallet seed

    public static void main(String[] args) {

        System.out.println(Base58.encode("box armed repair shoot grid give slide eagle kite excess fruit earn hill one legal".getBytes(Charset.forName("UTF-8"))));
    }*/

}
