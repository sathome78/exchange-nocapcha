package me.exrates.service.stellar;

import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.stellar.sdk.*;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.requests.PaymentsRequestBuilder;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;

import javax.annotation.PostConstruct;

/**
 * Created by maks on 06.06.2017.
 */
@Log4j2
@Component
@PropertySource("classpath:/merchants/stellar.properties")
public class StellarReceivePaymentsService {

    @Autowired
    private StellarService stellarService;

    private @Value("${stellar.horizon.url}")String SEVER_URL;
    private @Value("${stellar.account.name}")String ACCOUNT_NAME;
    private @Value("${stellar.account.secret}")String ACCOUNT_SECRET;
    private Server server;
    private KeyPair account;

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
                // Record the paging token so we can start from here next time.
                savePagingToken(payment.getPagingToken());

                // The payments stream includes both sent and received payments. We only
                // want to process received payments here.
                if (payment instanceof PaymentOperationResponse) {
                    if (((PaymentOperationResponse) payment).getTo().equals(account)) {
                        return;
                    }

                    String amount = ((PaymentOperationResponse) payment).getAmount();

                    Asset asset = ((PaymentOperationResponse) payment).getAsset();
                    String assetName;
                    if (asset.equals(new AssetTypeNative())) {
                        stellarService.onTransactionReceive(payment);
                    } else {
                        return;
                    }



                }

            }
        });
    }

    private void savePagingToken(String pagingToken) {

    }

    private String loadLastPagingToken() {

        return null;
    }
}
