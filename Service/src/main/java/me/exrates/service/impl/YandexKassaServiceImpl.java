package me.exrates.service.impl;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import me.exrates.service.AlgorithmService;
import me.exrates.service.TransactionService;
import me.exrates.service.YandexKassaService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.Properties;

@Service
//@PropertySource("classpath:/merchants/yandex_kassa.properties")
public class YandexKassaServiceImpl implements YandexKassaService {

//    private @Value("${nixmoney.url}") String url;
//    private @Value("${nixmoney.payeeAccountUSD}") String payeeAccountUSD;
//    private @Value("${nixmoney.payeeAccountEUR}") String payeeAccountEUR;
//    private @Value("${nixmoney.payeeName}") String payeeName;
//    private @Value("${nixmoney.payeePassword}") String payeePassword;
//    private @Value("${nixmoney.paymentUrl}") String paymentUrl;
//    private @Value("${nixmoney.noPaymentUrl}") String noPaymentUrl;
//    private @Value("${nixmoney.statustUrl}") String statustUrl;


    private static final Logger logger = LogManager.getLogger(YandexKassaServiceImpl.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AlgorithmService algorithmService;


    @Override
    public RedirectView preparePayment(CreditsOperation creditsOperation, String email) {

        Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
        BigDecimal sum = transaction.getAmount().add(transaction.getCommissionAmount());
        final Number amountToPay = sum.setScale(2, BigDecimal.ROUND_CEILING);


        Properties properties = new Properties();


        String url = "";
        properties.put("shopId", "12345");
        properties.put("scid", "678");
        properties.put("sum", amountToPay);
        properties.put("customerNumber", email);
        properties.put("orderNumber", transaction.getId());
        properties.put("hash", algorithmService.computeMD5Hash("secret"));

        RedirectView redirectView = new RedirectView(url);
        redirectView.setAttributes(properties);


        return redirectView;
    }

//    @Override
//    @Transactional
//    public boolean confirmPayment(Map<String,String> params) {
//
//        Transaction transaction;
//        try{
//            transaction = transactionService.findById(Integer.parseInt(params.get("PAYMENT_ID")));
//        }catch (EmptyResultDataAccessException e){
//            logger.error(e);
//            return false;
//        }
//
//        String passwordMD5 = algorithmService.computeMD5Hash(payeePassword).toUpperCase();;
//        String V2_HASH = algorithmService.computeMD5Hash(params.get("PAYMENT_ID") + ":" + params.get("PAYEE_ACCOUNT")
//                + ":" + params.get("PAYMENT_AMOUNT") + ":" + params.get("PAYMENT_UNITS") + ":" + params.get("PAYMENT_BATCH_NUM")
//                + ":" + params.get("PAYER_ACCOUNT") + ":" + passwordMD5 + ":" + params.get("TIMESTAMPGMT")).toUpperCase();;
//
//        if (V2_HASH.equals(params.get("V2_HASH"))){
//            transactionService.provideTransaction(transaction);
//        }
//
//        return true;
//    }
//
//    @Override
//    @Transactional
//    public void invalidateTransaction(Transaction transaction) {
//        transactionService.invalidateTransaction(transaction);
//    }

}
