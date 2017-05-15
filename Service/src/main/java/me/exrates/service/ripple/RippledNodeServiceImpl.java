package me.exrates.service.ripple;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.RippleAccount;
import me.exrates.model.dto.RippleTransaction;
import me.exrates.service.exception.RippleCheckConsensusException;
import me.exrates.service.util.RestUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by maks on 05.05.2017.
 */
@Log4j2
@Service
@PropertySource("classpath:/merchants/ripple.properties")
public class RippledNodeServiceImpl implements RippledNodeService {

    @Autowired
    private RestTemplate restTemplate;
    private @Value("${ripple.rippled.rpcUrl}") String rpcUrl;

    private static final String SIGN_RPC = "{\n" +
            "                     \"method\": \"sign\",\n" +
            "                     \"params\": [\n" +
            "                         {\n" +
            "                             \"offline\": false,\n" +
            "                             \"secret\": \"%s\",\n" +
            "                             \"tx_json\": {\n" +
            "                                 \"Account\": \"%s\",\n" +
            "                                 \"Amount\":  \"%s\",\n" +
            "                                 \"Destination\": \"%s\",\n" +
            "                                 \"TransactionType\": \"Payment\"\n" +
            "                             },\n" +
            "                             \"fee_mult_max\": 1000\n" +
            "                         }\n" +
            "                     ]\n" +
            "                 }";

    private static final String SUBMIT_TRANSACTION_RPC = "{\n" +
            "                     \"method\": \"submit\",\n" +
            "                     \"params\": [\n" +
            "                         {\n" +
            "                             \"tx_blob\": \"%s\"\n" +
            "                         }\n" +
            "                     ]\n" +
            "                 }";

    private final static String GET_TRANSACTION_RPC = "{\"method\": \"tx\",\n" +
            "                     \"params\": [\n" +
            "                         {\n" +
            "                             \"transaction\": \"%s\",\n" +
            "                             \"binary\": false\n" +
            "                         }\n" +
            "                     ]}";

    private static final String GET_ACCOUNT_RPC = "{\n" +
            "                     \"method\": \"account_info\",\n" +
            "                     \"params\": [\n" +
            "                         {\n" +
            "                             \"account\": \"%s\",\n" +
            "                             \"strict\": true,\n" +
            "                             \"ledger_index\": \"current\",\n" +
            "                             \"queue\": true\n" +
            "                         }\n" +
            "                     ]\n" +
            "                 }";

    private static final String WALLET_PORPOSE_RPC = "{\"method\": \"wallet_propose\",\n" +
            "                                           \"params\": [\n" +
            "\n" +
            "                                           ]}";


    @Override
    public void signTransaction(RippleTransaction transaction) {
        String requestBody = String.format(SIGN_RPC, transaction.getIssuerSecret(), transaction.getIssuerAddress(),
                transaction.getSendAmount(), transaction.getDestinationAddress());
        ResponseEntity<String> response = restTemplate.postForEntity(rpcUrl, requestBody, String.class);
        if (RestUtil.isError(response.getStatusCode())) {
            throw new RuntimeException("cant generate new address");
        }
        String blop = new JSONObject(response.getBody()).getString("blop");
        transaction.setBlop(blop);
        transaction.setTxSigned(true);
    }

    @Override
    public void submitTransaction(RippleTransaction transaction) {
        String requestBody = String.format(SUBMIT_TRANSACTION_RPC, transaction.getBlop());
        ResponseEntity<String> response = restTemplate.postForEntity(rpcUrl, requestBody, String.class);
        if (RestUtil.isError(response.getStatusCode())) {
            throw new RuntimeException("can't submit transaction");
        }
    }

    @Override
    public boolean checkSendedTransactionConsensus(String txHash) {
        String requestBody = String.format(GET_TRANSACTION_RPC, txHash);
        ResponseEntity<String> response = restTemplate.postForEntity(rpcUrl, requestBody, String.class);
        if (RestUtil.isError(response.getStatusCode()) || response.getBody().contains("error")) {
            log.error("error checking transaction {}", response.getBody());
            throw new RippleCheckConsensusException(response.getBody());
        }
        JSONObject responseBody = new JSONObject(response.getBody()).getJSONObject("result");
        return responseBody.getBoolean("validated");
    }

    @Override
    public JSONObject getAccountInfo(String accountName) {
        String requestBody = String.format(GET_ACCOUNT_RPC, accountName);
        ResponseEntity<String> response = restTemplate.postForEntity(rpcUrl, requestBody, String.class);
        if (RestUtil.isError(response.getStatusCode())) {
            throw new RuntimeException("cant get account Info");
        }
        return new JSONObject(response.getBody());
    }

    @Override
    public RippleAccount porposeAccount() {
        ResponseEntity<String> response = restTemplate.postForEntity(rpcUrl, WALLET_PORPOSE_RPC, String.class);
        if (RestUtil.isError(response.getStatusCode()) || response.getBody().contains("error")) {
            throw new RuntimeException("cant generate new address");
        }
        JSONObject responseBody = new JSONObject(response.getBody());
        return RippleAccount.builder()
                .name(responseBody.getString("account_id"))
                .secret(responseBody.getString("master_seed"))
                .build();
    }

}