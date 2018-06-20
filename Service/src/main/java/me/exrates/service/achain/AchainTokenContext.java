package me.exrates.service.achain;

import me.exrates.model.dto.MosaicIdDto;
import me.exrates.service.nem.XemMosaicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 15.06.2018.
 */
@Service
public class AchainTokenContext {


    @Autowired
    Map<String, AchainContract> contractsMap;

    Map<String, AchainContract> conractIdMap = new HashMap<>();

    @PostConstruct
    private void init() {
        contractsMap.forEach((k,v)-> {
            conractIdMap.put(v.getContract(), v);
        });
    }

    AchainContract getByContractId(String contractId) {
        return contractsMap.get(contractId);
    }
}
