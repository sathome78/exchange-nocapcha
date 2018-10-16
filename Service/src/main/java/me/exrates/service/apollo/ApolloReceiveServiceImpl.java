package me.exrates.service.apollo;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2(topic = "apollo")
@PropertySource("classpath:/merchants/apollo.properties")
@Component
public class ApolloReceiveServiceImpl {


    private @Value("${apollo.main_address}")String MAIN_ADDRESS;
    private static final String PARAM_NAME = "LastBlock";
    private static final String MERCHANT_NAME = "APL";
    private static final long GENESIS_TIME = 1515931200;

    @Autowired
    private MerchantSpecParamsDao specParamsDao;
    @Autowired
    private ApolloNodeService apolloNodeService;
    @Autowired
    private ApolloService apolloService;
    @Autowired
    private RefillService refillService;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService unconfirmedScheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(this::checkTransactions, 3, 5, TimeUnit.MINUTES);
        unconfirmedScheduler.scheduleAtFixedRate(this::checkUnconfirmed, 5, 10, TimeUnit.MINUTES);
    }

    private void checkTransactions() {
        log.debug("start check transactions");
        long lastBLock = loadLastBlock();
        int offset = 0;
        int limit = 10;
        JSONArray transactions;
        do {
            transactions = new JSONObject(apolloNodeService.getTransactions(MAIN_ADDRESS, offset, offset + limit)).getJSONArray("transactions");
            transactions.forEach(p -> {
                JSONObject tx = (JSONObject) p;
                long blockHeight = tx.getLong("height");
                if (lastBLock >= blockHeight) {
                    return;
                }
                String sender = tx.getString("senderRS");
                JSONObject attachment = tx.getJSONObject("attachment");
                if (!tx.getBoolean("phased") && attachment.has("message") && !sender.equalsIgnoreCase(MAIN_ADDRESS)) {
                    String hash = tx.getString("fullHash");
                    BigDecimal amount = parseAmount(tx.getString("amountATM"));
                    String address = attachment.getString("message");
                    RefillRequestAcceptDto requestAcceptDto = apolloService.createRequest(address, amount, hash);
                    if (needConfirmations(tx)) {
                        apolloService.putOnBchExam(requestAcceptDto);
                    } else {
                        try {
                            apolloService.processPayment(new HashMap<String, String>() {{
                                put("address", address);
                                put("hash", hash);
                                put("amount", amount.toPlainString());
                            }});
                        } catch (RefillRequestAppropriateNotFoundException e) {
                            log.error(e);
                        }
                    }
                }
            });
        } while (transactions.length() > 0);
    }


    private void checkUnconfirmed() {
        List<RefillRequestFlatDto> dtos = refillService.getInExamineWithChildTokensByMerchantIdAndCurrencyIdList(apolloService.getMerchant().getId(), apolloService.getCurrency().getId());
        dtos.forEach(p->{
            try {
                JSONObject tx = getTransaction(p.getMerchantTransactionId());
                if (!needConfirmations(tx)) {
                    apolloService.processPayment(new HashMap<String, String>() {{
                        put("address", p.getAddress());
                        put("hash", p.getMerchantTransactionId());
                        put("amount", p.getAmount().toPlainString());
                    }});
                }
            } catch (Exception e) {
                log.error(e);
            }
        });
    }


    private JSONObject getTransaction(String txHash) {
        return new JSONObject(apolloNodeService.getTransaction(txHash));
    }

    private BigDecimal parseAmount(String amount) {
        return new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, -8))).setScale(8, RoundingMode.HALF_DOWN);
    }

    private boolean needConfirmations(JSONObject tx) {
        long confirmations = tx.getLong("confirmations");
        long txTimestamp = tx.getLong("timestamp");
        long blockTimestamp = tx.getLong("blockTimestamp");
        long deadLine = tx.getLong("deadline");
        log.debug("1 {}, 2 {}", GENESIS_TIME + txTimestamp + (deadLine*60), blockTimestamp + (23*60*60));
        if ((GENESIS_TIME + txTimestamp + (deadLine*60)) > (blockTimestamp + (23*60*60))) {
            return confirmations <= 10;
        } else {
            return confirmations < 720;
        }
    }

    private void saveLastBlock(String hash) {
        specParamsDao.updateParam(MERCHANT_NAME, PARAM_NAME, hash);
    }

    private long loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, PARAM_NAME);
        return specParamsDto.getParamValue() == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }


}
