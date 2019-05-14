package me.exrates.service.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.exrates.model.enums.OperationType;
import me.exrates.service.AlgorithmService;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static com.yandex.money.api.utils.Numbers.bytesToHex;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class AlgorithmServiceImpl implements AlgorithmService {

    private static final String DEFAULT_ENCODING = "UTF-8";
    private static BASE64Encoder enc = new BASE64Encoder();
    private static BASE64Decoder dec = new BASE64Decoder();
    // TODO: save key to the base
    private static String key = "ell+cTISVmFhYHxXJ1JfByc9QVJ/dlN8aV44emVYCwVWeUMGYQF7Y28HeTQ0BXUGJ3B/dG1ad3hynN1h8IERJVQ==";
    private RestTemplate restTemplate;

    private static final int decimalPlaces = 8;
    private static final BigDecimal HUNDRED = new BigDecimal(100L).setScale(decimalPlaces, ROUND_HALF_UP);
    private static final BigDecimal SATOSHI = new BigDecimal(100_000_000L);

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    public AlgorithmServiceImpl (){
        restTemplate = new RestTemplate();
        key = getKeyFromVault();
    }

    private String getKeyFromVault(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();

        map.add("SecretId", "...........");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<Response> response = restTemplate.postForEntity(
                "http://......../api/v1/sendgapicoin", request , Response.class);

        return response.getBody().secretString;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Response {

        @JsonProperty("SecretString")
        String secretString;
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
        try {
            txt = new String(dec.decodeBuffer(text), DEFAULT_ENCODING);
        } catch (IOException e) {
            return null;
        }
        return xorMessage(txt, key);
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

//    public static void main(String[] args){
//    AlgorithmService algorithmService = new AlgorithmServiceImpl();
//    String temp = algorithmService.encodeByKey("0576de547126dcfade59540419a6013b01744023967cbb8af12352690f36b396");
//        System.out.println(temp);
//        System.out.println(algorithmService.decodeByKey(temp));
//    }
}
