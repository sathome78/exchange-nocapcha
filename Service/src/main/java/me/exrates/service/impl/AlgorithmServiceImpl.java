package me.exrates.service.impl;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.DecryptionFailureException;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.secretsmanager.model.InternalServiceErrorException;
import com.amazonaws.services.secretsmanager.model.InvalidRequestException;
import com.amazonaws.services.secretsmanager.model.ResourceNotFoundException;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.enums.OperationType;
import me.exrates.service.AlgorithmService;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static com.yandex.money.api.utils.Numbers.bytesToHex;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@Log4j2(topic = "algorithm_log")
@PropertySource("classpath:/merchants/gapi_wallet.properties")
public class AlgorithmServiceImpl implements AlgorithmService {

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static BASE64Encoder enc = new BASE64Encoder();
    private static BASE64Decoder dec = new BASE64Decoder();

    private static final int decimalPlaces = 8;
    private static final BigDecimal HUNDRED = new BigDecimal(100L).setScale(decimalPlaces, ROUND_HALF_UP);
    private static final BigDecimal SATOSHI = new BigDecimal(100_000_000L);

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CurrencyService currencyService;

    @Value("${gapi.secret.name}")
    private String secretName;

    @Autowired
    public AlgorithmServiceImpl(){

    }

    @Override
    public String computeMD5Hash(String string) {
        try {
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            final byte[] digest = md5.digest(string.getBytes("UTF-8"));
            return byteArrayToHexString(digest);
        } catch (UnsupportedEncodingException|NoSuchAlgorithmException ignore) {
            return null;
        }
    }

    @Override
    public byte[] computeMD5Byte(String string) {
        try {
            final MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest(string.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException|NoSuchAlgorithmException ignore) {
            return null;
        }
    }

    @Override
    public String sha1(final String string) {
        try {
            final MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            byte[] digest = sha1.digest(string.getBytes("UTF-8"));
            return byteArrayToHexString(digest);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String sha256(final String string) {
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(string.getBytes());
            return bytesToHex(md.digest());
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public String base64Encode(final String string) {
        return Base64
            .getEncoder()
            .encodeToString(string.getBytes());
    }

    @Override
    public String base64Decode(final String string) {
        return new String(Base64.getDecoder().decode(string));
    }

    @Override
    public BigDecimal computeAmount(final BigDecimal amount, final BigDecimal commission, final OperationType type) {
        switch (type) {
            case INPUT:
                return amount.add(commission).setScale(decimalPlaces, ROUND_HALF_UP);
            case OUTPUT:
                return amount.subtract(commission).setScale(decimalPlaces, ROUND_HALF_UP);
            default:
                throw new IllegalArgumentException(type + " is not defined operation for this method");
        }
    }

    @Override
    public BigDecimal computeCommission(final BigDecimal amount, final OperationType type) {
        BigDecimal commission = commissionService.findCommissionByTypeAndRole(type, userService.getUserRoleFromSecurityContext()).getValue();
        return amount.multiply(commission.divide(HUNDRED).setScale(decimalPlaces, ROUND_HALF_UP)).setScale(decimalPlaces, ROUND_HALF_UP);
    }

    @Override
    public BigDecimal fromSatoshi(final String amount) {
        return new BigDecimal(amount).setScale(decimalPlaces, ROUND_HALF_UP).divide(SATOSHI).setScale(decimalPlaces, ROUND_HALF_UP);
    }

    @Override
    public BigDecimal toBigDecimal(final String value) {
        return new BigDecimal(value).setScale(decimalPlaces, ROUND_HALF_UP);
    }

    private String byteArrayToHexString(byte[] bytes) {
        final StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }

    @Override
    public String encodeByKey(String txt) {
        String key = getSecret();
        String text = xorMessage(txt, key);
        try {
            return enc.encode(text.getBytes(DEFAULT_ENCODING));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    public String decodeByKey(String text) {
        String txt;
        String key;
        try {
            txt = new String(dec.decodeBuffer(text), DEFAULT_ENCODING);
        } catch (IOException e) {
            return null;
        }
        key = getSecret();
        return xorMessage(txt, key);
    }

    //    У инстанса должна быть iam policy, на чтение aws секретов!!!!!
    private String getSecret() {
        String region = "us-east-2";

        // Create a Secrets Manager client
        AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard()
                .withRegion(region)
                .build();

        // In this sample we only handle the specific exceptions for the 'GetSecretValue' API.
        // See https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
        // We rethrow the exception by default.

        String secret = null, decodedBinarySecret = null;
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest()
                .withSecretId(secretName);
        GetSecretValueResult getSecretValueResult = null;

        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (DecryptionFailureException e) {
            log.error(e);
            throw e;
        } catch (InternalServiceErrorException e) {
            log.error(e);
            throw e;
        } catch (InvalidParameterException e) {
            log.error(e);
            throw e;
        } catch (InvalidRequestException e) {
            log.error(e);
            throw e;
        } catch (ResourceNotFoundException e) {
            log.error(e);
            throw e;
        }

        // Decrypts secret using the associated KMS CMK.
        // Depending on whether the secret is a string or binary, one of these fields will be populated.
        if (getSecretValueResult.getSecretString() != null) {
            secret = getSecretValueResult.getSecretString();
        }
        else {
            decodedBinarySecret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
        }
        return secret != null ?
                secret.substring(secret.indexOf(":") + 2,secret.length() - 2) :
                decodedBinarySecret.substring(decodedBinarySecret.indexOf(":") + 2,decodedBinarySecret.length() - 2);
    }

    private String xorMessage(String message, String key) {
        try {
            if (message == null || key == null) {return null;}

            char[] keys = key.toCharArray();
            char[] mesg = message.toCharArray();

            int ml = mesg.length;
            int kl = keys.length;
            char[] newmsg = new char[ml];

            for (int i = 0; i < ml; i++) {
                newmsg[i] = (char)(mesg[i] ^ keys[i % kl]);
            }
            return new String(newmsg);
        } catch (Exception e) {
            return null;
        }
    }
}
