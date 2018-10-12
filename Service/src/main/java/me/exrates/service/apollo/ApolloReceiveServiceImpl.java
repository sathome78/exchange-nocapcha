package me.exrates.service.apollo;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Log4j2(topic = "apollo")
@PropertySource("classpath:/merchants/apollo.properties")
@Component
public class ApolloReceiveServiceImpl {


    private @Value("${apollo.main_address}")String MAIN_ADDRESS;
    private static final String LAST_HASH_PARAM = "LastRecievedTrHash";
    private static final String MERCHANT_NAME = "APL";

    @Autowired
    private MerchantSpecParamsDao specParamsDao;

    private void saveLastHash(String hash) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_HASH_PARAM, hash);
    }

    private String loadLastHash() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_HASH_PARAM);
        return specParamsDto == null ? null : specParamsDto.getParamValue();
    }


}
