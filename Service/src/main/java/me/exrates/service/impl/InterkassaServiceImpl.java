package me.exrates.service.impl;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import me.exrates.service.AlgorithmService;
import me.exrates.service.InterkassaService;
import me.exrates.service.TransactionService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@PropertySource("classpath:/merchants/interkassa.properties")
public class InterkassaServiceImpl implements InterkassaService {

    private @Value("${interkassa.checkoutId}") String checkoutId;
    private @Value("${interkassa.statustUrl}") String statustUrl;
    private @Value("${interkassa.successtUrl}") String successtUrl;
    private @Value("${interkassa.secretKey}") String secretKey;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AlgorithmService algorithmService;

    private static final Logger LOG = LogManager.getLogger("merchant");


    @Override
    public Map<String, String> preparePayment(final CreditsOperation creditsOperation,final String email) {

        LOG.debug("Begin method: preparePayment.");
        final Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
        final BigDecimal sum = transaction.getAmount().add(transaction.getCommissionAmount());
        final Number amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);

        final Map<String, String> properties = new TreeMap<>();

        properties.put("ik_am", String.valueOf(amountToPay));
        properties.put("ik_co_id", checkoutId);
        properties.put("ik_cur", creditsOperation.getCurrency().getName());
        properties.put("ik_desc", "Exrates input");
        properties.put("ik_ia_m", "post");
        properties.put("ik_ia_u", statustUrl);
        properties.put("ik_pm_no", String.valueOf(transaction.getId()));
        properties.put("ik_pnd_m", "post");
        properties.put("ik_pnd_u", statustUrl);
        properties.put("ik_suc_u", successtUrl);
        properties.put("ik_suc_m", "post");

        properties.put("ik_sign",getSignature(properties));

        return properties;
    }

    @Override
    @Transactional
    public boolean confirmPayment(final Map<String,String> params) {

        Transaction transaction;
        try{
            transaction = transactionService.findById(Integer.parseInt(params.get("ik_pm_no")));
            if (transaction.isProvided()){
                return true;
            }
        }catch (EmptyResultDataAccessException e){
            LOG.error(e);
            return false;
        }

        String signature = params.get("ik_sign");
        params.remove("ik_sign");
        String checkSignature = getSignature(new TreeMap<String, String>(params));
        if(checkSignature.equals(signature)
                && params.get("ik_co_id").equals(checkoutId)
                && params.get("ik_inv_st").equals("success"))
        {
            transactionService.provideTransaction(transaction);
            LOG.debug("Payment successful.");
            return true;
        }

        LOG.debug("Payment failure.");
        return false;
    }

    private String getSignature(final Map<String, String> params){

        List<String> listValues = new ArrayList<String>(params.values());

        listValues.add(secretKey);
        String stringValues = StringUtils.join(listValues, ":" );
        byte[] signMD5 = algorithmService.computeMD5Byte(stringValues);

        return Base64.getEncoder().encodeToString(signMD5);
    }

}
