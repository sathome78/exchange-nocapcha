package me.exrates.service.impl;

import me.exrates.dao.EthereumNodeDao;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.service.*;
import me.exrates.service.exception.EthereumException;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
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
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.web3j.tx.Contract.GAS_LIMIT;
import static org.web3j.tx.ManagedTransaction.GAS_PRICE;

/**
 * Created by ajet
 */
//@Service
public class EthereumCommonServiceImpl implements EthereumCommonService {

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

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RefillService refillService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private MerchantSpecParamsDao specParamsDao;

    @Qualifier(value = "eosServiceImpl")
    @Autowired
    private EthTokenService eosServiceImpl;

    @Qualifier(value = "repServiceImpl")
    @Autowired
    private EthTokenService repServiceImpl;

    @Qualifier(value = "golemServiceImpl")
    @Autowired
    private EthTokenService golemServiceImpl;

    @Qualifier(value = "omgServiceImpl")
    @Autowired
    private EthTokenService omgServiceImpl;

    @Qualifier(value = "bnbServiceImpl")
    @Autowired
    private EthTokenService bnbServiceImpl;

    private String url;

    private String destinationDir;

    private String password;

    private String mainAddress;

    private final List<String> accounts = new ArrayList<>();

    private final List<String> pendingTransactions = new ArrayList<>();

    private Web3j web3j;

    private Observable<org.web3j.protocol.core.methods.response.Transaction> observable;

    private Subscription subscription;

    private boolean subscribeCreated = false;

    private BigInteger currentBlockNumber;

    private String merchantName;

    private String currencyName;

    private Integer minConfirmations;

    private Credentials credentialsMain;

    private String transferAccPrivateKey;

    private String transferAccPublicKey;

    private BigDecimal minBalanceForTransfer = new BigDecimal("0.015");

    @Override
    public Web3j getWeb3j() {
        return web3j;
    }

    @Override
    public List<String> getAccounts() {
        return accounts;
    }

    @Override
    public String getMainAddress() {
        return mainAddress;
    }

    @Override
    public Credentials getCredentialsMain() {
        return credentialsMain;
    }

    @Override
    public Integer minConfirmationsRefill() {
        return minConfirmations;
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Logger LOG = LogManager.getLogger("node_ethereum");

    private static final String LAST_BLOCK_PARAM = "LastRecievedBlock";

    public EthereumCommonServiceImpl(String propertySource, String merchantName, String currencyName, Integer minConfirmations) {
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertySource));
            this.url = props.getProperty("ethereum.url");
            this.destinationDir = props.getProperty("ethereum.destinationDir");
            this.password = props.getProperty("ethereum.password");
            this.mainAddress = props.getProperty("ethereum.mainAddress");
            this.merchantName = merchantName;
            this.currencyName = currencyName;
            this.minConfirmations = minConfirmations;
            if (merchantName.equals("Ethereum")){
                this.transferAccPrivateKey = props.getProperty("ethereum.transferAccPrivateKey");
                this.transferAccPublicKey = props.getProperty("ethereum.transferAccPublicKey");
            }
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
        }, 0, 5, TimeUnit.MINUTES);

        scheduler.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    transferFundsToMainAccount();
                }catch (Exception e){
                    LOG.error(e);
                }
            }
        }, 60, 120, TimeUnit.MINUTES);

        scheduler.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    if (subscribeCreated == true) {
                       saveLastBlock(currentBlockNumber.toString());
                    }
                }catch (Exception e){
                    LOG.error(e);
                }
            }
        }, 1, 24, TimeUnit.HOURS);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        return new HashMap<>();
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        throw new NotImplimentedMethod("for " + params);
    }

    public void createSubscribe(){
        try {
            LOG.debug(merchantName + " Connecting ethereum...");

            Merchant merchant = merchantService.findByName(merchantName);
            Currency currency = currencyService.findByName(currencyName);
            if (merchantName.equals("Ethereum")) {
                credentialsMain = Credentials.create(new ECKeyPair(new BigInteger(transferAccPrivateKey),
                        new BigInteger(transferAccPublicKey)));
            }

            refillService.findAllAddresses(merchant.getId(), currency.getId()).forEach(address -> accounts.add(address));
            List<RefillRequestFlatDto> pendingTransactions = refillService.getInExamineByMerchantIdAndCurrencyIdList(merchant.getId(), currency.getId());
            subscribeCreated = true;
            currentBlockNumber = new BigInteger("0");

            observable = web3j.catchUpToLatestAndSubscribeToNewTransactionsObservable(new DefaultBlockParameterNumber(Long.parseLong(loadLastBlock())));
            subscription = observable.subscribe(ethBlock -> {
                if (merchantName.equals("Ethereum")) {
                    if (ethBlock.getFrom().equals(credentialsMain.getAddress())) {
                        return;
                    }
                }

                if (!currentBlockNumber.equals(ethBlock.getBlockNumber())){
                    LOG.debug(merchantName + " Current block number: " + ethBlock.getBlockNumber());

                    List<RefillRequestFlatDto> providedTransactions = new ArrayList<RefillRequestFlatDto>();
                    pendingTransactions.forEach(transaction ->
                            {
                                try {
                                    if (web3j.ethGetTransactionByHash(transaction.getMerchantTransactionId()).send().getResult()==null){
                                        return;
                                    }
                                    BigInteger transactionBlockNumber = web3j.ethGetTransactionByHash(transaction.getMerchantTransactionId()).send().getResult().getBlockNumber();
                                    if (ethBlock.getBlockNumber().subtract(transactionBlockNumber).intValue() > minConfirmations){
                                        provideTransactionAndTransferFunds(transaction.getAddress(), transaction.getMerchantTransactionId());
                                        saveLastBlock(ethBlock.getBlockNumber().toString());
                                        LOG.debug(merchantName + " Transaction: " + transaction + " - PROVIDED!!!");
                                        LOG.debug(merchantName + " Confirmations count: " + ethBlock.getBlockNumber().subtract(transactionBlockNumber).intValue());
                                        providedTransactions.add(transaction);
                                    }
                                } catch (EthereumException | IOException e) {
                                    subscribeCreated = false;
                                    LOG.error(merchantName + " " + e);
                                }

                            }

                    );
                    providedTransactions.forEach(transaction -> pendingTransactions.remove(transaction));
                }

                currentBlockNumber = ethBlock.getBlockNumber();
                LOG.info(merchantName + " block: " + ethBlock.getBlockNumber());

//  --------------EOS token
                if (ethBlock.getTo() != null && eosServiceImpl.getContractAddress().contains(ethBlock.getTo()) && merchantName.equals("Ethereum")){
                    eosServiceImpl.tokenTransaction(ethBlock);
                }
// ------------------------

//  --------------REP token
                if (ethBlock.getTo() != null && repServiceImpl.getContractAddress().contains(ethBlock.getTo()) && merchantName.equals("Ethereum")){
                   repServiceImpl.tokenTransaction(ethBlock);
                }
// ------------------------

//  --------------Golem token
                if (ethBlock.getTo() != null && golemServiceImpl.getContractAddress().contains(ethBlock.getTo()) && merchantName.equals("Ethereum")){
                    golemServiceImpl.tokenTransaction(ethBlock);
                }
// ------------------------

// --------------OMG token
                if (ethBlock.getTo() != null && omgServiceImpl.getContractAddress().contains(ethBlock.getTo()) && merchantName.equals("Ethereum")){
                    omgServiceImpl.tokenTransaction(ethBlock);
                }
// ------------------------

// --------------BNB token
                if (ethBlock.getTo() != null && bnbServiceImpl.getContractAddress().contains(ethBlock.getTo()) && merchantName.equals("Ethereum")){
                    bnbServiceImpl.tokenTransaction(ethBlock);
                }
// ------------------------


                String recipient = ethBlock.getTo();

                if (accounts.contains(recipient)){
                    if (!refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(recipient, merchant.getId(), currency.getId(), ethBlock.getHash()).isPresent()){
                        BigDecimal amount = Convert.fromWei(String.valueOf(ethBlock.getValue()), Convert.Unit.ETHER);
                        LOG.debug(merchantName + " recipient: " + recipient + ", amount: " + amount);

                        Integer requestId = refillService.createRefillRequestByFact(RefillRequestAcceptDto.builder()
                                        .address(recipient)
                                        .amount(amount)
                                        .merchantId(merchant.getId())
                                        .currencyId(currency.getId())
                                        .merchantTransactionId(ethBlock.getHash()).build());

                        try {
                            refillService.putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto.builder()
                                    .requestId(requestId)
                                    .merchantId(merchant.getId())
                                    .currencyId(currency.getId())
                                    .address(recipient)
                                    .amount(amount)
                                    .hash(ethBlock.getHash())
                                    .blockhash(ethBlock.getBlockNumber().toString()).build());
                        } catch (RefillRequestAppropriateNotFoundException e) {
                            LOG.error(e);
                        }

                        pendingTransactions.add(refillService.getFlatById(requestId));

                    }
                }

                });

        } catch (Exception e) {
            subscribeCreated = false;
            LOG.error(merchantName + " " + e);
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
            LOG.error(merchantName + " " + e);
            subscribeCreated = false;
        }
    }

    @Override
    @Transactional
    public Map<String, String> refill(RefillRequestCreateDto request) {

        Map<String, String> mapAddress = new HashMap<>();
        try {

            File destination = new File(destinationDir);
            LOG.debug(merchantName + " " + destinationDir);

            String fileName = "";
            fileName = WalletUtils.generateLightNewWalletFile(password, destination);
            LOG.debug(merchantName + " " + fileName);
            Credentials credentials = WalletUtils.loadCredentials(password, destinationDir + fileName);
            String address = credentials.getAddress();

            accounts.add(address);
            LOG.debug(merchantName + " " + address);
            mapAddress.put("address", address);
            mapAddress.put("privKey", String.valueOf(credentials.getEcKeyPair().getPrivateKey()));
            mapAddress.put("pubKey", String.valueOf(credentials.getEcKeyPair().getPublicKey()));

        }catch (EthereumException | IOException | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException | NoSuchProviderException | CipherException e){
            LOG.error(merchantName + " " + e);
        }


        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{mapAddress.get("address")}, request.getLocale());

        mapAddress.put("message", message);
        mapAddress.put("qr", mapAddress.get("address"));

        return mapAddress;
    }

    private void provideTransactionAndTransferFunds(String address, String merchantTransactionId){

        try {
            Optional<RefillRequestBtcInfoDto> refillRequestInfoDto = refillService.findRefillRequestByAddressAndMerchantTransactionId(address, merchantTransactionId, merchantName, currencyName);
            LOG.debug("Providing transaction!");
            RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                        .requestId(refillRequestInfoDto.get().getId())
                        .address(refillRequestInfoDto.get().getAddress())
                        .amount(refillRequestInfoDto.get().getAmount())
                        .currencyId(currencyService.findByName(currencyName).getId())
                        .merchantId(merchantService.findByName(merchantName).getId())
                        .merchantTransactionId(merchantTransactionId)
                        .build();
                refillService.autoAcceptRefillRequest(requestAcceptDto);
                LOG.debug(merchantName + " Ethereum transaction " + requestAcceptDto.toString() + " --- PROVIDED!!!");

                refillService.updateAddressNeedTransfer(requestAcceptDto.getAddress(), merchantService.findByName(merchantName).getId(), currencyService.findByName(currencyName).getId(), true);

        } catch (Exception e) {
            LOG.error(e);
        }

    }

    private void transferFundsToMainAccount(){
        List<RefillRequestAddressDto> listRefillRequestAddressDto = refillService.findAllAddressesNeededToTransfer(merchantService.findByName(merchantName).getId(), currencyService.findByName(currencyName).getId());
        for (RefillRequestAddressDto refillRequestAddressDto : listRefillRequestAddressDto) {
            try {
                LOG.info("Start method transferFundsToMainAccount...");
                BigDecimal ethBalance = Convert.fromWei(String.valueOf(web3j.ethGetBalance(refillRequestAddressDto.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance()), Convert.Unit.ETHER);

                if ( ethBalance.compareTo(minBalanceForTransfer) <= 0){
                    refillService.updateAddressNeedTransfer(refillRequestAddressDto.getAddress(), merchantService.findByName(merchantName).getId(),
                            currencyService.findByName(currencyName).getId(), false);
                    continue;
                }

                Credentials credentials = Credentials.create(new ECKeyPair(new BigInteger(refillRequestAddressDto.getPrivKey()),
                        new BigInteger(refillRequestAddressDto.getPubKey())));
                LOG.info("Credentials pubKey: " + credentials.getEcKeyPair().getPublicKey());
                Transfer.sendFunds(
                        web3j, credentials, mainAddress, ethBalance
                                .subtract(Convert.fromWei(Transfer.GAS_LIMIT.multiply(web3j.ethGasPrice().send().getGasPrice()).toString(), Convert.Unit.ETHER)), Convert.Unit.ETHER);
                LOG.debug(merchantName + " Funds " + ethBalance + " sent to main account!!!");
            } catch (Exception e) {
                subscribeCreated = false;
                LOG.error(merchantName + " " + e);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        LOG.debug("Destroying " + merchantName);
        scheduler.shutdown();
        subscription.unsubscribe();
        LOG.debug(merchantName + " destroyed");
    }

    public void saveLastBlock(String block) {
        specParamsDao.updateParam(merchantName, LAST_BLOCK_PARAM, block);
    }

    public String loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantIdAndParamName(merchantName, LAST_BLOCK_PARAM);
        return specParamsDto == null ? null : specParamsDto.getParamValue();
    }

}
