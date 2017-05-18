package me.exrates.service.impl;

import me.exrates.dao.EthereumNodeDao;
import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CurrencyService;
import me.exrates.service.EthereumCommonService;
import me.exrates.service.MerchantService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.EthereumException;
import me.exrates.service.exception.InvalidAmountException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.exceptions.TransactionTimeoutException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import rx.Observable;
import rx.Subscription;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ajet
 */
//@Service
public class EthereumCommonServiceImpl implements EthereumCommonService {

//    private @Value("${ethereum.destinationDir}") String destinationDir;
//    private @Value("${ethereum.password}") String password;
//    private @Value("${ethereum.mainAddress}") String mainAddress;

    @Autowired
    private EthereumNodeDao ethereumNodeDao;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PlatformTransactionManager txManager;


    private String currentCurrency;

    private String url;

    private String destinationDir;

    private String password;

    private String mainAddress;

    private final List<String> accounts = new ArrayList<>();

    private final List<String> pendingTransactions = new ArrayList<>();

    private Web3j web3j;

    private Observable<org.web3j.protocol.core.methods.response.Transaction> observable;

    private Subscription subscription;

    private boolean subscribeCreated;

    private BigInteger currentBlockNumber;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Logger LOG = LogManager.getLogger("node_ethereum");

    public EthereumCommonServiceImpl(String propertySource) {
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertySource));
            this.currentCurrency = props.getProperty("ethereum.currency");
            this.url = props.getProperty("ethereum.url");
            this.destinationDir = props.getProperty("ethereum.destinationDir");
            this.password = props.getProperty("ethereum.password");
            this.mainAddress = props.getProperty("ethereum.mainAddress");
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    @PostConstruct
    void start() {

        web3j = Web3j.build(new HttpService(url));

        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                checkSession();
            }
        }, 0, 1, TimeUnit.MINUTES);
        try {
            createSubscribe();
        } catch (EthereumException e) {
            LOG.error(e);
        }
    }

    public void createSubscribe(){
        try {
            LOG.debug(currentCurrency + " Connecting ethereum...");
            ethereumNodeDao.findAllAddresses(currentCurrency).forEach(address -> accounts.add(address));
            ethereumNodeDao.findPendingTransactions(currentCurrency).forEach(transaction -> pendingTransactions.add(transaction));
            subscribeCreated = true;
            currentBlockNumber = new BigInteger("0");

            observable = web3j.transactionObservable();
            subscription = observable.subscribe(ethBlock -> {

                if (!currentBlockNumber.equals(ethBlock.getBlockNumber())){
                    System.out.println(currentCurrency + " Current block number: " + ethBlock.getBlockNumber());
                    LOG.debug(currentCurrency + " Current block number: " + ethBlock.getBlockNumber());

                    List<String> providedTransactions = new ArrayList<String>();
                    pendingTransactions.forEach(transaction ->
                            {
                                try {
                                    if (web3j.ethGetTransactionByHash(transaction).send().getResult()==null){
                                        return;
                                    }
                                    BigInteger transactionBlockNumber = web3j.ethGetTransactionByHash(transaction).send().getResult().getBlockNumber();
                                    if (ethBlock.getBlockNumber().subtract(transactionBlockNumber).intValue() > 12){
                                        provideTransactionAndTransferFunds(transaction);
                                        LOG.debug(currentCurrency + " Transaction: " + transaction + " - PROVIDED!!!");
                                        LOG.debug(currentCurrency + " Confirmations count: " + ethBlock.getBlockNumber().subtract(transactionBlockNumber).intValue());
                                        providedTransactions.add(transaction);
                                    }
                                } catch (EthereumException | IOException e) {
                                    subscribeCreated = false;
                                    LOG.error(currentCurrency + " " + e);
                                }

                                System.out.println(currentCurrency + " Pending transaction: " + transaction);
                            }

                    );
                    providedTransactions.forEach(transaction -> pendingTransactions.remove(transaction));
                }

                currentBlockNumber = ethBlock.getBlockNumber();
                LOG.info(currentCurrency + " block: " + ethBlock.getBlockNumber());

                String recipient = ethBlock.getTo();
                if (accounts.contains(recipient)){
                    if (!ethereumNodeDao.isMerchantTransactionExists(ethBlock.getHash(), currentCurrency)){
                        BigDecimal amount = Convert.fromWei(String.valueOf(ethBlock.getValue()), Convert.Unit.ETHER);
                        LOG.debug(currentCurrency + " recipient: " + recipient + ", amount: " + amount);
                        pendingTransactions.add(ethBlock.getHash());
                        createTransaction(recipient, String.valueOf(amount), ethBlock.getHash());
                    }
                }

                });

        } catch (EthereumException e) {
            subscribeCreated = false;
            LOG.error(currentCurrency + " " + e);
        }
    }

    public void checkSession() {

        try {
            web3j.netVersion().send();
            if (subscribeCreated == false){
                createSubscribe();
            }
            subscribeCreated = true;
        } catch (IOException e) {
            LOG.error(currentCurrency + " " + e);
            subscribeCreated = false;
        }
    }

    @Override
    @Transactional
    public String createAddress(CreditsOperation creditsOperation){
        try {

            File destination = new File(destinationDir);
            LOG.debug(currentCurrency + " " + destinationDir);

            String fileName = "";
            fileName = WalletUtils.generateLightNewWalletFile(password, destination);
            LOG.debug(currentCurrency + " " + fileName);
            Credentials credentials = WalletUtils.loadCredentials(password, destinationDir + fileName);

            EthereumAccount ethereumAccount = new EthereumAccount();
            ethereumAccount.setAddress(credentials.getAddress());
            ethereumAccount.setUser(creditsOperation.getUser());
            ethereumAccount.setPrivateKey(credentials.getEcKeyPair().getPrivateKey());
            ethereumAccount.setPublicKey(credentials.getEcKeyPair().getPublicKey());
            ethereumNodeDao.createAddress(ethereumAccount, currentCurrency);
            accounts.add(ethereumAccount.getAddress());
            LOG.debug(currentCurrency + " " + ethereumAccount.getAddress());

            return ethereumAccount.getAddress();

        }catch (EthereumException | IOException | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException | NoSuchProviderException | CipherException e){
            LOG.error(currentCurrency + " " + e);
            return "";
        }
    }

    private void createTransaction(String address, String amount, String hash){
        try {
            String userEmail = ethereumNodeDao.findUserEmailByAddress(address, currentCurrency);
            Payment payment = new Payment();
            Currency currency;
            if (currentCurrency.equals("Ethereum")){
                currency = currencyService.findByName("ETH");
            }else {
                currency = currencyService.findByName("ETC");
            }
            payment.setCurrency(currency.getId());
            List<Integer> list = new ArrayList<>();
            list.add(currency.getId());
            MerchantCurrency merchantCurrency = merchantService.findAllByCurrencies(list, OperationType.INPUT).get(0);
            payment.setMerchant(merchantCurrency.getMerchantId());
            payment.setOperationType(OperationType.INPUT);
            payment.setMerchantImage(merchantCurrency.getListMerchantImage().get(0).getId());
            payment.setSum(Double.parseDouble(amount));
            CreditsOperation creditsOperation = merchantService
                    .prepareCreditsOperation(payment, userEmail)
                    .orElseThrow(InvalidAmountException::new);

            TransactionTemplate tmpl1 = new TransactionTemplate(txManager);
            tmpl1.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    LOG.debug(currentCurrency + " Creating transaction request ");
                    Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
                    ethereumNodeDao.createMerchantTransaction(address, hash, transaction.getId());
                    LOG.debug(currentCurrency + " transaction " + transaction.toString() + " --- was created!!!");
                }
            });

        }catch (EthereumException e){
            subscribeCreated = false;
            LOG.error(currentCurrency + " " + e);
        }
    }

    private void provideTransactionAndTransferFunds(String merchantTransactionId){

        Transaction transaction = transactionService.findById(ethereumNodeDao.findTransactionId(merchantTransactionId, currentCurrency));
        if (!transaction.isProvided()){
            transactionService.provideTransaction(transaction);

            LOG.debug(currentCurrency + " Ethereum transaction " + transaction.toString() + " --- PROVIDED!!!");
            transferFundsToMainAccount(ethereumNodeDao.findAddressByMerchantTransactionId(merchantTransactionId, currentCurrency), transaction.getAmount());
        }
    }

    private void transferFundsToMainAccount(String address, BigDecimal amount){
        try {
            Optional<EthereumAccount> ethereumAccount = ethereumNodeDao.findByAddress(address, currentCurrency);
            Credentials credentials = Credentials.create(new ECKeyPair(ethereumAccount.get().getPrivateKey(),
                    ethereumAccount.get().getPublicKey()));
            Transfer.sendFunds(
                    web3j, credentials, mainAddress, amount
                            .subtract(Convert.fromWei(Transfer.GAS_LIMIT.multiply(Transfer.GAS_PRICE).toString(), Convert.Unit.ETHER)), Convert.Unit.ETHER);
            LOG.debug(currentCurrency + " Funds " + amount + " sent to main account!!!");
        } catch (EthereumException| InterruptedException | ExecutionException | TransactionTimeoutException e) {
            subscribeCreated = false;
            LOG.error(currentCurrency + " " + e);
        }
    }

    @PreDestroy
    public void destroy() {
        LOG.debug("Destroing " + currentCurrency);
        scheduler.shutdown();
        subscription.unsubscribe();
        LOG.debug(currentCurrency + " destroyed");
    }
}
