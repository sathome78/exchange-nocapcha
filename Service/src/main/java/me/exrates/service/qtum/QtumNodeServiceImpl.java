package me.exrates.service.qtum;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.merchants.qtum.*;
import me.exrates.service.exception.QtumApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Log4j2(topic = "qtum_log")
@PropertySource("classpath:/merchants/qtum.properties")
public class QtumNodeServiceImpl implements QtumNodeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private @Value("${qtum.node.endpoint}") String endpoint;
    private @Value("${qtum.node.user}") String user;
    private @Value("${qtum.node.password}") String password;
    private @Value("${qtum.wallet.password}") String walletPassphrase;
    private @Value("${qtum.backup.folder}") String backupDestination;

    @PostConstruct
    private void init() {
        restTemplate.getInterceptors().add(
                new BasicAuthorizationInterceptor(user, password));
    }

    @Override
    public String getNewAddress() {
        return invokeJsonRpcMethod("getnewaddress", Collections.emptyList(), new TypeReference<QtumJsonRpcResponse<String>>() {});
    }

    @Override
    public String getBlockHash(Integer height) {
        return invokeJsonRpcMethod("getblockhash", Arrays.asList(height), new TypeReference<QtumJsonRpcResponse<String>>() {});
    }

    @Override
    public Block getBlock(String hash) {
        return invokeJsonRpcMethod("getblock", Arrays.asList(hash), new TypeReference<QtumJsonRpcResponse<Block>>() {});
    }

    @Override
    public Optional<QtumListTransactions> listSinceBlock(String blockHash) {
        try {
            return Optional.of(invokeJsonRpcMethod("listsinceblock", Arrays.asList(blockHash), new TypeReference<QtumJsonRpcResponse<QtumListTransactions>>() {}));
        }catch (Exception e){
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public void setWalletPassphrase() {
        invokeJsonRpcMethod("walletpassphrase", Arrays.asList(walletPassphrase, 1), new TypeReference<QtumJsonRpcResponse<String>>() {});
    }

    @Override
    public BigDecimal getBalance() {
        return invokeJsonRpcMethod("getbalance", Collections.emptyList(), new TypeReference<QtumJsonRpcResponse<BigDecimal>>() {});
    }

    @Override
    public void transfer(String mainAddress, BigDecimal amount) {
        invokeJsonRpcMethod("sendtoaddress", Arrays.asList(mainAddress, amount), new TypeReference<QtumJsonRpcResponse<String>>() {});
    }

    @Override
    public void backupWallet() {
        invokeJsonRpcMethod("backupwallet", Arrays.asList(backupDestination), new TypeReference<QtumJsonRpcResponse<String>>() {});
    }

    private <T> T invokeJsonRpcMethod(String methodName, List<Object> args, TypeReference<QtumJsonRpcResponse<T>> typeReference) {
        QtumJsonRpcRequest request = new QtumJsonRpcRequest();
        request.setMethod(methodName);
        request.setParams(args);
        return getQtumJsonRpcResponse(request, typeReference);
    }

    private <T> T getQtumJsonRpcResponse(QtumJsonRpcRequest request, TypeReference<QtumJsonRpcResponse<T>> typeReference) {
        String responseString = restTemplate.postForObject(endpoint, request, String.class);
        try {
            QtumJsonRpcResponse<T> response = objectMapper.readValue(responseString,  typeReference);
            if (response.getError() != null) {
                log.error(response.getError());
                throw new QtumApiException(response.getError().getCode(), response.getError().getMessage());
            }
            if (response.getResult() == null && !request.getMethod().equals("walletpassphrase")) {
                throw new QtumApiException("No result found in response");
            }
            return response.getResult();
        } catch (IOException e) {
            throw new QtumApiException(e);
        }
    }
}
