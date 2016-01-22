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

    public String getClientId() {
        return get(PREFIX+"clientId");
    }

    public String getRedirectURI() {
        return get(PREFIX+"redirectURI");
    }

    public String getResponseType() {
        return get(PREFIX+"responseType");
    }

    public MediaType getMediaType() {
        return MediaType.parse(get(PREFIX+"mediaType"));
    }
}