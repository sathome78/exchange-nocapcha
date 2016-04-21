package me.exrates.service.impl;

import com.squareup.okhttp.OkHttpClient;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import me.exrates.service.AlgorithmService;
import me.exrates.service.Privat24Service;
import me.exrates.service.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
@PropertySource("classpath:/merchants/privat24.properties")
public class Privat24ServiceImpl implements Privat24Service {

    private @Value("${privat24.url}") String url;
    private @Value("${privat24.merchant}") String merchant;
    private @Value("${privat24.details}") String details;
    private @Value("${privat24.ext_details}") String ext_details;
    private @Value("${privat24.pay_way}") String pay_way;
    private @Value("${privat24.return_url}") String return_url;
    private @Value("${privat24.server_url}") String server_url;
    private @Value("${privat24.password}") String password;

    private final OkHttpClient client = new OkHttpClient();

    private static final Logger LOG = LogManager.getLogger("merchant");

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AlgorithmService algorithmService;


    @Override
    public RedirectView preparePayment(CreditsOperation creditsOperation, String email) {

        LOG.debug("Begin method: preparePayment.");
        Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
        BigDecimal sum = transaction.getAmount().add(transaction.getCommissionAmount());
        final Number amountToPay = sum.setScale(2, BigDecimal.ROUND_CEILING);

        Properties properties = new Properties();

        properties.put("amt", amountToPay);
        properties.put("ccy", creditsOperation.getCurrency().getName());
        properties.put("merchant", merchant);
        properties.put("order", transaction.getId());
        properties.put("details", details);
        properties.put("ext_details", ext_details + transaction.getId());
        properties.put("pay_way", pay_way);
        properties.put("return_url", return_url);
        properties.put("server_url", server_url);

        RedirectView redirectView = new RedirectView(url);
        redirectView.setAttributes(properties);


        return redirectView;
    }

    @Override
    @Transactional
    public boolean confirmPayment(Map<String,String> params, String signature, String payment) {

        LOG.debug("Begin method: confirmPayment.");
        Transaction transaction;
        try{
            transaction = transactionService.findById(Integer.parseInt(params.get("order")));
        }catch (EmptyResultDataAccessException e){
            LOG.error(e);
            return false;
        }

        String checkSignature = algorithmService.sha1(algorithmService.computeMD5Hash(payment + password));
        if (checkSignature.equals(signature)){
            transactionService.provideTransaction(transaction);
            return true;
        }

        return false;
    }

}
