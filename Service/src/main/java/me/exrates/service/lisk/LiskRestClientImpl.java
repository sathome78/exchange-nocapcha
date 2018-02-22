package me.exrates.service.lisk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.merchants.lisk.LiskAccount;
import me.exrates.model.dto.merchants.lisk.LiskOpenAccountDto;
import me.exrates.model.dto.merchants.lisk.LiskSendTxDto;
import me.exrates.model.dto.merchants.lisk.LiskTransaction;
import me.exrates.service.exception.LiskRestException;
import me.exrates.service.util.RestApiUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.function.Predicate;

@Log4j2(topic = "lisk_log")
@Service
@Scope("prototype")
public class LiskRestClientImpl implements LiskRestClient {

    @Autowired
    private RestTemplate restTemplate;

    private String host;
    private String port;
    private String sortingPrefix;
    private int maxTransactionQueryLimit;
    private JsonNodeType countNodeType;


    private ObjectMapper objectMapper = new ObjectMapper();

    private final String newAccountEndpoint = "/api/accounts/open";
    private final String getAccountByAddressEndpoint = "/api/accounts";
    private final String getTransactionsEndpoint = "/api/transactions";
    private final String getTransactionByIdEndpoint = "/api/transactions/get";
    private final String sendTransactionEndpoint = "/api/transactions";
    private final String getFeeEndpoint = "/api/blocks/getFee";



    @Override
    public void initClient(String propertySource) {
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertySource));
            this.host = props.getProperty("lisk.node.host");
            this.port = props.getProperty("lisk.node.port");
            this.sortingPrefix = props.getProperty("lisk.tx.sort.prefix");
            this.maxTransactionQueryLimit = Integer.parseInt(props.getProperty("lisk.tx.queryLimit"));
            this.countNodeType = JsonNodeType.valueOf(props.getProperty("lisk.tx.count.nodeType"));

        } catch (IOException e) {
            log.error(e);
        }
    }

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
               put("orderBy", sortingPrefix + "timestamp:asc");
            }};
            String response = restTemplate.getForObject(getURIWithParams(getTransactionsEndpoint, params),
                    String.class);

            return extractListFromResponse(response, "transactions", LiskTransaction.class);
    }

    @Override
    public List<LiskTransaction> getAllTransactionsByRecipient(String recipientAddress, int offset) {
        log.info("Retrieving transactions: address {} offset {}", recipientAddress, offset);
        List<LiskTransaction> result = new ArrayList<>();
        int newOffset = offset;
        int count;
        do {
            String response = sendGetTransactionsRequest(recipientAddress, newOffset);
            count = Integer.parseInt(extractTargetNodeFromLiskResponse(response, "count", countNodeType).asText());
            result.addAll(extractListFromResponse(response, "transactions", LiskTransaction.class));
            newOffset += result.size();
        } while (newOffset < count);
        return result;
    }

    private String sendGetTransactionsRequest(String recipientAddress, int offset) {
        Map<String, String> params = new HashMap<String, String>() {{
            put("recipientId", recipientAddress);
            put("limit", String.valueOf(maxTransactionQueryLimit));
            put("offset", String.valueOf(offset));
            put("orderBy", sortingPrefix + "timestamp:asc");
        }};
        URI targetURI = getURIWithParams(getTransactionsEndpoint, params);
        return restTemplate.getForObject(targetURI, String.class);
    }

    @Override
    public Long getFee() {
        String response = restTemplate.getForObject(absoluteURI(getFeeEndpoint), String.class);
        return extractTargetNodeFromLiskResponse(response, "fee", JsonNodeType.NUMBER).longValue();
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
        if (target == null) {
            throw new LiskRestException(String.format("Field not found: %s", fieldName));
        } else if (!validator.test(target)) {
            throw new LiskRestException(String.format("Field %s is not in appropriate format: %s", fieldName, target.getNodeType()));
        }
        return target;
    }

    private URI getURIWithParams(String endpoint, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(absoluteURI(endpoint));
        params.forEach(builder::queryParam);
        return builder.build().encode().toUri();
    }


    private String absoluteURI(String relativeURI) {
        return RestApiUtils.constructAbsoluteURI(host, port, relativeURI);
    }


}
