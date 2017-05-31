package me.exrates.service.impl;

import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.AlgorithmService;
import me.exrates.service.InterkassaService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestIdNeededException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@PropertySource("classpath:/merchants/interkassa.properties")
public class InterkassaServiceImpl implements InterkassaService {

    private @Value("${interkassa.url}") String url;
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
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        throw new NotImplimentedMethod("for "+withdrawMerchantOperationDto);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request){
        Integer requestId = request.getId();
        if (requestId == null) {
            throw new RefillRequestIdNeededException(request.toString());
        }
        BigDecimal sum = request.getAmount();
        String currency = request.getCurrencyName();
        BigDecimal amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);
    /**/
        final Map<String, String> map = new TreeMap<>();

        map.put("ik_am", String.valueOf(amountToPay));
        map.put("ik_co_id", checkoutId);
        map.put("ik_cur", currency);
        map.put("ik_desc", "Exrates input");
        map.put("ik_ia_m", "post");
        map.put("ik_ia_u", statustUrl);
        map.put("ik_pm_no", String.valueOf(requestId));
        map.put("ik_pnd_m", "post");
        map.put("ik_pnd_u", statustUrl);
        map.put("ik_suc_u", successtUrl);
        map.put("ik_suc_m", "post");

        map.put("ik_sign",getSignature(map));

        Properties properties = new Properties();
        properties.putAll(map);
    /**/

        return generateFullUrlMap(url, "POST", properties);
    }

    @Override
    @Transactional
    public boolean confirmPayment(final Map<String,String> params) {

//        Transaction transaction;
//        try{
//            transaction = transactionService.findById(Integer.parseInt(params.get("ik_pm_no")));
//            if (transaction.isProvided()){
//                return true;
//            }
//        }catch (EmptyResultDataAccessException e){
//            LOG.error(e);
//            return false;
//        }
//        Double transactionSum = transaction.getAmount().add(transaction.getCommissionAmount()).doubleValue();
//
//        String signature = params.get("ik_sign");
//        params.remove("ik_sign");
//        String checkSignature = getSignature(new TreeMap<String, String>(params));
//        if(checkSignature.equals(signature)
//                && params.get("ik_co_id").equals(checkoutId)
//                && params.get("ik_inv_st").equals("success")
//                && Double.parseDouble(params.get("ik_am"))==transactionSum)
//        {
//            transactionService.provideTransaction(transaction);
//            LOG.debug("Payment successful.");
//            return true;
//        }
//
//        LOG.debug("Payment failure.");
        return false;
    }

    private String getSignature(final Map<String, String> params){

        List<String> listValues = new ArrayList<String>(params.values());

        listValues.add(secretKey);
        String stringValues = StringUtils.join(listValues, ":" );
        byte[] signMD5 = algorithmService.computeMD5Byte(stringValues);

        return Base64.getEncoder().encodeToString(signMD5);
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        throw new NotImplimentedMethod("for "+params);
    }
}
