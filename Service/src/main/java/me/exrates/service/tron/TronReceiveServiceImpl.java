package me.exrates.service.tron;


import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;

import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.TronReceivedTransactionDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Log4j2
@Service
public class TronReceiveServiceImpl {

    @Autowired
    private TronNodeService nodeService;
    @Autowired
    private TronServiceImpl tronService;
    @Autowired
    private MerchantSpecParamsDao specParamsDao;
    @Autowired
    private TronTransactionsService tronTransactionsService;

    private static final String LAST_HASH_PARAM = "LastScannedBlock";
    private static final String MERCHANT_NAME = "TRX";
    private static final String CURRENCY_NAME = "TRX";

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
            List<TronReceivedTransactionDto> transactionDtos = parseResponse(object);
            checkTransactionsAndProceed(transactionDtos);
            lastScannedBlock++;
            saveLastBlock(lastScannedBlock);
        }
    }

    private void checkTransactionsAndProceed(List<TronReceivedTransactionDto> transactionDtos) {
        transactionDtos.forEach(p->{
            if(tronService.getAddressesHEX().contains(p.getAddress())) {
                try {
                    setAdditionalTxInfo(p);
                    RefillRequestAcceptDto dto = tronService.createRequest(p);
                    if (p.isConfirmed()) {
                        tronTransactionsService.processTransaction(p);
                    } else {
                        tronService.putOnBchExam(dto);
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            }
        });
    }

    private List<TronReceivedTransactionDto> parseResponse(JSONObject rawResponse) {
        if(rawResponse.isNull("transactions")) {
            return Collections.EMPTY_LIST;
        }
        /*JSONObject blockHeader = rawResponse.getJSONObject("block_header");*/
        JSONArray transactions = rawResponse.getJSONArray("transactions");
        return parseTransactions(transactions);
    }

    private void setAdditionalTxInfo(TronReceivedTransactionDto dto) throws Exception {
        JSONObject rawResponse = nodeService.getTransaction(dto.getHash());
        String tokenName = rawResponse.getString("tokenName");
        if (!tokenName.equals(CURRENCY_NAME)) {
            throw new Exception("unsupported token");
        }
        dto.setAddressBase58("transferToAddress");
        dto.setConfirmed(rawResponse.getBoolean("confirmed"));
    }

    private List<TronReceivedTransactionDto> parseTransactions(JSONArray transactions) {
        List<TronReceivedTransactionDto> list = new ArrayList<>();
        transactions.forEach(p->{
            try {
                list.add(TronReceivedTransactionDto.fromJson((JSONObject) p));
            } catch (Exception e) {
                log.error(e);
            }
        });
        return list;
    }

    private void saveLastBlock(long blockNum) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_HASH_PARAM, String.valueOf(blockNum));
    }

    private long loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_HASH_PARAM);
        return specParamsDto == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }

    private long getLastBlockNum() {
        JSONObject jsonObject = nodeService.getLAstBlock();
        return jsonObject.getJSONObject("block_header").getJSONObject("raw_data").getLong("number");
    }

}
