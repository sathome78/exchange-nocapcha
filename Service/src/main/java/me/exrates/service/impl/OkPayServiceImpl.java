package me.exrates.service.impl;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import me.exrates.model.Transaction;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.AlgorithmService;
import me.exrates.service.OkPayService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestIdNeededException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

@Service
@PropertySource("classpath:/merchants/okpay.properties")
public class OkPayServiceImpl implements OkPayService {

    private @Value("${okpay.ok_receiver}") String ok_receiver;
    private @Value("${okpay.ok_receiver_email}") String ok_receiver_email;
    private @Value("${okpay.ok_item_1_name}") String ok_item_1_name;
    private @Value("${okpay.ok_s_title}") String ok_s_title;
    private @Value("${okpay.url}") String url;
    private @Value("${okpay.urlReturn}") String urlReturn;

    private static final Logger logger = LogManager.getLogger("merchant");

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AlgorithmService algorithmService;

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        throw new NotImplimentedMethod("for "+withdrawMerchantOperationDto);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {

        Integer requestId = request.getId();
        if (requestId == null) {
            throw new RefillRequestIdNeededException(request.toString());
        }
        BigDecimal sum = request.getAmount();
        String currency = request.getCurrencyName();
        BigDecimal amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);

        Properties properties = new Properties();

        properties.put("ok_receiver", ok_receiver);
        properties.put("ok_currency", currency);
        properties.put("ok_invoice", String.valueOf(requestId));
        properties.put("ok_item_1_name", ok_item_1_name);
        properties.put("ok_item_1_price", amountToPay.toString());
        properties.put("ok_s_title", ok_s_title);

        return generateFullUrlMap(url, "POST", properties);
    }

    @Override
    @Transactional
    public boolean confirmPayment(Map<String,String> params) {

        if (!sendReturnRequest(params)){
            return false;
        }

        Transaction transaction;

        try{
            transaction = transactionService.findById(Integer.parseInt(params.get("ok_invoice")));
        }catch (EmptyResultDataAccessException e){
            logger.error(e);
            return false;
        }
        if (transaction.isProvided()) {
            return true;
        }

        Double transactionSum = transaction.getAmount().add(transaction.getCommissionAmount()).doubleValue();

        if(Double.parseDouble(params.get("ok_txn_gross"))==transactionSum
                && params.get("ok_txn_currency").equals(transaction.getCurrency().getName())
                && params.get("ok_txn_status").equals("completed")
                && params.get("ok_receiver_email").equals(ok_receiver_email)){

            transactionService.provideTransaction(transaction);
            return true;
        }

        return false;
    }

    private boolean sendReturnRequest(Map<String,String> params) {

        final OkHttpClient client = new OkHttpClient();
        final FormEncodingBuilder formBuilder = new FormEncodingBuilder();
        formBuilder.add("ok_verify", "true");


        for (Map.Entry<String, String> entry : params.entrySet()){
            formBuilder.add(entry.getKey(), entry.getValue());
        }

        final Request request = new Request.Builder()
                .url(urlReturn)
                .post(formBuilder.build())
                .build();
        final String returnResponse;

        try {
            returnResponse =client
                    .newCall(request)
                    .execute()
                    .body()
                    .string();
            logger.info("returnResponse: " + returnResponse);
        } catch (IOException e) {
            throw new MerchantInternalException(e);
        }

        return returnResponse.equals("VERIFIED");
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        throw new NotImplimentedMethod("for "+params);
    }
}
