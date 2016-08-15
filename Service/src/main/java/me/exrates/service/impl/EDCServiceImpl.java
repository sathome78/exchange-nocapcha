package me.exrates.service.impl;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;
import me.exrates.service.AlgorithmService;
import me.exrates.service.EDCService;
import me.exrates.service.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class EDCServiceImpl implements EDCService {

    private final Logger LOG = LogManager.getLogger("merchant");

    private final PendingPaymentDao paymentDao;
    private final TransactionService transactionService;
    private final AlgorithmService algService;

    private final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private final MediaType MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded");

    private final String RPC_URL = "http://e-dinarcoin.com:5902";
    private final String CREATE_ACCOUNT = "{\"method\": \"create_account_with_brain_key\", \"jsonrpc\": \"2.0\", \"params\": [\"COMPEND HIPPED POSITUM BARREL WEARY GALLFLY AURORAE TOURTE AXOID MILNER JENKIN NODE ASPERGE MOKY SENSE RELEVY\", \"%s\", \"alpha\", \"exrates-currency-exchange\", \"true\" ], \"id\": %d}";
    private final String GET_ACCOUNT_ID = "{\"method\": \"get_account_id\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\"], \"id\": %d}";

    private final ConcurrentMap<String, PendingPayment> pendingPayments = new ConcurrentHashMap<>();

    @Autowired
    public EDCServiceImpl(final PendingPaymentDao paymentDao,
                          final TransactionService transactionService,
                          final AlgorithmService algService)
    {
        this.paymentDao = paymentDao;
        this.transactionService = transactionService;
        this.algService = algService;
    }

    @Override
    @Transactional
    public PendingPayment createInvoice(CreditsOperation operation) throws Exception {
        final Transaction tx = transactionService.createTransactionRequest(operation);
        final String account = createAccount(tx.getId());
        final PendingPayment payment = new PendingPayment();
        payment.setAddress(account);
        payment.setInvoiceId(tx.getId());
        payment.setTransactionHash(algService.sha256(account));
        paymentDao.create(payment);
        return payment;
    }

//    private void addPaymentOnExpectationStack(final PendingPayment payment) throws IOException {
//        final String response = makeRpcCall(GET_ACCOUNT_ID, payment.getAddress().get(), payment.getInvoiceId());
//        System.out.println(response);
//        final ObjectMapper mapper = new ObjectMapper();
//        final Map<String,String> result = mapper.readValue(response, new TypeReference<Map<String, String>>() {});
//        System.out.println(result);
//        pendingPayments.put(result.get())
//    }

    private String createAccount(int id) throws Exception {
        final String accountName = "exrates-" + id + "ex" + UUID.randomUUID();
        final String response = makeRpcCall(CREATE_ACCOUNT, accountName, id);
        if (response.contains("error")) {
            throw new Exception("Could not create new account!\n" + response);
        }
        LOG.info("EDC: NEW ACCOUNT GENERATED "  + accountName);
        return accountName;
    }

    private String makeRpcCall(String rpc, Object ... args) throws IOException {
        final String rpcCall = String.format(rpc, args);
        System.out.println(rpcCall);
        final Request request = new Request.Builder()
                .url(RPC_URL)
                .post(RequestBody.create(MEDIA_TYPE, rpcCall))
                .build();
        return HTTP_CLIENT.newCall(request)
                .execute()
                .body()
                .string();
    }
}
