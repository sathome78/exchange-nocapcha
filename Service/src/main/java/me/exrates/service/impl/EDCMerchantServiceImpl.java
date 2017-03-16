package me.exrates.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import me.exrates.dao.EDCMerchantDao;
import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CurrencyService;
import me.exrates.service.EDCMerchantService;
import me.exrates.service.MerchantService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.MerchantInternalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by ajet on 06.03.2017.
 */
@Service
@PropertySource("classpath:/merchants/edcmerchant.properties")
public class EDCMerchantServiceImpl implements EDCMerchantService{

    private @Value("${edcmerchant.token}") String token;
    private @Value("${edcmerchant.main_account}") String main_account;
    private @Value("${edcmerchant.hook}") String hook;

    private static final Logger log = LogManager.getLogger("merchant");

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private EDCMerchantDao edcMerchantDao;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CurrencyService currencyService;

    @Override
    @Transactional
    public String createAddress(CreditsOperation creditsOperation) throws Exception{

        try {
            String address = getAddress();
            edcMerchantDao.createAddress(address, creditsOperation.getUser());
            return address;
        }catch (Exception e){
            log.error(e);
        }

        return "";
    }

    private String getAddress() {

        final OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(60, TimeUnit.SECONDS);

        final FormEncodingBuilder formBuilder = new FormEncodingBuilder();
        formBuilder.add("account", main_account);
        formBuilder.add("hook", hook);

        final Request request = new Request.Builder()
                .url("https://receive.edinarcoin.com/new-account/" + token)
                .post(formBuilder.build())
                .build();
        final String returnResponse;

        try {
            returnResponse =client
                    .newCall(request)
                    .execute()
                    .body()
                    .string();
            log.info("returnResponse: " + returnResponse);
        } catch (IOException e) {
            throw new MerchantInternalException(e);
        }

        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(returnResponse).getAsJsonObject();

        return object.get("address").getAsString();
    }

    private boolean checkTransactionByHistory(Map<String,String> params) {

        final OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url("https://receive.edinarcoin.com/history/" + token + "/" + params.get("address"))
                .build();
        final String returnResponse;

        try {
            returnResponse =client
                    .newCall(request)
                    .execute()
                    .body()
                    .string();
            log.info("returnResponse: " + returnResponse);
        } catch (IOException e) {
            throw new MerchantInternalException(e);
        }

        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(returnResponse).getAsJsonArray();

        for (JsonElement element : jsonArray){
            if (element.getAsJsonObject().get("id").getAsString().equals(params.get("id"))){
                if (element.getAsJsonObject().get("amount").getAsString().equals(params.get("amount"))){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    @Transactional
    public boolean checkMerchantTransactionIdIsEmpty(String merchantTransactionId){
        boolean isEmpty = edcMerchantDao.checkMerchantTransactionIdIsEmpty(merchantTransactionId);
        return isEmpty;
    }

    @Override
    @Transactional
    public void createAndProvideTransaction(Map<String,String> params){
        try {
            if (checkTransactionByHistory(params)){
                String userEmail = edcMerchantDao.findUserEmailByAddress(params.get("address"));
                Payment payment = new Payment();
                Currency currency = currencyService.findByName("EDR");
                payment.setCurrency(currency.getId());
                List<Integer> list = new ArrayList<>();
                list.add(currency.getId());
                MerchantCurrency merchantCurrency = merchantService.findAllByCurrencies(list, OperationType.INPUT).get(0);
                payment.setMerchant(merchantCurrency.getMerchantId());
                payment.setOperationType(OperationType.INPUT);
                payment.setMerchantImage(merchantCurrency.getListMerchantImage().get(0).getId());
                payment.setSum(Double.parseDouble(params.get("amount")));
                CreditsOperation creditsOperation = merchantService
                        .prepareCreditsOperation(payment, userEmail)
                        .orElseThrow(InvalidAmountException::new);
                Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
                edcMerchantDao.createMerchantTransaction(params.get("address"), params.get("id"), transaction.getId());
                transactionService.provideTransaction(transaction);
            }
        }catch (Exception e){
            log.error(e);
        }
    }
}
