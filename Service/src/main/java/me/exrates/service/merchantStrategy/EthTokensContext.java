package me.exrates.service.merchantStrategy;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.impl.EthTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 24.01.2018.
 */
@Log4j2
@Component
public class EthTokensContext {

    @Autowired
    Map<String, EthTokenService> merchantServiceMap;

    Map<Integer, EthTokenService> merchantMapByCurrencies = new HashMap<>();

    @PostConstruct
    private void init() {
        merchantServiceMap.forEach((k,v)-> {
            merchantMapByCurrencies.put(v.currencyId(), v);
        });
    }

    public EthTokenService getByCurrencyId(int currencyId) {
        return merchantMapByCurrencies.get(currencyId);
    }
}
