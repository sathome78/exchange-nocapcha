package me.exrates.service.stellar;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.RefillRequest;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import org.apache.http.client.utils.URIBuilder;
import org.glassfish.jersey.media.sse.EventSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.stellar.sdk.*;
import org.stellar.sdk.requests.EventListener;
import org.stellar.sdk.requests.PaymentsRequestBuilder;
import org.stellar.sdk.requests.RequestBuilder;
import org.stellar.sdk.requests.TransactionsRequestBuilder;
import org.stellar.sdk.responses.GsonSingleton;
import org.stellar.sdk.responses.TransactionResponse;
import org.stellar.sdk.responses.operations.OperationResponse;
import org.stellar.sdk.responses.operations.PaymentOperationResponse;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by maks on 06.06.2017.
 */
@Log4j2(topic = "stellar_log")
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
    private EventSource eventSource;


    @PostConstruct
    public void init() {
        server = new Server(SEVER_URL);
        account = KeyPair.fromAccountId(ACCOUNT_NAME);
    }

    private void checkIncomePayment() {
        log.debug("starting check xlm income payments");
        paymentsRequest = server.payments().forAccount(account);
        String lastToken = loadLastPagingToken();
        log.debug("lastToken {}", lastToken);
        if (lastToken != null) {
            paymentsRequest.cursor(lastToken);
        }
        eventSource = paymentsRequest.stream(payment -> {
            savePagingToken(payment.getPagingToken());
            // The payments stream includes both sent and received payments. We only
            // want to process received payments here.
            if (payment instanceof PaymentOperationResponse) {
                log.debug("its payment response");
                log.debug("to {}", ((PaymentOperationResponse) payment).getTo().getAccountId());
                log.debug("from {}", ((PaymentOperationResponse) payment).getFrom().getAccountId());
                if (((PaymentOperationResponse) payment).getTo().getAccountId().equals(ACCOUNT_NAME)) {
                    PaymentOperationResponse response = ((PaymentOperationResponse) payment);
                    log.debug(response.getAsset().getType());
                    if (response.getAsset().equals(new AssetTypeNative())) {
                        TransactionResponse transactionResponse = null;
                        try {
                            transactionResponse = stellarTransactionService.getTxByURI(SEVER_URL, response.getLinks().getTransaction().getUri());
                        } catch (Exception e) {
                            log.error("error getting transaction {}", e);
                        }
                        log.debug("process transaction");
                        stellarService.onTransactionReceive(transactionResponse, ((PaymentOperationResponse) payment).getAmount());
                        // Record the paging token so we can start from here next time.
                        log.debug("transaction xlm {} saved ", transactionResponse.getHash());
                    }
                } else {
                    log.debug("payment not for us");
                }
            } else {
                log.debug("its not !! payment response");
            }
        });
    }

   @Scheduled(initialDelay = 30000, fixedRate = 1000 * 60 * 3)
    public void checkEventSource() {
       log.debug("check eventsource state");
        if (eventSource.isOpen()) {
            eventSource.close(20, TimeUnit.SECONDS);
        } else {
            checkIncomePayment();
        }
    }


    private void savePagingToken(String pagingToken) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_PAGING_TOKEN_PARAM, pagingToken);
    }

    private String loadLastPagingToken() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantIdAndParamName(MERCHANT_NAME, LAST_PAGING_TOKEN_PARAM);
        return specParamsDto == null ? null : specParamsDto.getParamValue();
    }
}
