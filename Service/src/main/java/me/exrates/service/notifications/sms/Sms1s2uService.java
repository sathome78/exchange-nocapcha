package me.exrates.service.notifications.sms;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.LookupResponseDto;
import me.exrates.service.exception.*;
import me.exrates.service.notifications.Subscribable;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.URI;
import java.nio.charset.Charset;
import java.util.Random;

import static rx.internal.operators.NotificationLite.isError;

/**
 * Created by Maks on 09.10.2017.
 */
@PropertySource("classpath:telegram_bot.properties")
@Log4j2(topic = "message_notify")
@Service
public class Sms1s2uService {

    @Autowired
    private RestTemplate restTemplate;


    private @Value("${1s2usms.key}") String key;
    private @Value("${1s2usms.hlr.url}") String hlrUrl;
    private @Value("${1s2usms.sms.url}") String smsUrl;
    private @Value("${1s2usms.username}") String userName;
    private @Value("${1s2usms.password}") String password;
    private final static String SENDER = "Exrates";


    public void sendMessage(long contact, String message) {
        URI uri = UriComponentsBuilder
                .fromUriString(smsUrl)
                .queryParam("username", userName)
                .queryParam("password", password)
                .queryParam("mno", contact)
                .queryParam("msg", StringEscapeUtils.escapeJava(message).replaceAll("\\\\u", ""))
                .queryParam("Sid", SENDER)
                .queryParam("fl", 0)
                .queryParam("mt", 1)
                .queryParam("ipcl", "127.0.0.1")
                .build().toUri();
        log.debug("uri {}", uri.toString());
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
        log.debug("response {}", response.toString());
        if (isError(response)) {
            throw new MessageUndeliweredException();
        }
        long res = Long.parseLong(response.getBody());
        if (res == 0020) {
            throw new InsuficcienceServiceBalanceException();
        }
        if (res < 9999) {
            throw new MessageUndeliweredException();
        }
    }

    public LookupResponseDto getLookup(long contact) {
        URI uri = UriComponentsBuilder
                .fromUriString(hlrUrl)
                .queryParam("key", key)
                .queryParam("ref", generateRef())
                .queryParam("msisdn", contact)
                .build().toUri();
        log.debug("uri {}", uri.toString());
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, null, String.class);
        log.debug("response {}", response.toString());
        if (isError(response)) {
            throw new ServiceUnavailableException();
        }
        log.debug("resp {}", response);
        if (NumberUtils.isDigits(response.getBody())) {
            resolveError(Integer.parseInt(response.getBody()));
        }
        JSONObject object = new JSONObject(response).getJSONArray("result").getJSONObject(0);
        return LookupResponseDto.builder()
                .country(object.getString("country"))
                .operator("operator")
                .isOperable(object.getString("errcode").equals("000"))
                .build();


    }


    private long generateRef() {
        return 100000000 + new Random().nextInt(100000000);
    }

    private void resolveError(int errCode) {
        switch (errCode) {
            case 01 : {
                throw new InvalidRefNumberException();
            }
            case 02 : {
                throw new InvalidContactFormatException();
            }
            case 03 : {
                throw new InsuficcienceServiceBalanceException();
            }
            default: {
                throw new ServiceUnavailableException();
            }


        }
    }

}
