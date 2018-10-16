package me.exrates.service.apollo;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
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

@Log4j2(topic = "apollo")
@PropertySource("classpath:/merchants/apollo.properties")
@Component
public class ApolloReceiveServiceImpl {


    private @Value("${apollo.main_address}")String MAIN_ADDRESS;
    private static final String PARAM_NAME = "LastBlock";
    private static final String MERCHANT_NAME = "APL";

    @Autowired
    private MerchantSpecParamsDao specParamsDao;
    @Autowired
    private ApolloNodeService apolloNodeService;
    @Autowired
    private ApolloService apolloService;

    @PostConstruct
    private void init() {

    }

    private void saveLastBlock(String hash) {
        specParamsDao.updateParam(MERCHANT_NAME, PARAM_NAME, hash);
    }

    private long loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, PARAM_NAME);
        return specParamsDto.getParamValue() == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }

    private void checkTransactions() {
        long lastBLock = loadLastBlock();
        int offset = 0;
        int limit = 10;
        JSONArray transactions;
        do {
            transactions = new JSONObject(apolloNodeService.getTransactions(MAIN_ADDRESS, offset, offset + limit)).getJSONArray("transactions");
            transactions.forEach(p -> {
                JSONObject tx = (JSONObject) p;
                long blockHeight = tx.getLong("height");
                if (loadLastBlock() >= blockHeight) {
                    return;
                }
                String sender = tx.getString("senderRS");
                JSONObject attachment = tx.getJSONObject("attachment");
                if (attachment.has("message") && !sender.equalsIgnoreCase(MAIN_ADDRESS)) {
                    long confirmations = tx.getLong("confirmations");
                    String hash = tx.getString("fullHash");
                    BigDecimal amount = parseAmount(tx.getString("amountATM"));
                    String address = attachment.getString("message");
                    RefillRequestAcceptDto requestAcceptDto = apolloService.createRequest(address, amount, hash);
                    if (needConfirmations()) {
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

    private BigDecimal parseAmount(String amount) {
        return new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, -8))).setScale(8, RoundingMode.HALF_DOWN);
    }


    private boolean needConfirmations() {
        return true;
    }


}
