package me.exrates.model.dto.mobileApiDto;

import java.net.HttpCookie;
import java.util.List;

/**
 * Created by OLEG on 20.10.2016.
 */
public class ResponseContainer {
    private String body;
    private List<HttpCookie> cookies;

    public ResponseContainer() {
    }

    public ResponseContainer(String body, List<HttpCookie> cookies) {
        this.body = body;
        this.cookies = cookies;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<HttpCookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<HttpCookie> cookies) {
        this.cookies = cookies;
    }
}
