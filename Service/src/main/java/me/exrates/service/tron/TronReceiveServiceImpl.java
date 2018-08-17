package me.exrates.service.tron;


import lombok.extern.log4j.Log4j2;
import me.exrates.service.achain.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Log4j2
@Service
public class TronReceiveServiceImpl {

    @Autowired
    private NodeService nodeService;
    @Autowired
    private TronServiceImpl tronService;


    @PostConstruct
    private void init() {

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
