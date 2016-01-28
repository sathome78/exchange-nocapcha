package me.exrates;

import com.squareup.okhttp.MediaType;

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
}