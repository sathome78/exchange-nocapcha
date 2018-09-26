package me.exrates.service.aidos;


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
import java.math.BigDecimal;
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
        RequestEntity requestEntity = RequestEntity.post(nodeURI).body(createRequestBody("getnewaddress", "1", EMPTY_PARAMS).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getString("result");
    }

    @Override
    public BigDecimal getBalance() {
        RequestEntity requestEntity = RequestEntity.post(nodeURI).body(createRequestBody("getbalance", "1", EMPTY_PARAMS).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getBigDecimal("result");
    }

    @Override
    public BigDecimal getTransaction(String txId) {
        RequestEntity requestEntity = RequestEntity.post(nodeURI).body(createRequestBody("gettransaction", "1", new Object[]{txId}).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getBigDecimal("result");
    }

    @Override
    public JSONArray getAllTransactions(Integer count, Integer from) {
        Object[] args = new Object[3];
        args[0] = "";
        if (count != null && from != null) {
            args[1] = count;
            args[2] = from;
        }
        RequestEntity requestEntity = RequestEntity.post(nodeURI).body(createRequestBody("listtransactions", "1", args).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getJSONArray("result");
    }


    @Override
    public JSONArray getAllTransactions() {
       return getAllTransactions(null ,null);
    }


    private JSONObject createRequestBody(String method, String id, Object[] params) {
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
