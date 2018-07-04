package me.exrates.service.decred;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.HandshakeResponse;
import javax.xml.bind.DatatypeConverter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@PropertySource("classpath:/merchants/decred.properties")
public class DecredWSConfig extends ClientEndpointConfig.Configurator {

    private ClientEndpointConfig clientConfig;

    private @Value("${decred.rpcuser}") String username;
    private @Value("${decred.rpcpass}") String password;


    public DecredWSConfig() {
        super();
    }


    @Override
    public void beforeRequest(Map<String, List<String>> headers) {
        headers.put("Authorization", Collections.singletonList("Basic " + DatatypeConverter.printBase64Binary(String.join("", username, ":", password).getBytes())));
        super.beforeRequest(headers);
    }

    @Override
    public void afterResponse(HandshakeResponse hr) {
        super.afterResponse(hr);
    }
}
