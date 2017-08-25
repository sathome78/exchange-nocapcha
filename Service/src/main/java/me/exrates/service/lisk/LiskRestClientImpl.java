package me.exrates.service.lisk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import me.exrates.model.dto.merchants.lisk.LiskAccount;
import me.exrates.model.dto.merchants.lisk.LiskOpenAccountDto;
import me.exrates.model.dto.merchants.lisk.LiskSendTxDto;
import me.exrates.model.dto.merchants.lisk.LiskTransaction;
import me.exrates.service.exception.LiskRestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Service
@PropertySource("classpath:/merchants/lisk.properties")
public class LiskRestClientImpl implements LiskRestClient {

    @Autowired
    private RestTemplate restTemplate;

    private @Value("${lisk.node.host}") String host;
    private @Value("${lisk.node.port}") String port;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final String newAccountEndpoint = "/api/accounts/open";
    private final String getAccountByAddressEndpoint = "/api/accounts";
    private final String getTransactionsEndpoint = "/api/transactions";
    private final String getTransactionByIdEndpoint = "/api/transactions/get";
    private final String sendTransactionEndpoint = "/api/transactions";

    private final int MAX_LIMIT_OF_TRANSACTIONS = 1000;

    @Override
    public LiskTransaction getTransactionById(String txId) {
            String response = restTemplate.getForObject(getURIWithParams(getTransactionByIdEndpoint, Collections.singletonMap("id", txId)),
                    String.class);

            return extractObjectFromResponse(response, "transaction", LiskTransaction.class);
    }

    @Override
    public List<LiskTransaction> getTransactionsByRecipient(String recipientAddress) {
            Map<String, String> params = new HashMap<String, String>() {{
               put("recipientId", recipientAddress);
               put("orderBy", "timestamp:desc");
            }};
            String response = restTemplate.getForObject(getURIWithParams(getTransactionsEndpoint, params),
                    String.class);

            return extractListFromResponse(response, "transactions", LiskTransaction.class);
    }

    public List<LiskTransaction> getAllTransactionsAwaitingConfirmationByRecipient(String recipientAddress, int minConfirmations) {
        List<LiskTransaction> result = null;
        int offset = 0;
        Map<String, String> params = new HashMap<String, String>() {{
            put("recipientId", recipientAddress);
            put("limit", String.valueOf(MAX_LIMIT_OF_TRANSACTIONS));
            put("offset", String.valueOf(offset));
            put("orderBy", "confirmations:desc");
        }};
        String response = restTemplate.getForObject(getURIWithParams(getTransactionsEndpoint, params),
                String.class);
        return Collections.emptyList();
    }



    @Override
    public String sendTransaction(LiskSendTxDto dto) {
            ResponseEntity<String> response = restTemplate.exchange(absoluteURI(sendTransactionEndpoint), HttpMethod.PUT, new HttpEntity<>(dto), String.class);
            return extractTargetNodeFromLiskResponse(response.getBody(), "transactionId", JsonNodeType.STRING).textValue();
    }

    @Override
    public LiskAccount createAccount(String secret) {
        LiskOpenAccountDto dto = new LiskOpenAccountDto();
        dto.setSecret(secret);
            ResponseEntity<String> response = restTemplate.exchange(absoluteURI(newAccountEndpoint), HttpMethod.POST, new HttpEntity<>(dto), String.class);
            return extractObjectFromResponse(response.getBody(), "account", LiskAccount.class);

    }

    @Override
    public LiskAccount getAccountByAddress(String address) {
        String response = restTemplate.getForObject(getURIWithParams(getAccountByAddressEndpoint, Collections.singletonMap("address", address)),
                String.class);
        return extractObjectFromResponse(response, "account", LiskAccount.class);
    }

    private <T> T extractObjectFromResponse(String responseBody, String targetFieldName, Class<T> type)  {
        try {
            return objectMapper.treeToValue(extractTargetNodeFromLiskResponse(responseBody, targetFieldName, JsonNodeType.OBJECT), type);
        } catch (JsonProcessingException e) {
            throw new LiskRestException(e.getMessage());
        }
    }

    private <T> List<T> extractListFromResponse(String responseBody, String targetFieldName, Class<T> listElementType)  {
        try {
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, listElementType);
            String array = extractTargetNodeFromLiskResponse(responseBody, targetFieldName, JsonNodeType.ARRAY).toString();
            return objectMapper.readValue(array, type);
        } catch (IOException e) {
            throw new LiskRestException(e.getMessage());
        }
    }

    private JsonNode extractTargetNodeFromLiskResponse(String responseBody, String targetFieldName, JsonNodeType targetNodeType)  {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode successNode = getAndValidateJsonNode("success", root, JsonNode::isBoolean);
            boolean success = successNode.booleanValue();
            if (success) {
                return getAndValidateJsonNode(targetFieldName, root, jsonNode -> jsonNode.getNodeType() == targetNodeType);
            } else {
                JsonNode error = getAndValidateJsonNode("error", root, JsonNode::isTextual);
                throw new LiskRestException(String.format("API error: %s", error.textValue()));
            }
        } catch (IOException e) {
            throw new LiskRestException(e.getMessage());
        }
    }

    private JsonNode getAndValidateJsonNode(String fieldName, JsonNode parent, Predicate<JsonNode> validator) {
        JsonNode target = parent.get(fieldName);
        if (target == null || !validator.test(target)) {
            throw new LiskRestException(String.format("Field not found: %s", fieldName));
        }
        return target;
    }

    private URI getURIWithParams(String endpoint, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(absoluteURI(endpoint));
        params.forEach(builder::queryParam);
        return builder.build().encode().toUri();
    }


    private String absoluteURI(String relativeURI) {
        return String.join("", host, ":", port, relativeURI);
    }


}
