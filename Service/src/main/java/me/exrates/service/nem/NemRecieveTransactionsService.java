package me.exrates.service.nem;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by maks on 21.07.2017.
 */
@Log4j2
@Service
@PropertySource("classpath:/merchants/nem.properties")
public class NemRecieveTransactionsService {

    @Autowired
    private MerchantSpecParamsDao specParamsDao;
    @Autowired
    private NemNodeService nodeService;
    @Autowired
    private NemTransactionsService transactionsService;
    @Autowired
    private NemService nemService;

    private static final String LAST_HASH_PARAM = "LastRecievedTrHash";
    private static final String MERCHANT_NAME = "NEM";
    private static final int CONFIRMATIONS_COUNT_REFILL = 20;

    private @Value("${nem.address}")String address;


    public void checkTransactions() {
        log.debug("starting check nem income payments");
        String lastHash = loadLastHash();
        JSONArray transactions = nodeService.getIncomeTransactions(address, lastHash);
        for (int i = 0; transactions.opt(i) != null; i++) {
            JSONObject transactionData = transactions.getJSONObject(i);
            if (transactionsService.checkIsConfirmed(transactionData, CONFIRMATIONS_COUNT_REFILL)) {
                try {
                    Map<String, String> params = extractParams(transactionData);
                    nemService.processPayment(params);
                    saveLastHash(params.get("hash"));
                } catch (RefillRequestAppropriateNotFoundException e) {
                    log.error("nem refill address not found {}", transactionData.toString());
                } catch (Exception e) {
                    log.error("nem refill error {}", e);
                }
            }

        }
    }

    private Map<String, String> extractParams(JSONObject transactionMetaPair) {
        JSONObject meta = transactionMetaPair.getJSONObject("meta");
        JSONObject transaction = transactionMetaPair.getJSONObject("transaction");
        String message = transaction.getJSONObject("message").getString("payload");
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("hash", meta.getJSONObject("transactionHash").getString("data"));
        paramsMap.put("address", message);
        paramsMap.put("amount", transactionsService.transformToString(transaction.getLong("amount")));
        return paramsMap;
    }





    private void saveLastHash(String hash) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_HASH_PARAM, hash);
    }

    private String loadLastHash() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantIdAndParamName(MERCHANT_NAME, LAST_HASH_PARAM);
        return specParamsDto == null ? null : specParamsDto.getParamValue();
    }
}
