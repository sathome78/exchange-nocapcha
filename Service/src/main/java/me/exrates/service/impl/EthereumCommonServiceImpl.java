package me.exrates.service.impl;

import me.exrates.dao.EthereumNodeDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.service.*;
import me.exrates.service.ethTokensWrappers.Eos;
import me.exrates.service.ethTokensWrappers.Rep;
import me.exrates.service.exception.EthereumException;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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

    private String merchantName;

    private String currencyName;

    private Integer minConfirmations;

    private Credentials mainWalletETH;

    private Eos eosContract;

    @Override
    public Integer minConfirmationsRefill() {
        return minConfirmations;
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Logger LOG = LogManager.getLogger("node_ethereum");

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

            if (currencyName.equals("ETH")){
                mainWalletETH = WalletUtils.loadCredentials("jet2103",
                        "d:/Ethereum1/Original/keystore/UTC--2017-03-28T13-44-46.443655600Z--061372f91f949effb934abadff5f0636de09113d");
                eosContract = Eos.load("0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0", web3j, mainWalletETH, GAS_PRICE, GAS_LIMIT);
            }


            refillService.findAllAddresses(merchant.getId(), currency.getId()).forEach(address -> accounts.add(address));
            List<RefillRequestFlatDto> pendingTransactions = refillService.getInExamineByMerchantIdAndCurrencyIdList(merchant.getId(), currency.getId());
            subscribeCreated = true;
            currentBlockNumber = new BigInteger("0");

            observable = web3j.transactionObservable();
            subscription = observable.subscribe(ethBlock -> {

                if (!currentBlockNumber.equals(ethBlock.getBlockNumber())){
                    System.out.println(merchantName + " Current block number: " + ethBlock.getBlockNumber());
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
                                        LOG.debug(merchantName + " Transaction: " + transaction + " - PROVIDED!!!");
                                        LOG.debug(merchantName + " Confirmations count: " + ethBlock.getBlockNumber().subtract(transactionBlockNumber).intValue());
                                        providedTransactions.add(transaction);
                                    }
                                } catch (EthereumException | IOException e) {
                                    subscribeCreated = false;
                                    LOG.error(merchantName + " " + e);
                                }

                                System.out.println(merchantName + " Pending transaction: " + transaction);
                            }

                    );
                    providedTransactions.forEach(transaction -> pendingTransactions.remove(transaction));
                }

                currentBlockNumber = ethBlock.getBlockNumber();
                LOG.info(merchantName + " block: " + ethBlock.getBlockNumber());

// -------------------- EOS
                if (ethBlock.getTo().equals("0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0")){
                    try {
                        TransactionReceipt transactionReceipt = new TransactionReceipt();
                        transactionReceipt = web3j.ethGetTransactionReceipt(ethBlock.getHash()).send().getResult();
                        System.out.println(transactionReceipt);
                        Log log = transactionReceipt.getLogs().get(0);
                        Eos.TransferEventResponse response = extractData(log.getTopics(), log.getData());
                        if (response == null) {
                            return;
                        }
                        /*List<Eos.TransferEventResponse> receipt = eosContract.getTransferEvents(transactionReceipt);*/
                       /* String contractRecipient = receipt.get(0).to.toString();*/
                        String contractRecipient = response.to.toString();
                        if (accounts.contains(contractRecipient)){
                            if (!refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(contractRecipient, merchant.getId(), currency.getId(), ethBlock.getHash()).isPresent()){
                               /* BigDecimal amount = Convert.fromWei(String.valueOf(receipt.get(0).value), Convert.Unit.ETHER);*/
                                BigDecimal amount = Convert.fromWei(response.value.getValue().toString(), Convert.Unit.ETHER);
                                LOG.debug(merchantName + " recipient: " + contractRecipient + ", amount: " + amount);

                                Integer requestId = refillService.createRefillRequestByFact(RefillRequestAcceptDto.builder()
                                        .address(contractRecipient)
                                        .amount(amount)
                                        .merchantId(merchant.getId())
                                        .currencyId(currency.getId())
                                        .merchantTransactionId(ethBlock.getHash()).build());

                                try {
                                    refillService.putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto.builder()
                                            .requestId(requestId)
                                            .merchantId(merchant.getId())
                                            .currencyId(currency.getId())
                                            .address(contractRecipient)
                                            .amount(amount)
                                            .hash(ethBlock.getHash())
                                            .blockhash(ethBlock.getBlockNumber().toString()).build());
                                } catch (RefillRequestAppropriateNotFoundException e) {
                                    LOG.error(e);
                                }

                                pendingTransactions.add(refillService.getFlatById(requestId));

                            }                        }
                    } catch (Exception e) {
                        LOG.error(e);
                    }
                }
// -------------------- /EOS

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

//                transferFundsToMainAccount(refillService.getRefillRequestById(requestAcceptDto.getRequestId(), "ajet5911@gmail.com"));
//        } catch (RefillRequestAppropriateNotFoundException e) {
        } catch (Exception e) {
            LOG.error(e);
        }

    }

    private void transferFundsToMainAccount(RefillRequestsAdminTableDto refillRequest){
        try {
            LOG.info("Start method transferFundsToMainAccount...");
            Credentials credentials = Credentials.create(new ECKeyPair(new BigInteger(refillRequest.getPrivKey()),
                    new BigInteger(refillRequest.getPubKey())));
            LOG.info("Credentials pubKey: " + credentials.getEcKeyPair().getPublicKey());
            Transfer.sendFunds(
                    web3j, credentials, mainAddress, refillRequest.getAmount()
                            .subtract(Convert.fromWei(Transfer.GAS_LIMIT.multiply(Transfer.GAS_PRICE).toString(), Convert.Unit.ETHER)), Convert.Unit.ETHER);
            LOG.debug(merchantName + " Funds " + refillRequest.getAmount() + " sent to main account!!!");
        } catch (Exception e) {
            subscribeCreated = false;
            LOG.error(merchantName + " " + e);
        }
    }

    @PreDestroy
    public void destroy() {
        LOG.debug("Destroing " + merchantName);
        scheduler.shutdown();
        subscription.unsubscribe();
        LOG.debug(merchantName + " destroyed");
    }

    private static Eos.TransferEventResponse extractData(List<String> topics, String data) {
        final Event event = new Event("Transfer",
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        String encodedEventSignature = EventEncoder.encode(event);
        if (!topics.get(0).equals(encodedEventSignature)) {
            return null;
        }
        System.out.println("event signature " + encodedEventSignature);
        List<Type> indexedValues = new ArrayList<>();
        List<Type> nonIndexedValues = FunctionReturnDecoder.decode(
                data, event.getNonIndexedParameters());
        List<TypeReference<Type>> indexedParameters = event.getIndexedParameters();
        for (int i = 0; i < indexedParameters.size(); i++) {
            Type value = FunctionReturnDecoder.decodeIndexedValue(
                    topics.get(i + 1), indexedParameters.get(i));
            indexedValues.add(value);
        }
        EventValues eventValues = new EventValues(indexedValues, nonIndexedValues);
        Eos.TransferEventResponse typedResponse = new Eos.TransferEventResponse();
        typedResponse.from = (Address) eventValues.getIndexedValues().get(0);
        typedResponse.to = (Address) eventValues.getIndexedValues().get(1);
        typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
        return typedResponse;
    }
}
