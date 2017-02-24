package me.exrates.service.impl;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import me.exrates.service.AlgorithmService;
import me.exrates.service.PayeerService;
import me.exrates.service.TransactionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

@Service
@PropertySource("classpath:/merchants/payeer.properties")
public class PayeerServiceImpl implements PayeerService {

    private @Value("${payeer.url}") String url;
    private @Value("${payeer.m_shop}") String m_shop;
    private @Value("${payeer.m_desc}") String m_desc;
    private @Value("${payeer.m_key}") String m_key;


    private static final Logger logger = org.apache.log4j.LogManager.getLogger("merchant");

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AlgorithmService algorithmService;


    @Override
    @Transactional
    public RedirectView preparePayment(CreditsOperation creditsOperation, String email) {

        Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
        BigDecimal sum = transaction.getAmount().add(transaction.getCommissionAmount());
        final Number amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);


        Properties properties = new Properties();

        properties.put("m_shop", m_shop);
        properties.put("m_orderid", String.valueOf(transaction.getId()));
        properties.put("m_amount", String.valueOf(amountToPay));
        properties.put("m_curr", creditsOperation.getCurrency().getName());
        properties.put("m_desc", algorithmService.base64Encode(m_desc));

        String sign = algorithmService.sha256(properties.getProperty(m_shop) + ":" + properties.getProperty("m_orderid")
                + ":" + properties.getProperty("m_amount") + ":" + properties.getProperty("m_curr")
                + ":" + properties.getProperty(m_desc) + ":" + m_key).toUpperCase();

        properties.put("m_sign", sign);

        RedirectView redirectView = new RedirectView(url);
        redirectView.setAttributes(properties);


        return redirectView;
    }

    @Override
    @Transactional
    public boolean confirmPayment(Map<String,String> params) {

        String sign = algorithmService.sha256(params.get("m_operation_id") + ":" + params.get("m_operation_ps")
                + ":" + params.get("m_operation_date") + ":" + params.get("m_operation_pay_date")
                + ":" + params.get("m_shop") + ":" + params.get("m_orderid")
                + ":" + params.get("m_amount") + ":" + params.get("m_curr") + ":" + params.get("m_desc")
                + ":" + params.get("m_status") + ":" + m_key).toUpperCase();

        if (params.get("m_sign").equals(sign) && params.get("m_status").equals("success")){
            Transaction transaction;

            try{
                transaction = transactionService.findById(Integer.parseInt(params.get("m_orderid")));
            }catch (EmptyResultDataAccessException e){
                logger.error(e);
                return false;
            }
            Double transactionSum = transaction.getAmount().add(transaction.getCommissionAmount()).doubleValue();

            if(Double.parseDouble(params.get("m_amount"))==transactionSum
                    && params.get("m_curr").equals(transaction.getCurrency().getName()))
            {

                transactionService.provideTransaction(transaction);
                return true;
            }
        }

        return false;
    }

}
