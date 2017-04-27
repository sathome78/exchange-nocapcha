package me.exrates.service.impl;

import me.exrates.dao.EthereumNodeDao;
import me.exrates.model.*;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CurrencyService;
import me.exrates.service.EthereumService;
import me.exrates.service.MerchantService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.EthereumException;
import me.exrates.service.exception.InvalidAmountException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ajet
 */
@Service
@PropertySource("classpath:/merchants/ethereum.properties")
public class EthereumServiceImpl implements EthereumService{

    private @Value("${ethereum.destinationDir}") String destinationDir;
    private @Value("${ethereum.password}") String password;
    private @Value("${ethereum.mainAddress}") String mainAddress;

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

    private final List<String> accounts = new ArrayList<>();

    private final List<String> pendingTransactions = new ArrayList<>();

    private Web3j web3j = Web3j.build(new HttpService());

    private Observable<org.web3j.protocol.core.methods.response.Transaction> observable = web3j.transactionObservable();

    private Subscription subscription;

    private boolean subscribeCreated;

    private BigInteger currentBlockNumber;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Logger LOG = LogManager.getLogger("node_ethereum");

    @PostConstruct
    public void start() {

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

    private void createSubscribe(){
        try {
            LOG.debug("Connecting ethereum...");
            ethereumNodeDao.findAllAddresses().forEach(address -> accounts.add(address));
            ethereumNodeDao.findPendingTransactions().forEach(transaction -> pendingTransactions.add(transaction));
            subscribeCreated = true;
            currentBlockNumber = new BigInteger("0");

            subscription = observable.subscribe(ethBlock -> {

                if (!currentBlockNumber.equals(ethBlock.getBlockNumber())){
                    System.out.println("Current block number: " + ethBlock.getBlockNumber());
                    LOG.debug("Current block number: " + ethBlock.getBlockNumber());

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
                                        LOG.debug("Transaction: " + transaction + " - PROVIDED!!!");
                                        LOG.debug("Confirmations count: " + ethBlock.getBlockNumber().subtract(transactionBlockNumber).intValue());
                                        providedTransactions.add(transaction);
                                    }
                                } catch (EthereumException | IOException e) {
                                    subscribeCreated = false;
                                    LOG.error(e);
                                }

                                System.out.println("Pending transaction: " + transaction);
                            }

                    );
                    providedTransactions.forEach(transaction -> pendingTransactions.remove(transaction));
                }

                currentBlockNumber = ethBlock.getBlockNumber();
                LOG.info("Ethereum block: " + ethBlock.getBlockNumber());

                String recipient = ethBlock.getTo();
                if (accounts.contains(recipient)){
                    if (!ethereumNodeDao.isMerchantTransactionExists(ethBlock.getHash())){
                        BigDecimal amount = Convert.fromWei(String.valueOf(ethBlock.getValue()), Convert.Unit.ETHER);
                        LOG.debug("recipient: " + recipient + ", amount: " + amount);
                        pendingTransactions.add(ethBlock.getHash());
                        createTransaction(recipient, String.valueOf(amount), ethBlock.getHash());
                    }
                }

                });

        } catch (EthereumException e) {
            subscribeCreated = false;
            LOG.error(e);
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
            LOG.error(e);
            subscribeCreated = false;
        }
    }

    @Override
    @Transactional
    public String createAddress(CreditsOperation creditsOperation){
        try {

            File destination = new File(destinationDir);
            LOG.debug(destinationDir);

            String fileName = "";
            fileName = WalletUtils.generateLightNewWalletFile(password, destination);
            LOG.debug(fileName);
            Credentials credentials = WalletUtils.loadCredentials(password, destinationDir + fileName);

            EthereumAccount ethereumAccount = new EthereumAccount();
            ethereumAccount.setAddress(credentials.getAddress());
            ethereumAccount.setUser(creditsOperation.getUser());
            ethereumAccount.setPrivateKey(credentials.getEcKeyPair().getPrivateKey());
            ethereumAccount.setPublicKey(credentials.getEcKeyPair().getPublicKey());
            ethereumNodeDao.createAddress(ethereumAccount);
            accounts.add(ethereumAccount.getAddress());
            LOG.debug(ethereumAccount.getAddress());

            return ethereumAccount.getAddress();

        }catch (EthereumException | IOException | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException | NoSuchProviderException | CipherException e){
            LOG.error(e);
            return "";
        }
    }

    private void createTransaction(String address, String amount, String hash){
        /*try {
            String userEmail = ethereumNodeDao.findUserEmailByAddress(address);
            Payment payment = new Payment();
            Currency currency = currencyService.findByName("ETH");
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
                    LOG.debug("Creating transaction request ");
                    Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
                    ethereumNodeDao.createMerchantTransaction(address, hash, transaction.getId());
                    LOG.debug("Ethereum transaction " + transaction.toString() + " --- was created!!!");
                }
            });

        }catch (EthereumException e){
            subscribeCreated = false;
            LOG.error(e);
        }*/
    }

    private void provideTransactionAndTransferFunds(String merchantTransactionId){

        Transaction transaction = transactionService.findById(ethereumNodeDao.findTransactionId(merchantTransactionId));
        if (!transaction.isProvided()){
            transactionService.provideTransaction(transaction);

            LOG.debug("Ethereum transaction " + transaction.toString() + " --- PROVIDED!!!");
            transferFundsToMainAccount(ethereumNodeDao.findAddressByMerchantTransactionId(merchantTransactionId), transaction.getAmount());
        }
    }

    private void transferFundsToMainAccount(String address, BigDecimal amount){
        try {
            Optional<EthereumAccount> ethereumAccount = ethereumNodeDao.findByAddress(address);
            Credentials credentials = Credentials.create(new ECKeyPair(ethereumAccount.get().getPrivateKey(),
                    ethereumAccount.get().getPublicKey()));
            Transfer.sendFunds(
                    web3j, credentials, mainAddress, amount
                            .subtract(Convert.fromWei(Transfer.GAS_LIMIT.multiply(Transfer.GAS_PRICE).toString(), Convert.Unit.ETHER)), Convert.Unit.ETHER);
            LOG.debug("Funds " + amount + " sent to main account!!!");
        } catch (EthereumException| InterruptedException | ExecutionException | TransactionTimeoutException e) {
            subscribeCreated = false;
            LOG.error(e);
        }
    }

    @PreDestroy
    public void destroy() {
        LOG.debug("Destroing ethereum");
        scheduler.shutdown();
        subscription.unsubscribe();
        LOG.debug("Ethereum destroyed");
    }
}
