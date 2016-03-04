package me.exrates.service.impl;

import me.exrates.model.Payment;
import me.exrates.service.MerchantService;
import me.exrates.service.PerfectMoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@PropertySource("classpath:/${spring.profile.active}/merchants/perfectmoney.properties")
public class PerfectMoneyServiceImpl implements PerfectMoneyService {

    private @Value("${payeeName}") String payeeName;
    private @Value("${paymentSuccess}") String paymentSuccess;
    private @Value("${paymentFailure}") String paymentFailure;
    private @Value("${paymentStatus}") String paymentStatus;
    private @Value("${payeeAccount}") String payeeAccount;

    @Autowired
    private MerchantService merchantService;

    @Override
    public Optional<Map<String, String>> preparePayment(Payment payment, Principal principal) {
//        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        final Map<String,String> result = new HashMap<String,String>(){
            {
                put("PAYEE_ACCOUNT",payeeAccount);
                put("PAYEE_NAME",payeeName);
                put("PAYMENT_AMOUNT",String.valueOf(payment.getSum()));
                put("PAYMENT_UNITS","USD");
                put("PAYMENT_ID","666");
                put("PAYMENT_URL",paymentSuccess);
                put("NOPAYMENT_URL",paymentFailure);
                put("STATUS_URL",paymentStatus);
                put("FORCED_PAYMENT_METHOD","account");
                put("STATUS_URL","account");
            }
        };
        return Optional.of(result);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String s="“ohboyi’msogood1”";
        MessageDigest m=MessageDigest.getInstance("MD5");
        m.update(s.getBytes(),0,s.length());
        System.out.println("MD5: "+new BigInteger(1,m.digest()).toString(16));
    }
}