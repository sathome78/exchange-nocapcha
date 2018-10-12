package me.exrates.service.apollo;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

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

    private void saveLastBlock(String hash) {
        specParamsDao.updateParam(MERCHANT_NAME, PARAM_NAME, hash);
    }

    private long loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, PARAM_NAME);
        return specParamsDto.getParamValue() == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }

    private void checkTransactions() {
        long lastBLock = loadLastBlock();
        JSONArray transactions = apolloNodeService.getTransactions(MAIN_ADDRESS, lastBLock);

    }


}
