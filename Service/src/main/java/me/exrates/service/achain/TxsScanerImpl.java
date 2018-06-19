package me.exrates.service.achain;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.achain.TransactionDTO;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Maks on 14.06.2018.
 */
@Log4j2(topic = "achain")
@Service
public class TxsScanerImpl implements BlocksScaner {

    @Autowired
    private NodeService nodeService;
    @Autowired
    private MerchantSpecParamsDao merchantSpecParamsDao;
    @Autowired
    private AchainTokenContext tokenContext;
    @Autowired
    private AchainService achainService;
    @Autowired
    private MerchantSpecParamsDao specParamsDao;

    private static final String PARAM_NAME = "LastScannedBlock";
    private static final String MERCHANT_NAME = "ACHAIN";

    @Override
    public void scan() {

        Long lastProcessedBlock = loadLastBlock();
        Long endBlock = nodeService.getBlockCount();
        List<TransactionDTO> txs = nodeService.getTransactionsList(
                nodeService.getAccountName(),
                " ",
                1000,
                lastProcessedBlock.toString(),
                endBlock.toString());
        for (TransactionDTO tx : txs) {


            endBlock = tx.getBlockNum();
        }
        saveLastBlock(endBlock);
    }

    private void acceptPayment(TransactionDTO transactionDTO, String merchantName, String currencyName) {
        Map<String, String> paymentParamsMap = new HashMap<>();
        paymentParamsMap.put("currency", currencyName);
        paymentParamsMap.put("merchant", merchantName);
        paymentParamsMap.put("address", transactionDTO.getToAddr());
        paymentParamsMap.put("hash", transactionDTO.getTrxId());
        paymentParamsMap.put("amount", transactionDTO.getAmount().toString());
        try {
            achainService.processPayment(paymentParamsMap);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }
    }



    private void saveLastBlock(Long blockNum) {
        specParamsDao.updateParam(MERCHANT_NAME, PARAM_NAME, blockNum.toString());
    }

    private Long loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, PARAM_NAME);
        return specParamsDto == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }
}