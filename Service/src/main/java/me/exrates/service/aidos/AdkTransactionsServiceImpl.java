package me.exrates.service.aidos;


import com.google.common.base.Preconditions;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.merchants.btc.BtcTransactionDto;
import me.exrates.model.dto.merchants.btc.BtcTxPaymentDto;
import me.exrates.model.dto.merchants.btc.BtcWalletPaymentItemDto;
import me.exrates.service.RefillService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Log4j2(topic = "adk_log")
@Service
public class AdkTransactionsServiceImpl implements TransactionsCheckService {


    private final AidosNodeService aidosNodeService;
    private final AdkService adkService;
    private final MerchantSpecParamsDao specParamsDao;
    private final RefillService refillService;


    private static final String LAST_BLOCK_PARAM = "LastBundle";
    private static final Integer CONFIRMATION_VALUE = 100000;
    private static final String RECEIVE_CATEGORY_VALUE = "receive";
    private static final String MERCHANT_NAME = "ADK";
    private static final Integer TX_SCAN_COUNT = 10;
    private static final Object SEND_MONITOR = new Object();


    private ScheduledExecutorService txScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService unconfScheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public AdkTransactionsServiceImpl(AidosNodeService aidosNodeService, AdkService adkService, MerchantSpecParamsDao specParamsDao, RefillService refillService) {
        this.aidosNodeService = aidosNodeService;
        this.adkService = adkService;
        this.specParamsDao = specParamsDao;
        this.refillService = refillService;
    }

    @PostConstruct
    private void init() {
        txScheduler.scheduleAtFixedRate(this::checkTransactions, 0, 10, TimeUnit.MINUTES);
        unconfScheduler.scheduleAtFixedRate(this::checkUnconfirmedJob, 10, 15, TimeUnit.MINUTES);
    }

    private void checkTransactions() {
        try {
            log.info("start check transactions");
            int offset = 0;
            String lastBundle = loadLastBundle();
            log.info("lastBundle {}", lastBundle);
            List<String> hashes;
            do {
                hashes = getTxHashesToProcess(aidosNodeService.getAllTransactions(TX_SCAN_COUNT, offset));
                hashes.forEach(p -> {
                    log.info("hash {}", p);
                    if (p.equals(lastBundle)) {
                        return;
                    }
                    BtcTransactionDto transactionDto = aidosNodeService.getTransaction(p);
                    log.info("tx dto {}", transactionDto);
                    RefillRequestAcceptDto requestDto = adkService.createRequest(transactionDto);
                    refillService.invalidateAddress(requestDto.getAddress(), adkService.getMerchant().getId(), adkService.getCurrency().getId());
                    if (transactionDto.getConfirmations().equals(CONFIRMATION_VALUE)) {
                        processTransaction(transactionDto);
                    } else {
                        adkService.putOnBchExam(requestDto);
                    }
                    saveLastBundle(p);
                });
                offset += TX_SCAN_COUNT;
            } while (hashes.size() < TX_SCAN_COUNT);
        } catch (Exception e) {
            log.error(e);
        }
    }

    private List<String> getTxHashesToProcess(JSONArray array) {
        log.info("transactions {}", array);
        return StreamSupport.stream(array.spliterator(), false)
                .map(JSONObject.class::cast)
                .filter(tx -> tx.getString("category").equals(RECEIVE_CATEGORY_VALUE))
                .map(tx -> tx.getString("txid"))
                .collect(Collectors.toList());
    }

    private void processTransaction(BtcTransactionDto transactionDto) {
        BtcTxPaymentDto paymentFlatDto = transactionDto.getDetails().get(0);
        Preconditions.checkArgument(paymentFlatDto.getCategory().equals(RECEIVE_CATEGORY_VALUE));
        processTransaction(paymentFlatDto.getAddress(), transactionDto.getTxId(), paymentFlatDto.getAmount().toPlainString());
    }

    private void processTransaction(String address, String hash, String amount) {
        try {
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("hash", address);
            paramsMap.put("address", hash);
            paramsMap.put("amount", amount);
            adkService.processPayment(paramsMap);
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Synchronized(value = "SEND_MONITOR")
    @Override
    public String sendManyTransactions(List<BtcWalletPaymentItemDto> payments) {
        JSONObject object = aidosNodeService.sendMany(payments);
        return "";
    }

    private void checkUnconfirmedJob() {
        List<RefillRequestFlatDto> dtos = refillService.getInExamineWithChildTokensByMerchantIdAndCurrencyIdList(adkService.getMerchant().getId(), adkService.getCurrency().getId());
        dtos.forEach(p->{
            try {
                if (isTransactionConfirmed(p.getMerchantTransactionId())) {
                    processTransaction(p.getAddress(), p.getMerchantTransactionId(), p.getAmount().toString());
                }
            } catch (Exception e) {
                log.error(e);
            }
        });
    }

    private boolean isTransactionConfirmed(String txHash) {
        return aidosNodeService.getTransaction(txHash).getConfirmations().equals(CONFIRMATION_VALUE);
    }


    private void saveLastBundle(String bundle) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_BLOCK_PARAM, bundle);
    }

    private String loadLastBundle() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_BLOCK_PARAM);
        return specParamsDto.getParamValue();
    }


}
