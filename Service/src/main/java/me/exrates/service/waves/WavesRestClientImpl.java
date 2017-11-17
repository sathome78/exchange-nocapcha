package me.exrates.service.waves;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.dto.merchants.waves.WavesAddress;
import me.exrates.model.dto.merchants.waves.WavesPayment;
import me.exrates.model.dto.merchants.waves.WavesTransaction;
import me.exrates.service.exception.WavesRestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@PropertySource("classpath:/merchants/waves.properties")
public class WavesRestClientImpl implements WavesRestClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private @Value("${waves.rest.host}") String host;
    private @Value("${waves.rest.port}") String port;
    private @Value("${waves.rest.api.key}") String apiKey;

    private final String API_KEY_HEADER_NAME = "api_key";

    private final int MAX_TRANSACTION_QUERY_LIMIT = 50;

    private final String newAddressEndpoint = "/addresses";
    private final String transferCostsEndpoint = "/assets/transfer";
    private final String accountTransactionsEndpoint = "/transactions/address/{address}/limit/{limit}";
    private final String transactionByIdEndpoint = "/transactions/info/{id}";

    @Override
    public String generateNewAddress() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(API_KEY_HEADER_NAME, apiKey);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        return restTemplate.postForObject(generateBaseUrl() + newAddressEndpoint, entity, WavesAddress.class).getAddress();
    }


    @Override
    public String transferCosts(WavesPayment payment) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(API_KEY_HEADER_NAME, apiKey);
        HttpEntity<WavesPayment> entity = new HttpEntity<>(payment, headers);
        return restTemplate.postForObject(generateBaseUrl() + transferCostsEndpoint, entity, WavesTransaction.class).getId();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<WavesTransaction> getTransactionsForAddress(String address) {
        Map<String, Object> params = new HashMap<>();
        params.put("address", address);
        params.put("limit", MAX_TRANSACTION_QUERY_LIMIT);
        return restTemplate.getForObject(generateBaseUrl() + accountTransactionsEndpoint, List.class, params);
    }


    @Override
    public WavesTransaction getTransactionById(String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        return restTemplate.getForObject(generateBaseUrl() + transactionByIdEndpoint, WavesTransaction.class, params);
    }





    /*private <T> T extractObjectFromResponse(String responseBody, String targetFieldName, Class<T> targetType) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

        } catch (IOException e) {
            throw new WavesRestException(e);
        }
    }
*/








    private String generateBaseUrl() {
        return String.join(":", host, port);
    }







}
