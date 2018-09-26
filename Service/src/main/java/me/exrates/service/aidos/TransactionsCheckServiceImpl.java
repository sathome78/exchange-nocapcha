package me.exrates.service.aidos;


import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class TransactionsCheckServiceImpl implements TransactionsCheckService {


    @Autowired
    private AidosNodeService aidosNodeService;
    @Autowired
    private AdkService adkService;
    @Autowired
    private MerchantSpecParamsDao specParamsDao;

    private static final String LAST_BLOCK_PARAM = "LastBundle";
    private static final String MERCHANT_NAME = "ADK";
    private static final String CURRENCY_NAME = "ADK";

    private static final Integer TX_SCAN_COUNT = 5;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(this::checkTransactions, 3, 10, TimeUnit.MINUTES);
    }

    private void checkTransactions() {
        int offset = 0;
        do {
            JSONArray array = aidosNodeService.getAllTransactions(TX_SCAN_COUNT, offset);

        } while (true);

    }

    private String getBundleIdFrom()

    private void saveLastBlock(long blockNum) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_BLOCK_PARAM, String.valueOf(blockNum));
    }

    private String loadLastBundle() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_BLOCK_PARAM);
        return specParamsDto.getParamValue();
    }


}
