package me.exrates.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;
import me.exrates.service.EDCService;
import me.exrates.service.TransactionService;
import me.exrates.service.util.BiTuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class EDCServiceImpl implements EDCService {

    private final Logger LOG = LogManager.getLogger("merchant");
    private static final BigDecimal BTS = new BigDecimal(1000L);
    private static final int decimalPlaces = 2;

    private final PendingPaymentDao paymentDao;
    private final TransactionService transactionService;

    private final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private final MediaType MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded");

    private final String PENDING_PAYMENT_HASH = "1fc3403096856798ab8992f73f241334a4fe98ce";

    private final String RPC_URL = "http://e-dinarcoin.com:5902";
    private final String CREATE_ACCOUNT = "{\"method\": \"create_account_with_brain_key\", \"jsonrpc\": \"2.0\", \"params\": [\"COMPEND HIPPED POSITUM BARREL WEARY GALLFLY AURORAE TOURTE AXOID MILNER JENKIN NODE ASPERGE MOKY SENSE RELEVY\", \"%s\", \"alpha\", \"exrates-currency-exchange\", \"true\" ], \"id\": %d}";
    private final String GET_ACCOUNT_ID = "{\"method\": \"get_account_id\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\"], \"id\": %d}";

    private final ConcurrentMap<String, PendingPayment> pendingPayments = new ConcurrentHashMap<>();
    private final BlockingQueue<String> rawTransactions = new LinkedBlockingQueue<>();
    private final BlockingQueue<BiTuple<String,String>> incomingPayments = new LinkedBlockingQueue<>();
    private final ExecutorService workers = Executors.newFixedThreadPool(2);
    private volatile boolean isRunning = true;

    @Autowired
    public EDCServiceImpl(final PendingPaymentDao paymentDao,
                          final TransactionService transactionService)
    {
        this.paymentDao = paymentDao;
        this.transactionService = transactionService;
    }

    private void handleRawTransactions(final String tx) {
        final String transactions = tx.substring(tx.indexOf("transactions"), tx.length());
        final String[] operationses = transactions.split("operations");
        for (String str : operationses) {
            final int extensions = str.indexOf("extensions");
            if (extensions > 0) {
                str = str.substring(0, extensions);
                if (str.contains("\":[[0,{\"fee\"")) {
                    str = str .substring(str.indexOf("to"));
                    final String accountId =  str.substring(str.indexOf("to") + 5, str.indexOf("amount") - 3);
                    final String amount = str.substring(str.lastIndexOf("amount") + 8, str.indexOf("asset_id") - 2);
                    try {
                        incomingPayments.put(new BiTuple<>(accountId, amount));
                    } catch (final InterruptedException e) {
                        LOG.error(e);
                    }
                }
            }
        }
    }

    @Transactional
    private void acceptTransaction(final BiTuple<String,String> tuple) {
        final String accountId = tuple.left;
        final PendingPayment payment = pendingPayments.get(accountId);
        if (payment != null) {
            final Transaction tx = transactionService.findById(payment.getInvoiceId());
            final BigDecimal targetAmount = tx.getAmount().add(tx.getCommissionAmount()).setScale(decimalPlaces, ROUND_HALF_UP);
            final BigDecimal currentAmount = new BigDecimal(tuple.right).setScale(decimalPlaces, ROUND_HALF_UP).divide(BTS).setScale(decimalPlaces, ROUND_HALF_UP);
            if (targetAmount.compareTo(currentAmount) != 0) {
                transactionService.updateTransactionAmount(tx, currentAmount);
            }
            transactionService.provideTransaction(tx);
            pendingPayments.remove(accountId);
        } else {
            LOG.error("UNKNOWN PAYMENT " + tuple.right + " EDC FROM " + accountId);
        }
    }

    @PostConstruct
    public void init() {
        // cache warm
        paymentDao.findAllByHash(PENDING_PAYMENT_HASH).forEach(payment -> pendingPayments.put(payment.getAddress().get(), payment));
        workers.submit(() -> {  // processing json with transactions from server
            while (isRunning) {
                final String poll = rawTransactions.poll();
                if (poll != null) {
                    handleRawTransactions(poll);
                }
            }
        });
        workers.submit(() -> {
            while (isRunning) { // accepting transactions
                final BiTuple<String, String> poll = incomingPayments.poll();
                if (poll != null) {
                    acceptTransaction(poll);
                }
            }
        });
    }

    @PreDestroy
    public void destroy() {
        isRunning = false;
        workers.shutdown();
    }

    @Override
    @Transactional
    public String createInvoice(CreditsOperation operation) throws Exception {
        final Transaction tx = transactionService.createTransactionRequest(operation);
        final String account = createAccount(tx.getId());
        final String accountId = extractAccountId(account, tx.getId());
        final PendingPayment payment = new PendingPayment();
        payment.setAddress(accountId);
        payment.setInvoiceId(tx.getId());
        payment.setTransactionHash(PENDING_PAYMENT_HASH); // every edc payment invoice has uniform tx-hash to distinguish them from other invoices
        pendingPayments.put(accountId, payment);
        paymentDao.create(payment);
        return account;
    }

    @Override
    public void submitTransactionsForProcessing(String list) {
        try {
            rawTransactions.put(list);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String extractAccountId(final String account, final int invoiceId) throws IOException {
        final String response = makeRpcCall(GET_ACCOUNT_ID, account, invoiceId);
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String,String> result = mapper.readValue(response, new TypeReference<Map<String, String>>() {});
        return result.get("result");
    }

    private String createAccount(int id) throws Exception {
        final String accountName = "exrates-" + id + "ex" + UUID.randomUUID();
        final String response = makeRpcCall(CREATE_ACCOUNT, accountName, id);
        if (response.contains("error")) {
            throw new Exception("Could not create new account!\n" + response);
        }
        return accountName;
    }

    private String makeRpcCall(String rpc, Object ... args) throws IOException {
        final String rpcCall = String.format(rpc, args);
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
