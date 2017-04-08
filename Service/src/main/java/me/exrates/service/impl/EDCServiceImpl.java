package me.exrates.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import me.exrates.model.dto.PendingPaymentSimpleDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;
import me.exrates.service.EDCService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.util.BiTuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.springframework.transaction.annotation.Propagation.NESTED;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@PropertySource("classpath:edc_cli_wallet.properties")
public class EDCServiceImpl implements EDCService {

    private @Value("${edc.blockchain.host_delayed}") String RPC_URL_DELAYED;
    private @Value("${edc.blockchain.host_fast}") String RPC_URL_FAST;
    private @Value("${edc.account.registrar}") String REGISTRAR_ACCOUNT;
    private @Value("${edc.account.referrer}") String REFERRER_ACCOUNT;
    private @Value("${edc.account.main}") String MAIN_ACCOUNT;
    private @Value("${edc.account.main.private.key}") String MAIN_ACCOUNT_PRIVATE_KEY;
    private final String PENDING_PAYMENT_HASH = "1fc3403096856798ab8992f73f241334a4fe98ce";

    private final PendingPaymentDao paymentDao;
    private final EDCAccountDao edcAccountDao;
    private final TransactionService transactionService;

    private final Logger LOG = LogManager.getLogger("merchant");

    private final BigDecimal BTS = new BigDecimal(1000L);
    private final int DEC_PLACES = 2;

    private final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private final MediaType MEDIA_TYPE = MediaType.parse("application/x-www-form-urlencoded");

    private final ConcurrentMap<String, PendingPaymentSimpleDto> pendingPayments = new ConcurrentHashMap<>();
    private final BlockingQueue<String> rawTransactions = new LinkedBlockingQueue<>();
    private final BlockingQueue<BiTuple<String,String>> incomingPayments = new LinkedBlockingQueue<>();
    private final ExecutorService workers = Executors.newFixedThreadPool(2);
    private volatile boolean isRunning = true;

    private final String ACCOUNT_PREFIX = "ex1f";
    private final String REGISTER_NEW_ACCOUNT_RPC = "{\"method\":\"register_account\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", 0, \"true\"], \"id\":%s}";
    private final String NEW_KEY_PAIR_RPC = "{\"method\": \"suggest_brain_key\", \"jsonrpc\": \"2.0\", \"params\": [], \"id\": %d}";
    private final String IMPORT_KEY = "{\"method\": \"import_key\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\",\"%s\"], \"id\": %s}";
    private final String TRANSFER_EDC = "{\"method\":\"transfer\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"true\"], \"id\":%s}";
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

        try {

        final String transactions = tx.substring(tx.indexOf("transactions"), tx.length());
        final String[] operationses = transactions.split("operations");
        for (String str : operationses) {
            final int extensions = str.indexOf("extensions");
            if (extensions > 0) {
                str = str.substring(0, extensions);
                if (str.contains("\":[[0,{\"fee\"")) {
                    str = str.substring(str.indexOf("to"));
                    final String accountId = str.substring(str.indexOf("to") + 5, str.indexOf("amount") - 3);
                    final String amount = str.substring(str.lastIndexOf("amount") + 8, str.indexOf("asset_id") - 2);
                    incomingPayments.put(new BiTuple<>(accountId, amount));
                }
            }
        }
        }catch (InterruptedException e){
            LOG.info("Method acceptTransaction InterruptedException........................................... error: ");
            LOG.error(e);
        }catch (Exception e){
            LOG.info("Method acceptTransaction Exception........................................... error: ");
            LOG.error(e);
        }
    }

    @Transactional
    private void acceptTransaction(final BiTuple<String,String> tuple) {
        try {

        final String accountId = tuple.left;
        final PendingPaymentSimpleDto payment = pendingPayments.get(accountId);
        if (payment != null) {
            final Transaction tx = transactionService.findById(payment.getInvoiceId());
            if (debugLog) {
                LOG.info("PROVIDING EDC TRANSACTION : " + tx);
            }
            final BigDecimal targetAmount = tx.getAmount().add(tx.getCommissionAmount()).setScale(DEC_PLACES, ROUND_HALF_UP);
            final BigDecimal currentAmount = new BigDecimal(tuple.right).setScale(DEC_PLACES, ROUND_HALF_UP).divide(BTS, ROUND_HALF_UP).setScale(DEC_PLACES, ROUND_HALF_UP);
            if (targetAmount.compareTo(currentAmount) != 0) {
                transactionService.updateTransactionAmount(tx, currentAmount);
            }
            transactionService.provideTransaction(tx);
            paymentDao.delete(payment.getInvoiceId());
            pendingPayments.remove(accountId);
            transferToMainAccount(accountId, tx);
            edcAccountDao.setAccountUsed(tx.getId());
        }
        }catch (InterruptedException e){
            LOG.info("Method acceptTransaction InterruptedException........................................... error: ");
            LOG.error(e);
        }catch (IOException e){
            LOG.info("Method acceptTransaction IOException........................................... error: ");
            LOG.error(e);
        }catch (Exception e){
            LOG.info("Method acceptTransaction Exception........................................... error: ");
            LOG.error(e);
        }

    }

    @Transactional
    @Override
    public void rescanUnusedAccounts(){
        List<EDCAccount> list = edcAccountDao.getUnusedAccounts();
        for (EDCAccount account : list){
            final String responseImportKey;
            try {
                responseImportKey = makeRpcCallFast(IMPORT_KEY, account.getAccountId(), account.getWifPrivKey(), account.getTransactionId());
                if (responseImportKey.contains("true")) {
                    String accountBalance = extractBalance(account.getAccountId(), account.getTransactionId());
                    if (Double.valueOf(accountBalance) > 0){
                        final String responseTransfer = makeRpcCallFast(TRANSFER_EDC,account.getAccountId(), MAIN_ACCOUNT, accountBalance, "EDC", "Inner transfer", String.valueOf(account.getTransactionId()));
                        if (responseTransfer.contains("error")) {
                            throw new InterruptedException("Could not transfer money to main account!\n" + responseTransfer);
                        }
                        edcAccountDao.setAccountUsed(account.getTransactionId());
                        Optional<PendingPayment> payment = paymentDao.findByInvoiceId(account.getTransactionId());
                        if (payment.isPresent()){
                            paymentDao.delete(account.getTransactionId());
                        }
                        Transaction transaction = transactionService.findById(account.getTransactionId());
                        if (!transaction.isProvided()){

                            final BigDecimal targetAmount = transaction.getAmount().add(transaction.getCommissionAmount()).setScale(DEC_PLACES, ROUND_HALF_UP);
                            final BigDecimal currentAmount = new BigDecimal(accountBalance).add(new BigDecimal("0.001")).setScale(DEC_PLACES, ROUND_HALF_UP);
                            if (targetAmount.compareTo(currentAmount) != 0) {
                                transactionService.updateTransactionAmount(transaction, currentAmount);
                            }
                            transactionService.provideTransaction(transaction);
                        }
                    }else {
                        edcAccountDao.setAccountUsed(account.getTransactionId());
                    }
                }
            } catch (IOException e) {
                LOG.error(e);
            } catch (InterruptedException e) {
                LOG.error(e);
            }
        }
    }

    @PostConstruct
    public void init() {
        // cache warm
            try {

            paymentDao.findAllByHash(PENDING_PAYMENT_HASH)
                .forEach(payment -> pendingPayments.put(payment.getAddress(), payment));
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
            }catch (Exception e){
                LOG.info("Method init Exception........................................... error: ");
                LOG.error(e);
            }
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
        getDelayedAccountId(account, tx);
        return account;
    }

    private void getDelayedAccountId (String account, Transaction tx) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {

                String accountId = "";
                try {
                    Thread.sleep(60000);
                    accountId = extractAccountId(account, tx.getId());
                    final PendingPayment payment = new PendingPayment();
                    payment.setAddress(accountId);
                    payment.setInvoiceId(tx.getId());
                    payment.setTransactionHash(PENDING_PAYMENT_HASH); // every edc payment invoice has uniform tx-hash to distinguish them from other invoices
                  payment.setPendingPaymentStatus(PendingPaymentStatusEnum.getBeginState());
                    pendingPayments.put(accountId, new PendingPaymentSimpleDto(payment));
                    paymentDao.create(payment);
                    edcAccountDao.setAccountIdByTransactionId(tx.getId(), accountId);
                } catch (IOException e) {
                    LOG.error(e);
                } catch (InterruptedException e) {
                    LOG.error(e);
                }
            }
        }).start();

    }

    @Override
    public void submitTransactionsForProcessing(String list) {
        try {
            rawTransactions.put(list);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public String extractAccountId(final String account, final int invoiceId) throws IOException {
        final String GET_ACCOUNT_ID_RPC = "{\"method\": \"get_account_id\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\"], \"id\": %d}";
        final String response = makeRpcCallDelayed(GET_ACCOUNT_ID_RPC, account, invoiceId);
        final ObjectMapper mapper = new ObjectMapper();
        final Map<String,String> result = mapper.readValue(response, new TypeReference<Map<String, String>>() {});
        return result.get("result");
    }

    @Transactional(propagation = NESTED)
    private String createAccount(final int id) throws Exception {
        LOG.info("Start method createAccount");
        final String accountName = (ACCOUNT_PREFIX + id + UUID.randomUUID()).toLowerCase();
        final EnumMap<KEY_TYPE, String> keys = extractKeys(makeRpcCallFast(NEW_KEY_PAIR_RPC, id)); // retrieve public and private from server
        final String response = makeRpcCallFast(REGISTER_NEW_ACCOUNT_RPC, accountName, keys.get(KEY_TYPE.PUBLIC), keys.get(KEY_TYPE.PUBLIC), REGISTRAR_ACCOUNT, REFERRER_ACCOUNT, String.valueOf(id));
        LOG.info("bit_response: " + response.toString());
        if (response.contains("error")) {
            throw new Exception("Could not create new account!\n" + response);
        }
        final EDCAccount edcAccount = new EDCAccount();
        edcAccount.setTransactionId(id);
        edcAccount.setBrainPrivKey(keys.get(KEY_TYPE.BRAIN));
        edcAccount.setPubKey(keys.get(KEY_TYPE.PUBLIC));
        edcAccount.setWifPrivKey(keys.get(KEY_TYPE.PRIVATE));
        edcAccount.setAccountName(accountName);
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

    private String extractBalance(final String accountId, final int invoiceId) throws IOException {
        final String LIST_ACCOUNT_BALANCE = "{\"method\": \"list_account_balances\", \"jsonrpc\": \"2.0\", \"params\": [\"%s\"], \"id\": %s}";
        final String response = makeRpcCallFast(LIST_ACCOUNT_BALANCE, accountId, invoiceId);
        JsonParser parser = new JsonParser();
        JsonObject object = parser.parse(response).getAsJsonObject();
        JsonArray result = object.getAsJsonArray("result");
        if (result.size() != 1) {
            return new BigDecimal("0.000").toString();
        }
        BigDecimal amount = BigDecimal.valueOf(result.get(0).getAsJsonObject().get("amount").getAsLong()).divide(BTS);

        return amount.subtract(new BigDecimal("0.001")).toString();
    }

    public void transferToMainAccount(final String accountId, final Transaction tx) throws IOException, InterruptedException {

        final EDCAccount edcAccount = edcAccountDao.findByTransactionId(tx.getId());
        final String responseImportKey = makeRpcCallFast(IMPORT_KEY, accountId, edcAccount.getWifPrivKey(), tx.getId());
        if (responseImportKey.contains("true")) {
            String accountBalance = extractBalance(accountId, tx.getId());
                final String responseTransfer = makeRpcCallFast(TRANSFER_EDC,accountId, MAIN_ACCOUNT, accountBalance, "EDC", "Inner transfer", String.valueOf(tx.getId()));
                if (responseTransfer.contains("error")) {
                    throw new InterruptedException("Could not transfer money to main account!\n" + responseTransfer);
                }
        }
    }

    private void transferFromMainAccount(final String accountName, final String amount) throws IOException, InterruptedException {
        final String responseImportKey = makeRpcCallFast(IMPORT_KEY, MAIN_ACCOUNT, MAIN_ACCOUNT_PRIVATE_KEY, 1);
        if (responseImportKey.contains("true")) {
            final String responseTransfer = makeRpcCallFast(TRANSFER_EDC,MAIN_ACCOUNT, accountName, amount, "EDC", "Output transfer", 1);
            if (responseTransfer.contains("error")) {
                throw new InterruptedException("Could not transfer money from main account!\n" + responseTransfer);
            }
        }
    }

    private String makeRpcCallDelayed(String rpc, Object ... args) throws IOException {
        final String rpcCall = String.format(rpc, args);
        final Request request = new Request.Builder()
                .url(RPC_URL_DELAYED)
                .post(RequestBody.create(MEDIA_TYPE, rpcCall))
                .build();
        return HTTP_CLIENT.newCall(request)
                .execute()
                .body()
                .string();
    }

    private String makeRpcCallFast(String rpc, Object ... args) throws IOException {
        final String rpcCall = String.format(rpc, args);
        final Request request = new Request.Builder()
                .url(RPC_URL_FAST)
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

    @Override
    public void withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        transferFromMainAccount(
            withdrawMerchantOperationDto.getAccountTo(),
            withdrawMerchantOperationDto.getAmount());
    }

    @Override
    public RedirectView getMerchantRefillPage(RefillRequestCreateDto request){
        throw new NotImplimentedMethod("for "+request);
    }
}
