package me.exrates.service.achain;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.achain.TransactionDTO;
import me.exrates.model.dto.achain.enums.AchainTransactionType;
import me.exrates.model.enums.TransactionType;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    private static final String CURRENCY_NAME = "ACT";

    @Override
    public void scan() {

        Long lastProcessedBlock = loadLastBlock();
        /*scan to the pred-last block*/
        Long endBlock = nodeService.getBlockCount() - 1;
        while (lastProcessedBlock < endBlock) {
            lastProcessedBlock++;
            JSONArray transactions =  nodeService.getBlockTransactions(lastProcessedBlock);
            checkTransactionsArray(transactions);
            saveLastBlock(lastProcessedBlock);
        }
    }

    private void checkTransactionsArray(JSONArray transactions) {
        for (Object p : transactions) {
            try {
                JSONArray tx = (JSONArray)p;
                JSONObject trx = tx.getJSONObject(1).getJSONObject("trx");
                String result = trx.getString("result_trx_type");
                String recieveAccount = trx.getString("alp_account");
                if (StringUtils.isEmpty(recieveAccount)
                        || recieveAccount.startsWith(nodeService.getMainAccountAddress())
                        || !result.equals("origin_transaction")) {
                    continue;
                }
                //determine the transaction type
                JSONArray operations = trx.getJSONArray("operations");
                String operation = "";
                if (operations.length() == 1) {
                    operation = operations.getJSONObject(0).getString("type");
                } else if (operations.length() > 1) {
                    for (Object op : operations) {
                        JSONObject opJson = (JSONObject)op;
                        if (opJson.get("type").equals("deposit_op_type")) {
                            operation = "deposit_op_type";
                        }
                    }
                } else {
                    continue;
                }
                AchainTransactionType transactionType =
                        AchainTransactionType.convert(operation);
                JSONObject amountData = trx.getJSONObject("alp_inport_asset");
                String finalAmount = parseAmount(amountData.getLong("amount"));
                String txHash = tx.getString(0);
                switch (transactionType) {
                    /*accept ACT transfer*/
                    case SIMPLE_TRANSFER: {
                        acceptPayment(recieveAccount, txHash, finalAmount, MERCHANT_NAME, CURRENCY_NAME);
                        break;
                    }
                    /*accept contract transfer*/
                    case CONTRACT_CALL: {
                        String contractId = " ";
                        AchainContract contract =
                            Preconditions.checkNotNull(tokenContext.getByContractId(contractId));
                        acceptPayment(recieveAccount, txHash, finalAmount, contract.getMerchantName(), contract.getCurencyName());
                        break;
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    private String parseAmount(Long amount) {
        Double res = amount/10000d;
        return res.toString();
    }

    private void acceptPayment(String address, String txHash, String amount, String merchantName, String currencyName) {
        Map<String, String> paymentParamsMap = new HashMap<>();
        paymentParamsMap.put("currency", currencyName);
        paymentParamsMap.put("merchant", merchantName);
        paymentParamsMap.put("address", address);
        paymentParamsMap.put("hash", txHash);
        paymentParamsMap.put("amount", amount);
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