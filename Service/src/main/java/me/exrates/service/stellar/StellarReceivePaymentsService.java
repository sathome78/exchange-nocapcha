package me.exrates.service.stellar;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.service.MerchantService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.stellar.sdk.*;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.requests.PaymentsRequestBuilder;
import org.stellar.sdk.requests.TransactionsRequestBuilder;
import org.stellar.sdk.responses.TransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by maks on 06.06.2017.
 */
@Log4j2
@Component
@PropertySource("classpath:/merchants/stellar.properties")
public class StellarReceivePaymentsService {

    @Autowired
    private StellarService stellarService;
    @Autowired
    private StellarTransactionService stellarTransactionService;
    @Autowired
    private MerchantSpecParamsDao specParamsDao;


    private @Value("${stellar.horizon.url}")String SEVER_URL;
    private @Value("${stellar.account.name}")String ACCOUNT_NAME;
    private @Value("${stellar.account.seed}")String ACCOUNT_SECRET;
    private Server server;
    private KeyPair account;
    private static final String LAST_PAGING_TOKEN_PARAM = "LastPagingToken";
    private static final String MERCHANT_NAME = "Stellar";

    // Create an API call to query payments involving the account.
    private PaymentsRequestBuilder paymentsRequest;


    @PostConstruct
    public void init() {
        server = new Server(SEVER_URL);
        account = KeyPair.fromAccountId(ACCOUNT_NAME);
        paymentsRequest = server.payments().forAccount(account);
        String lastToken = loadLastPagingToken();
        if (lastToken != null) {
            paymentsRequest.cursor(lastToken);
        }
        paymentsRequest.stream(new EventListener<OperationResponse>() {
            @Override
            public void onEvent(OperationResponse payment) {
                log.debug("stellar income payment {}", payment);
                // The payments stream includes both sent and received payments. We only
                // want to process received payments here.
                if (payment instanceof PaymentOperationResponse) {
                    if (((PaymentOperationResponse) payment).getTo().equals(account)) {
                        PaymentOperationResponse response = ((PaymentOperationResponse) payment);
                        if (response.getAsset().equals(new AssetTypeNative())) {
                            TransactionResponse transactionResponse = null;
                            try {
                                transactionResponse = stellarTransactionService.getTxByURI(SEVER_URL, response.getLinks().getTransaction().getUri());
                            } catch (Exception e) {
                                log.error("error getting transaction {}", e);
                            }
                            stellarService.onTransactionReceive(transactionResponse, ((PaymentOperationResponse) payment).getAmount());
                            // Record the paging token so we can start from here next time.
                            savePagingToken(payment.getPagingToken());
                        } else {
                            return;
                        }
                    } else {
                        /*todo processing of sended payments if needed*/
                        return;
                    }
                }
            }
        });
    }

    private void savePagingToken(String pagingToken) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_PAGING_TOKEN_PARAM, pagingToken);
    }

    private String loadLastPagingToken() {
        return specParamsDao.getByMerchantIdAndParamName(MERCHANT_NAME, LAST_PAGING_TOKEN_PARAM).getParamValue();
    }
}
