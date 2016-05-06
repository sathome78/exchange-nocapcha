package me.exrates.service.impl;

//import com.liqpay.LiqPay;

import com.google.gson.Gson;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;
import me.exrates.model.enums.OperationType;
import me.exrates.service.AlgorithmService;
import me.exrates.service.LiqpayService;
import me.exrates.service.TransactionService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import javax.xml.bind.DatatypeConverter;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Service
@PropertySource("classpath:/merchants/liqpay.properties")
public class LiqpayServiceImpl implements LiqpayService {

    private @Value("${liqpay.url}") String url;
    private @Value("${liqpay.public_key}") String public_key;
    private @Value("${liqpay.private_key}") String private_key;
    private @Value("${liqpay.apiVersion}") String apiVersion;
    private @Value("${liqpay.action}") String action;


    private static final Logger logger = LogManager.getLogger(AdvcashServiceImpl.class);

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private PendingPaymentDao pendingPaymentDao;


    public RedirectView preparePayment(CreditsOperation creditsOperation, String email){
        Transaction transaction = transactionService.createTransactionRequest(creditsOperation);

        BigDecimal sum = transaction.getAmount().add(transaction.getCommissionAmount());
        final String currency = transaction.getCurrency().getName();
        final Number amountToPay = sum.setScale(2, BigDecimal.ROUND_CEILING);

        Map params = new HashMap();
        params.put("version", Integer.parseInt(apiVersion));
        params.put("public_key", public_key);
        params.put("action", action);
        params.put("amount", amountToPay);
        params.put("currency", creditsOperation.getCurrency().getName());
        params.put("description", "Order: " + transaction.getId());
        params.put("order_id", transaction.getId());
        byte[] hashSha1 = sha1(transaction.getId() + private_key);
        String hash = base64_encode(hashSha1);
        params.put("info", hash);


        Gson gson = new Gson();
        String jsonData = gson.toJson(params);
        String data = algorithmService.base64Encode(jsonData);
        byte[] signatureSha1 = sha1((private_key+data+private_key));
        String signature = base64_encode(signatureSha1);

        final PendingPayment payment = new PendingPayment();
        payment.setTransactionHash(hash);
        payment.setInvoiceId(transaction.getId());
        pendingPaymentDao.create(payment);



        Properties properties = new Properties();
        properties.put("data", data);
        properties.put("signature", signature);

        RedirectView redirectView = new RedirectView(url);
        redirectView.setAttributes(properties);


        return redirectView;

    }

    @Override
    @Transactional
    public void provideTransaction(Transaction transaction) {
        if (transaction.getOperationType()== OperationType.INPUT){
            pendingPaymentDao.delete(transaction.getId());
        }
        transactionService.provideTransaction(transaction);
    }


    public Map<String,Object> getResponse(String data){
        String decodeData = algorithmService.base64Decode(data);
        Gson gson = new Gson();

        Map<String,Object> map = new HashMap<String,Object>();
        map = (Map<String,Object>) gson.fromJson(decodeData.toString(), map.getClass());

        return map;
    }

    public static String base64_encode(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    public static byte[] sha1(String param) {
        try {
            MessageDigest e = MessageDigest.getInstance("SHA-1");
            e.reset();
            e.update(param.getBytes("UTF-8"));
            return e.digest();
        } catch (Exception var2) {
            throw new RuntimeException("Can\'t calc SHA-1 hash", var2);
        }
    }

    @Override
    public boolean checkHashTransactionByTransactionId(int invoiceId, String inputHash) {
        Optional<PendingPayment> pendingPayment = pendingPaymentDao.findByInvoiceId(invoiceId);

        if (pendingPayment.isPresent()){
            String transactionHash = pendingPayment.get().getTransactionHash();
            return  transactionHash.equals(inputHash);
        }else {
            return false;
        }
    }

}
