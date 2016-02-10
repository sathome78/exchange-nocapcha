package me.exrates;

import com.squareup.okhttp.MediaType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public final class YandexMoneyProperties extends BaseProperties {

    private static final String PREFIX = "yandexmoney.";

    public YandexMoneyProperties(String resource) {
        super(resource);
    }

    public String clientId() {
        return get(PREFIX+"clientId");
    }

    public String redirectURI() {
        return get(PREFIX+"redirectURI");
    }

    public String responseType() {
        return get(PREFIX+"responseType");
    }

    public MediaType mediaType() {
        return MediaType.parse(get(PREFIX+"mediaType"));
    }

    public String companyYandexMoneyWalletId() {
        return get(PREFIX+"companyWalletId");
    }

    public BigDecimal yandexMoneyP2PCommission() {
        return BigDecimal.valueOf(Double.valueOf(get(PREFIX+"commissionP2P")));
    }

    public String accessToken(){
        return get(PREFIX+"token");
    }
}