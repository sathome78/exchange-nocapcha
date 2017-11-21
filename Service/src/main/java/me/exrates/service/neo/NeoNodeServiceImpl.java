package me.exrates.service.neo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.merchants.neo.*;
import me.exrates.service.exception.NeoApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2(topic = "neo_log")
@PropertySource("classpath:/merchants/neo.properties")
public class NeoNodeServiceImpl implements NeoNodeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private @Value("${neo.node.endpoint}") String endpoint;




    @Override
    public String getNewAddress() {
        return invokeJsonRpcMethod("getnewaddress", Collections.emptyList(), new TypeReference<NeoJsonRpcResponse<String>>() {});
    }

    @Override
    public Integer getBlockCount() {
        return invokeJsonRpcMethod("getblockcount", Collections.emptyList(), new TypeReference<NeoJsonRpcResponse<Integer>>() {});
    }

    @Override
    public Block getBlock(Integer height) {
        return invokeJsonRpcMethod("getblock", Arrays.asList(height, 1), new TypeReference<NeoJsonRpcResponse<Block>>() {});
    }

    @Override
    public NeoTransaction getTransactionById(String txId) {
        return invokeJsonRpcMethod("getrawtransaction", Arrays.asList(txId, 1), new TypeReference<NeoJsonRpcResponse<NeoTransaction>>() {});
    }

    @Override
    public NeoTransaction sendToAddress(NeoAsset asset, String address, BigDecimal amount) {
        return invokeJsonRpcMethod("sendtoaddress", Arrays.asList(asset.getId(), address, amount), new TypeReference<NeoJsonRpcResponse<NeoTransaction>>() {});
    }





    private <T> T invokeJsonRpcMethod(String methodName, List<Object> args, TypeReference<NeoJsonRpcResponse<T>> typeReference) {
        NeoJsonRpcRequest request = new NeoJsonRpcRequest();
        request.setMethod(methodName);
        request.setParams(args);
        return getNeoJsonRpcResponse(request, typeReference);
    }

    @SuppressWarnings("unchecked")
    private <T> T getNeoJsonRpcResponse(NeoJsonRpcRequest request, TypeReference<NeoJsonRpcResponse<T>> typeReference) {
        String responseString = restTemplate.postForObject(endpoint, request, String.class);
        try {
            NeoJsonRpcResponse<T> response = objectMapper.readValue(responseString,  typeReference);
            if (response.getError() != null) {
                log.error(response.getError());
                throw new NeoApiException(response.getError().getCode(), response.getError().getMessage());
            }
            if (response.getResult() == null) {
                throw new NeoApiException("No result found in response");
            }
            return response.getResult();
        } catch (IOException e) {
            throw new NeoApiException(e);
        }


    }

    public static void main(String[] args) throws IOException {



       /* RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        NeoJsonRpcRequest request = new NeoJsonRpcRequest();
        *//*request.setMethod("getnewaddress");
        request.setParams(Collections.emptyList());
        String response = restTemplate.postForObject("http://127.0.0.1:20332", request, String.class);
        System.out.println(response);

        System.out.println(objectMapper.readValue(response, NeoJsonRpcResponse.class));*//*

        request.setMethod("getblock");
        request.setParams(Arrays.asList(798134, 1));
        System.out.println(getNeoJsonRpcResponse(restTemplate, objectMapper, "http://127.0.0.1:20332", request, new TypeReference<NeoJsonRpcResponse<Block>>() {}));
*/
        /*String responseString = restTemplate.postForObject("http://127.0.0.1:20332", request, String.class);
        System.out.println(responseString);
        NeoJsonRpcResponse<Block> response = objectMapper.readValue(responseString, new TypeReference<NeoJsonRpcResponse<Block>>() {});
        System.out.println(response);*/

        /*request.setMethod("getrawtransaction");
        request.setParams(Arrays.asList("0c41d4278a2f1ee537db3647f3a61f6b07b224f185d979692a33a8ee7ecf465d", 1));

        String responseString = restTemplate.postForObject("http://127.0.0.1:20332", request, String.class);
        System.out.println(responseString);
        NeoJsonRpcResponse<Block> response = objectMapper.readValue(responseString, new TypeReference<NeoJsonRpcResponse<NeoTransaction>>() {});
        System.out.println(response);
*/
       /* request.setMethod("sendtoaddress");
        request.setParams(Arrays.asList("602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7", "AJgnT4KVw5VMt2bbwqJ3cawFrjpCLb64PQ", new BigDecimal(0.001)));

        String responseString = restTemplate.postForObject("http://127.0.0.1:20332", request, String.class);
        System.out.println(responseString);
        NeoJsonRpcResponse<Block> response = objectMapper.readValue(responseString, new TypeReference<NeoJsonRpcResponse<NeoTransaction>>() {});
        System.out.println(response);*/
        /*request.setMethod("getblockcount");
        request.setParams(Collections.emptyList());
        String responseString = restTemplate.postForObject("http://127.0.0.1:20332", request, String.class);
        System.out.println(responseString);
        NeoJsonRpcResponse<Integer> response = objectMapper.readValue(responseString, new TypeReference<NeoJsonRpcResponse<Integer>>() {});
        System.out.println(response);*/



    }

   /* private static <T> T invokeJsonRpcMethod(String methodName, List<Object> args, Class<T> resultType) {

        return getNeoJsonRpcResponse(request, resultType);
    }*/

  /*  @SuppressWarnings("unchecked")
    private static <T> T getNeoJsonRpcResponse(RestTemplate restTemplate, ObjectMapper objectMapper,
                                               String endpoint, NeoJsonRpcRequest request, TypeReference<NeoJsonRpcResponse<T>> typeReference) {
        String responseString = restTemplate.postForObject(endpoint, request, String.class);
        try {
            NeoJsonRpcResponse<T> response = objectMapper.readValue(responseString, typeReference);
            if (response.getError() != null) {
                throw new NeoApiException(response.getError().getMessage());
            }
            if (response.getResult() == null) {
                throw new NeoApiException("No result found in response");
            }
            return response.getResult();
        } catch (IOException e) {
            throw new NeoApiException("Could not parse response");
        }


    }*/


}
