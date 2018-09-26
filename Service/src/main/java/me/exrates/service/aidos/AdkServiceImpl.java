package me.exrates.service.aidos;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.btc.BtcPaymentFlatDto;
import me.exrates.model.dto.merchants.btc.BtcTransactionDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2(topic = "adk_log")
@Service
public class AdkServiceImpl implements AdkService {

    private final AidosNodeService aidosNodeService;
    private final MessageSource messageSource;
    private static final String CURRENCY_NAME = "ADK";
    private static final String MERCHANT_NAME = "ADK";
    private static final Integer CONFIRAMTION_VALUE = 100000;

   /* @Autowired
    private CoreWalletService bitcoinWalletService;*/
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RefillService refillService;

    @Autowired
    public AdkServiceImpl(AidosNodeService aidosNodeService, MessageSource messageSource) {
        this.aidosNodeService = aidosNodeService;
        this.messageSource = messageSource;
    }




   /* @PostConstruct
    private void init() {
        bitcoinWalletService.initCoreClient("node_config/node_config_adk.properties", false, false, false);
        bitcoinWalletService.initBtcdDaemon(false);
        bitcoinWalletService.walletFlux().subscribe(this::onPayment);

     *//*   initCoreClient("node_config/node_config_adk.properties", false, false, false);
        initBtcdDaemon();*//*
    }*/


    public void onPayment(BtcTransactionDto transactionDto) {
        log.info("income adk payment!!! Yoooo!");
        log.info(transactionDto);
        log.info("on payment {} - {}", CURRENCY_NAME, transactionDto);

        try {
            Merchant merchant = merchantService.findByName(MERCHANT_NAME);
            Currency currency = currencyService.findByName(CURRENCY_NAME);
            transactionDto.getDetails().stream().filter(payment -> "RECEIVE".equalsIgnoreCase( payment.getCategory()))
                        .forEach(payment -> {
                            log.debug("Payment " + payment);
                            BtcPaymentFlatDto btcPaymentFlatDto = BtcPaymentFlatDto.builder()
                                    .txId(transactionDto.getTxId())
                                    .address(payment.getAddress())
                                    .amount(payment.getAmount())
                                    .confirmations(transactionDto.getConfirmations())
                                    .blockhash(transactionDto.getBlockhash())
                                    .merchantId(merchant.getId())
                                    .currencyId(currency.getId()).build();
                            try {
                                Map<String, String> paramsMap = new HashMap<>();
                                paramsMap.put("hash", btcPaymentFlatDto.getTxId());
                                paramsMap.put("address", btcPaymentFlatDto.getAddress());
                                paramsMap.put("amount", btcPaymentFlatDto.getAmount().toPlainString());
                                processPayment(paramsMap);
                            } catch (Exception e) {
                                log.error(e);
                            }
                        });
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        Currency currency = currencyService.findByName(CURRENCY_NAME);
        Merchant merchant = merchantService.findByName(MERCHANT_NAME);
        BigDecimal amount = new BigDecimal(params.get("amount"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        refillService.autoAcceptRefillRequest(requestAcceptDto);
    }

    /*public void initCoreClient(String nodePropertySource, boolean supportInstantSend, boolean supportSubtractFee, boolean supportReferenceLine) {
        try {
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            CloseableHttpClient httpProvider = HttpClients.custom().setConnectionManager(cm)
                    .build();
            Properties nodeConfig = new Properties();
            nodeConfig.load(getClass().getClassLoader().getResourceAsStream(nodePropertySource));
            log.info("Node config: " + nodeConfig);
            btcdClient = new BtcdClientImpl(httpProvider, nodeConfig);
            this.supportInstantSend = supportInstantSend;
            this.supportSubtractFee = supportSubtractFee;
            this.supportReferenceLine = supportReferenceLine;
        } catch (Exception e) {
            log.error("Could not initialize BTCD client of config {}. Reason: {} ", nodePropertySource, e.getMessage());
            log.error(ExceptionUtils.getStackTrace(e));
        }

    }

    public void initBtcdDaemon()  {
            btcDaemon = new BtcHttpDaemonImpl(btcdClient);
        try {
            btcDaemon.init();
        } catch (Exception e) {
            log.error(e);
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }
*/

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String address = aidosNodeService.generateNewAddress();
        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{address}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address", address);
            put("message", message);
            put("qr", address);
        }};
    }


    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getBalance() {
        return aidosNodeService.getBalance().toPlainString();
    }

    @Override
    public List<BtcTransactionHistoryDto> listAllTransactions() {
        JSONArray array = aidosNodeService.getAllTransactions();
        return StreamSupport.stream(array.spliterator(), false)
                .map(transaction -> dtoMapper((JSONObject) transaction)).collect(Collectors.toList());
    }

    private BtcTransactionHistoryDto dtoMapper(JSONObject jsonObject) {
        BtcTransactionHistoryDto dto = new BtcTransactionHistoryDto();
        dto.setAddress(jsonObject.getString("address"));
        dto.setAmount(jsonObject.getString("amount"));
        dto.setCategory(jsonObject.getString("category"));
        dto.setConfirmations(jsonObject.getInt("confirmations"));
        dto.setTxId("txid");
        dto.setTime(new Timestamp(jsonObject.getInt("time")).toLocalDateTime());

        return dto;
    }

    public static String getCurrencyName() {
        return CURRENCY_NAME;
    }

    public static String getMerchantName() {
        return MERCHANT_NAME;
    }
}
