package me.exrates.service.tron;


import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class TronReceiveServiceImpl {

    @Autowired
    private TronNodeService nodeService;
    @Autowired
    private TronServiceImpl tronService;
    @Autowired
    private MerchantSpecParamsDao specParamsDao;

    private static final String LAST_HASH_PARAM = "LastScannedBlock";
    private static final String MERCHANT_NAME = "TRX";

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(this::checkBlocks, 1, 5, TimeUnit.MINUTES);
    }

    private void checkBlocks() {
        long lastScannedBlock = loadLastBlock();
        long blockchainHeight = getLastBlockNum();
        while (lastScannedBlock < blockchainHeight) {
            JSONObject object = nodeService.getTransactions(lastScannedBlock + 1);

            lastScannedBlock++;
        }
    }

    private void parseTransactions(JSONObject rawResponse) {

    }

    private void saveLastBlock(String hash) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_HASH_PARAM, hash);
    }

    private long loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_HASH_PARAM);
        return specParamsDto == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }

    private long getLastBlockNum() {
        JSONObject jsonObject = nodeService.getLAstBlock();
        /*todo parse object*/
        return 0;
    }

}
