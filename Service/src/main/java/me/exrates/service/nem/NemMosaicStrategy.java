package me.exrates.service.nem;

import me.exrates.service.impl.EthTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 27.02.2018.
 */
@Component
public class NemMosaicStrategy {

    @Autowired
    Map<String, XemMosaicService> mosaicMap;

    Map<String, XemMosaicService> servicesByMosaicNameMap = new HashMap<>();

    @PostConstruct
    private void init() {
        mosaicMap.forEach((k,v)-> {
            servicesByMosaicNameMap.put(v.getMosaicName(), v);
        });
    }

    public XemMosaicService getByCurrencyMosaic(String mosaicName) {
        return servicesByMosaicNameMap.get(mosaicName);
    }
}
