package me.exrates.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import me.exrates.dao.EDCAccountDao;
import me.exrates.dao.PendingPaymentDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.EDCAccount;
import me.exrates.model.PendingPayment;
import me.exrates.model.Transaction;
import me.exrates.service.EDCService;
import me.exrates.service.TransactionService;
import me.exrates.service.util.BiTuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.springframework.transaction.annotation.Propagation.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@PropertySource("classpath:edc_cli_wallet.properties")
public class EDCServiceImpl implements EDCService {

    private @Value("${edc.blockchain.host}") String RPC_URL;
    private @Value("${edc.account.registrar}") String REGISTRAR_ACCOUNT;
    private @Value("${edc.account.referrer}") String REFERRER_ACCOUNT;
    private final String PENDING_PAYMENT_HASH = "1fc3403096856798ab8992f73f241334a4fe98ce";

    private final PendingPaymentDao paymentDao;
    private final EDCAccountDao edcAccountDao;
    private final TransactionService transactionService;

    private final Logger LOG = LogManager.getLogger("merchant");

    private final BigDecimal BTS = new BigDecimal(1000L);
    private final int DEC_PLACES = 2;

    private final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private final MediaType MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded");

    private final ConcurrentMap<String, PendingPayment> pendingPayments = new ConcurrentHashMap<>();
    private final BlockingQueue<String> rawTransactions = new LinkedBlockingQueue<>();
    private final BlockingQueue<BiTuple<String,String>> incomingPayments = new LinkedBlockingQueue<>();
    private final ExecutorService workers = Executors.newFixedThreadPool(2);
    private volatile boolean isRunning = true;

    private final String ACCOUNT_PREFIX = "ex1f";
    private final String REGISTER_NEW_ACCOUNT_RPC = "{\"method\":\"register_account\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", 0, \"true\"], \"id\":%s}";
    private final String NEW_KEY_PAIR_RPC = "{\"method\": \"suggest_brain_key\", \"jsonrpc\": \"2.0\", \"params\": [], \"id\": %d}";
    private final String IMPORT_KEY = "{\"method\": \"import_key\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\",\"%s\"], \"id\": %s}";
    private final String TRANSFER_EDC = "";
    private final Pattern pattern = Pattern.compile("\"brain_priv_key\":\"([\\w\\s]+)+\",\"wif_priv_key\":\"(\\S+)\",\"pub_key\":\"(\\S+)\"");

    private volatile boolean debugLog = true;

    @Autowired
    public EDCServiceImpl(final PendingPaymentDao paymentDao,
                          final EDCAccountDao edcAccountDao,
                          final TransactionService transactionService)
    {
        this.paymentDao = paymentDao;
        this.edcAccountDao = edcAccountDao;
        this.transactionService = transactionService;
    }

    public void changeDebugLogStatus(final boolean status) {
        debugLog = true;
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
                        System.out.println(incomingPayments);
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
            if (debugLog) {
                LOG.info("PROVIDING EDC TRANSACTION : " + tx);
            }
            final EDCAccount edcAccount = edcAccountDao.findByTransactionId(tx.getId());
            final BigDecimal targetAmount = tx.getAmount().add(tx.getCommissionAmount()).setScale(DEC_PLACES, ROUND_HALF_UP);
            final BigDecimal currentAmount = new BigDecimal(tuple.right).setScale(DEC_PLACES, ROUND_HALF_UP).divide(BTS, ROUND_HALF_UP).setScale(DEC_PLACES, ROUND_HALF_UP);
            if (targetAmount.compareTo(currentAmount) != 0) {
                transactionService.updateTransactionAmount(tx, currentAmount);
            }
            transactionService.provideTransaction(tx);
            paymentDao.delete(payment.getInvoiceId());
            pendingPayments.remove(accountId);
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
        final String GET_ACCOUNT_ID_RPC = "{\"method\": \"get_account_id\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\"], \"id\": %d}";
        final String response = makeRpcCall(GET_ACCOUNT_ID_RPC, account, invoiceId);
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String,String> result = mapper.readValue(response, new TypeReference<Map<String, String>>() {});
        return result.get("result");
    }

    @Transactional(propagation = NESTED)
    private String createAccount(final int id) throws Exception {
        final String accountName = (ACCOUNT_PREFIX + id + UUID.randomUUID()).toLowerCase();
        final EnumMap<KEY_TYPE, String> keys = extractKeys(makeRpcCall(NEW_KEY_PAIR_RPC, id)); // retrieve public and private from server
        final String response = makeRpcCall(REGISTER_NEW_ACCOUNT_RPC, accountName, keys.get(KEY_TYPE.PUBLIC), keys.get(KEY_TYPE.PUBLIC), REGISTRAR_ACCOUNT, REFERRER_ACCOUNT, String.valueOf(id));
        if (response.contains("error")) {
            throw new Exception("Could not create new account!\n" + response);
        }
        final EDCAccount edcAccount = new EDCAccount();
        edcAccount.setTransactionId(id);
        edcAccount.setBrainPrivKey(keys.get(KEY_TYPE.BRAIN));
        edcAccount.setPubKey(keys.get(KEY_TYPE.PUBLIC));
        edcAccount.setWifPrivKey(keys.get(KEY_TYPE.PRIVATE));
        edcAccountDao.create(edcAccount);
        return accountName;
    }

    private EnumMap<KEY_TYPE,String> extractKeys(final String json) {
        final Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            final EnumMap<KEY_TYPE, String> result = new EnumMap<>(KEY_TYPE.class);
            result.put(KEY_TYPE.BRAIN, matcher.group(1));
            result.put(KEY_TYPE.PRIVATE, matcher.group(2));
            result.put(KEY_TYPE.PUBLIC, matcher.group(3));
            return result;
        }
        throw new RuntimeException("Invalid response from server:\n" + json);
    }

    /**
     * In progress
     *
     */
//    public void transferToExchangeAccount(final String accountId, final Transaction tx) throws IOException {
//        final EDCAccount edcAccount = edcAccountDao.findByTransactionId(tx.getId());
//        final String response = makeRpcCall(IMPORT_KEY, accountId, edcAccount.getWifPrivKey(), tx.getId());
//        if (response.contains("true")) {
//
//        }
//    }

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

    private enum KEY_TYPE {
        BRAIN("brain_priv_key"),
        PUBLIC("pub_key"),
        PRIVATE("wif_priv_key");

        public final String type;

        KEY_TYPE(final String type) {
            this.type = type;
        }
    }
}
