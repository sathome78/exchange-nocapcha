package me.exrates.controller.openAPI;

import com.google.common.collect.ImmutableList;
import me.exrates.model.HmacSignature;
import me.exrates.security.filter.OpenApiAuthenticationFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;

public class OpenApiCommonTest {

    protected RequestBuilder getOpenApiRequestBuilder(URI uri, HttpMethod method) {
        if (method.equals(HttpMethod.GET)) {
           return MockMvcRequestBuilders.get(uri).headers(createHeaders(method.toString(), uri.getPath()));
        } else if (method.equals(HttpMethod.POST)) {
            return MockMvcRequestBuilders.get(uri).headers(createHeaders(method.toString(), uri.getPath()));
        }
        throw new UnsupportedOperationException(String.format("Method: %s not supported", method.toString()));
    }

    private HttpHeaders createHeaders(String endPoint, String method) {
        Long timestamp = System.currentTimeMillis();
        HmacSignature signature = new HmacSignature.Builder()
                .algorithm("HmacSHA256")
                .delimiter("|")
                .apiSecret("PRIVATE_KEY")
                .endpoint(endPoint)
                .requestMethod(method)
                .timestamp(timestamp)
                .publicKey("PUBLIC_KEY")
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.put(OpenApiAuthenticationFilter.HEADER_PUBLIC_KEY, ImmutableList.of("PUBLIC_KEY"));
        headers.put(OpenApiAuthenticationFilter.HEADER_SIGNATURE, ImmutableList.of(signature.getSignatureHexString()));
        headers.put(OpenApiAuthenticationFilter.HEADER_TIMESTAMP, ImmutableList.of(Long.toString(timestamp)));
        return headers;
    }
}
