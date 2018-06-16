package me.exrates.service.achain;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.achain.TransactionDTO;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.math.BigDecimal;
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

    @Override
    public void scan() {
        String lastSavedBlock = "";
        List<TransactionDTO> txs = nodeService.getTransactionsList("", "", 100, lastSavedBlock, "-1");
        txs.forEach(p->{

        });
    }

    private void acceptPayment(TransactionDTO transactionDTO, String merchantName, String currencyName) {
        Map<String, String> paymentParamsMap = new HashMap<>();
        paymentParamsMap.put("currency", currencyName);
        paymentParamsMap.put("merchant", merchantName);
        paymentParamsMap.put("address", transactionDTO.getToAddr());
        paymentParamsMap.put("hash", transactionDTO.getTrxId());
        paymentParamsMap.put("amount", parseAmount(transactionDTO.getAmount()));
        try {
            achainService.processPayment(paymentParamsMap);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }
    }

    private String parseAmount(Long amount) {
        return amount.toString();
    }
}