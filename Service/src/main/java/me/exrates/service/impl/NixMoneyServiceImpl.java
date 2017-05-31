package me.exrates.service.impl;

import me.exrates.model.Transaction;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.AlgorithmService;
import me.exrates.service.NixMoneyService;
import me.exrates.service.TransactionService;
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

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

@Service
@PropertySource("classpath:/merchants/nixmoney.properties")
public class NixMoneyServiceImpl implements NixMoneyService {

    private @Value("${nixmoney.url}") String url;
    private @Value("${nixmoney.payeeAccountUSD}") String payeeAccountUSD;
    private @Value("${nixmoney.payeeAccountEUR}") String payeeAccountEUR;
    private @Value("${nixmoney.payeeName}") String payeeName;
    private @Value("${nixmoney.payeePassword}") String payeePassword;
    private @Value("${nixmoney.paymentUrl}") String paymentUrl;
    private @Value("${nixmoney.noPaymentUrl}") String noPaymentUrl;
    private @Value("${nixmoney.statustUrl}") String statustUrl;


    private static final Logger logger = LogManager.getLogger(NixMoneyServiceImpl.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AlgorithmService algorithmService;

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
        Properties properties = new Properties() {{
            if (currency.equals("USD")){
                put("PAYEE_ACCOUNT", payeeAccountUSD);
            }
            if (currency.equals("EUR")){
                put("PAYEE_ACCOUNT", payeeAccountEUR);
            }
            put("PAYMENT_ID", requestId);
            put("PAYEE_NAME", payeeName);
            put("PAYMENT_AMOUNT", amountToPay);
            put("PAYMENT_URL", paymentUrl);
            put("NOPAYMENT_URL", noPaymentUrl);
            put("BAGGAGE_FIELDS", "PAYEE_ACCOUNT PAYMENT_AMOUNT PAYMENT_ID");
            put("STATUS_URL", statustUrl);        }};
    /**/
        return generateFullUrlMap(url, "POST", properties);
    }

    @Override
    @Transactional
    public boolean confirmPayment(Map<String,String> params) {

        Transaction transaction;

        try{
            transaction = transactionService.findById(Integer.parseInt(params.get("PAYMENT_ID")));
        }catch (EmptyResultDataAccessException e){
            logger.error(e);
            return false;
        }
        if (transaction.isProvided()) {
            return true;
        }

        Double transactionSum = transaction.getAmount().add(transaction.getCommissionAmount()).doubleValue();

        String passwordMD5 = algorithmService.computeMD5Hash(payeePassword).toUpperCase();;
        String V2_HASH = algorithmService.computeMD5Hash(params.get("PAYMENT_ID") + ":" + params.get("PAYEE_ACCOUNT")
                + ":" + params.get("PAYMENT_AMOUNT") + ":" + params.get("PAYMENT_UNITS") + ":" + params.get("PAYMENT_BATCH_NUM")
                + ":" + params.get("PAYER_ACCOUNT") + ":" + passwordMD5 + ":" + params.get("TIMESTAMPGMT")).toUpperCase();;

        if (V2_HASH.equals(params.get("V2_HASH")) && Double.parseDouble(params.get("PAYMENT_AMOUNT"))==transactionSum){
            transactionService.provideTransaction(transaction);
        }

        return true;
    }

    @Override
    @Transactional
    public void invalidateTransaction(Transaction transaction) {
        transactionService.invalidateTransaction(transaction);
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        throw new NotImplimentedMethod("for "+params);
    }

}
