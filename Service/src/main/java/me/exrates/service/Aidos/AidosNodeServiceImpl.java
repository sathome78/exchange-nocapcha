package me.exrates.service.Aidos;


import lombok.extern.log4j.Log4j2;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;

@Log4j2
@Service
@PropertySource("classpath:/merchants/adk.properties")
public class AidosNodeServiceImpl implements AidosNodeService {

    private @Value("${node.adk.rpc.host}")String nodeHost;
    private @Value("${node.adk.rpc.user}")String rpcUser;
    private @Value("${node.adk.rpc.password}")String rpcPassword;
    private URI nodeURI;

    private static final String[] EMPTY_PARAMS = {};

    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(rpcUser, rpcPassword));
        try {
            nodeURI = new URI(nodeHost);
        } catch (URISyntaxException e) {
            log.error("wrong ADK url");
        }
    }

    @Override
    public String generateNewAddress() {
        System.out.println();
        RequestEntity requestEntity = RequestEntity.post(nodeURI).body(createRequestBody("getnewaddress", "1", EMPTY_PARAMS).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getString("result");
    }


    private JSONObject createRequestBody(String method, String id, String[] params) {
        return new JSONObject() {{
            put("method", method);
            put("id", id);
            put("params", new JSONArray(params));
        }};
    }

    private JSONObject makeRequest(RequestEntity requestEntity) {
        return new JSONObject(restTemplate.exchange(requestEntity, String.class).getBody());
    }

}
