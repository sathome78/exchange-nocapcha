package me.exrates.service.apollo;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;

public class Test {

    private static RestTemplate restTemplate = new RestTemplate();
    private static final String SERVER_URL = "http://172.10.13.74:7876/apl";

    public static void main(String[] args) {
        /*System.out.println(getTransactions("APL-LLWA-PLU5-KCT8-D5DW5", 0));*/
        /*System.out.println(URLEncoder.encode("requestType=getBlockchainTransactions&account=APL-LLWA-PLU5-KCT8-D5DW5&executedOnly=true&includePhasingResult=true", "UTF-8"));*/
        System.out.println(new BigDecimal(1).multiply(new BigDecimal(Math.pow(10, -8))).setScale(8, RoundingMode.HALF_DOWN).toPlainString());
    }


    public static String getTransactions(String address, long lastBlock) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(SERVER_URL)
                .queryParam("requestType", "getBlockchainTransactions")
                .queryParam("nonPhasedOnly", true)
                .queryParam("type", 0)
                .queryParam("subtype", 0)
               /* .queryParam("withMessage", true)*/
                .queryParam("account", address);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                String.class).getBody();
    }
}
