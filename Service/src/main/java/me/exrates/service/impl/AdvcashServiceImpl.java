package me.exrates.service.impl;


import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import me.exrates.service.AdvcashService;
import me.exrates.service.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@PropertySource("classpath:/${spring.profile.active}/merchants/advcashmoney.properties")
public class AdvcashServiceImpl implements AdvcashService{

    private @Value("${advcash.accountId}") String accountId;
    private @Value("${advcash.accountPass}") String accountPass;
    private @Value("${advcash.payeeName}") String payeeName;
    private @Value("${advcash.paymentSuccess}") String paymentSuccess;
    private @Value("${advcash.paymentFailure}") String paymentFailure;
    private @Value("${advcash.paymentStatus}") String paymentStatus;
    private @Value("${advcash.USDAccount}") String usdCompanyAccount;
    private @Value("${advcash.EURAccount}") String eurCompanyAccount;
    private @Value("${advcash.payeePassword}") String payeePassword;


    private static final Logger logger = LogManager.getLogger(AdvcashServiceImpl.class);

    @Autowired
    private TransactionService transactionService;

    @Override
    public Map<String, String> getPerfectMoneyParams(Transaction transaction) {
        BigDecimal sum = transaction.getAmount().add(transaction.getCommissionAmount());
        final String currency = transaction.getCurrency().getName();
        final String companyAccount;
        final Number amountToPay;
        switch (currency) {
            case "GOLD":
                amountToPay = sum.toBigInteger();
                break;
            default:
                amountToPay = sum.setScale(2, BigDecimal.ROUND_CEILING);
        }

        return new HashMap<String,String>(){
            {
                put("PAYEE_ACCOUNT", currency.equals("USD") ? usdCompanyAccount : eurCompanyAccount);
                put("PAYEE_NAME",payeeName);
                put("PAYMENT_AMOUNT", String.valueOf(amountToPay));
                put("PAYMENT_UNITS",currency);
                put("PAYMENT_ID", String.valueOf(transaction.getId()));
                put("PAYMENT_URL",paymentSuccess);
                put("NOPAYMENT_URL",paymentFailure);
                put("STATUS_URL",paymentStatus);
                put("FORCED_PAYMENT_METHOD","account");
            }
        };
    }

    @Override
    public RedirectView preparePayment(CreditsOperation creditsOperation, String email) {

        Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
        Map<String, String> params = getPerfectMoneyParams(transaction);
        BigDecimal sum = transaction.getAmount().add(transaction.getCommissionAmount());
        final String currency = transaction.getCurrency().getName();
        final Number amountToPay;
        switch (currency) {
            case "GOLD":
                amountToPay = sum.toBigInteger();
                break;
            default:
                amountToPay = sum.setScale(2, BigDecimal.ROUND_CEILING);
        }
        Properties properties = new Properties();
        String url = "https://wallet.advcash.com/sci/";
        properties.put("ac_account_email", accountId);
        properties.put("ac_sci_name", payeeName);
        properties.put("ac_amount", amountToPay);
        properties.put("ac_currency", creditsOperation.getCurrency().getName());
        properties.put("ac_order_id", transaction.getId());
        String sign = accountId + ":" + payeeName + ":" + amountToPay
                + ":" + creditsOperation.getCurrency().getName() + ":" + payeePassword
                + ":" + transaction.getId();
        try {
            properties.put("ac_sign", getSHA256String(sign));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }
        properties.put("ac_success_url", paymentSuccess);
        properties.put("ac_success__method", "POST");
        RedirectView redirectView = new RedirectView(url);
        redirectView.setAttributes(properties);


        return redirectView;
    }

    private static String getSHA256String(String stringToConfert) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(stringToConfert.getBytes());
        byte[] byteData = md.digest();
        StringBuffer result = new StringBuffer();

        for(int i = 0; i < byteData.length; ++i) {
            result.append(Integer.toString((byteData[i] & 255) + 256, 16).substring(1));
        }

        return result.toString();
    }

    @Override
    public Transaction preparePaymentTransactionRequest(CreditsOperation creditsOperation) {
        return transactionService.createTransactionRequest(creditsOperation);
    }


    @Override
    @Transactional
    public void provideTransaction(Transaction transaction) {
        transactionService.provideTransaction(transaction);
    }

    @Override
    @Transactional
    public void invalidateTransaction(Transaction transaction) {
        transactionService.invalidateTransaction(transaction);
    }

}
