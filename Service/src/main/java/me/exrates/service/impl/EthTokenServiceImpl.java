package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestBtcInfoDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.RefillRequestPutOnBchExamDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.EthereumCommonService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.events.EthPendingTransactionsEvent;
import me.exrates.service.exception.EthereumException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.merchantStrategy.IMerchantService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by Maks on 19.09.2017.
 */
@Log4j2
@Service
public class EthTokenServiceImpl implements EthTokenService{

    private Merchant merchant;
    private Currency currency;
    private List<String> contractAddress;
    private String merchantName;
    private String currencyName;
    private int minConfirmations;
    private BigInteger currentBlockNumber;
    List<RefillRequestFlatDto> pendingTransactions;

    @Autowired
    private RefillService refillService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;

    @Qualifier(value = "ethereumServiceImpl")
    @Autowired
    private EthereumCommonService ethereumCommonService;


    private final Logger LOG = LogManager.getLogger("node_ethereum");

    @PostConstruct
    public void init() {
        merchant = merchantService.findByName(merchantName);
        currency = currencyService.findByName(currencyName);
        currentBlockNumber = new BigInteger("0");
        pendingTransactions = refillService.getInExamineByMerchantIdAndCurrencyIdList(merchant.getId(), currency.getId());
        this.minConfirmations = ethereumCommonService.minConfirmationsRefill();
    }

    public EthTokenServiceImpl(List<String> contractAddress, String merchantName, String currencyName) {
        this.contractAddress = contractAddress;
        this.merchantName = merchantName;
        this.currencyName = currencyName;
    }

    @Override
    public List<String> getContractAddress(){
        return contractAddress;
    }

    @Override
    public void tokenTransaction(Transaction transaction){
        try {
            if (!currentBlockNumber.equals(transaction.getBlockNumber())){
                System.out.println(merchant.getName() + " Current block number: " + transaction.getBlockNumber());
                LOG.debug(merchant.getName() + " Current block number: " + transaction.getBlockNumber());

                List<RefillRequestFlatDto> providedTransactions = new ArrayList<RefillRequestFlatDto>();
                pendingTransactions.forEach(pendingTransaction ->
                        {
                            try {
                                if (ethereumCommonService.getWeb3j().ethGetTransactionByHash(pendingTransaction.getMerchantTransactionId()).send().getResult()==null){
                                    return;
                                }
                                BigInteger transactionBlockNumber = ethereumCommonService.getWeb3j().ethGetTransactionByHash(pendingTransaction.getMerchantTransactionId()).send().getResult().getBlockNumber();
                                if (transaction.getBlockNumber().subtract(transactionBlockNumber).intValue() > minConfirmations){

                                    provideTransactionAndTransferFunds(pendingTransaction.getAddress(), pendingTransaction.getMerchantTransactionId());
                                    ethereumCommonService.saveLastBlock(transaction.getBlockNumber().toString());
                                    LOG.debug(merchant.getName() + " Transaction: " + pendingTransaction + " - PROVIDED!!!");
                                    LOG.debug(merchant.getName() + " Confirmations count: " + transaction.getBlockNumber().subtract(transactionBlockNumber).intValue());
                                    providedTransactions.add(pendingTransaction);
                                }
                            } catch (EthereumException | IOException e) {
                                LOG.error(merchant.getName() + " " + e);
                            }

                            System.out.println(merchant.getName() + " Pending transaction: " + pendingTransaction);
                        }

                );
                providedTransactions.forEach(pendingTransaction -> pendingTransactions.remove(pendingTransaction));
            }

            System.out.println("transaction1.getBlockNumber(): " + transaction.getBlockNumber());
            currentBlockNumber = transaction.getBlockNumber();
            System.out.println("currentBlockNumberEos: " + currentBlockNumber);

            TransactionReceipt transactionReceipt = new TransactionReceipt();
            transactionReceipt = ethereumCommonService.getWeb3j().ethGetTransactionReceipt(transaction.getHash()).send().getResult();
            if (transactionReceipt == null) {
                LOG.error("receipt null " + transaction.getHash());
                return;
            }
            List<Log> logsList = transactionReceipt.getLogs();
            logsList.forEach(l -> {
                TransferEventResponse response = extractData(l.getTopics(), l.getData());
                if (response == null) {
                    LOG.debug("response null " + transaction.getHash());
                    return;
                }

                String contractRecipient = response.to.toString();
                if (ethereumCommonService.getAccounts().contains(contractRecipient)){
                    if (!refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(
                            contractRecipient,
                            merchant.getId(),
                            currency.getId(),
                            transaction.getHash()).isPresent()){
                        BigDecimal amount = Convert.fromWei(response.value.getValue().toString(), Convert.Unit.ETHER);
                        LOG.debug(merchant.getName() + " recipient: " + contractRecipient + ", amount: " + amount);

                        Integer requestId = refillService.createRefillRequestByFact(RefillRequestAcceptDto.builder()
                                .address(contractRecipient)
                                .amount(amount)
                                .merchantId(merchant.getId())
                                .currencyId(currency.getId())
                                .merchantTransactionId(transaction.getHash()).build());
                        try {
                            refillService.putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto.builder()
                                    .requestId(requestId)
                                    .merchantId(merchant.getId())
                                    .currencyId(currency.getId())
                                    .address(contractRecipient)
                                    .amount(amount)
                                    .hash(transaction.getHash())
                                    .blockhash(transaction.getBlockNumber().toString()).build());
                        } catch (RefillRequestAppropriateNotFoundException e) {
                            LOG.error(e);
                        }
                        pendingTransactions.add(refillService.getFlatById(requestId));
                    }
                }
            });
        } catch (Exception e) {
            LOG.error(e);
        }
        System.out.println(transaction);
    }

    private void provideTransactionAndTransferFunds(String address, String merchantTransactionId){

        try {
            Optional<RefillRequestBtcInfoDto> refillRequestInfoDto = refillService.findRefillRequestByAddressAndMerchantTransactionId(address, merchantTransactionId, merchantName, currencyName);
            log.debug("Providing transaction!");
            RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                    .requestId(refillRequestInfoDto.get().getId())
                    .address(refillRequestInfoDto.get().getAddress())
                    .amount(refillRequestInfoDto.get().getAmount())
                    .currencyId(currencyService.findByName(currencyName).getId())
                    .merchantId(merchantService.findByName(merchantName).getId())
                    .merchantTransactionId(merchantTransactionId)
                    .build();
            refillService.autoAcceptRefillRequest(requestAcceptDto);
            log.debug(merchantName + " Ethereum transaction " + requestAcceptDto.toString() + " --- PROVIDED!!!");

//            transferFundsToMainAccount(refillService.getRefillRequestById(requestAcceptDto.getRequestId(), "ajet5911@gmail.com"));
        } catch (RefillRequestAppropriateNotFoundException e) {
        } catch (Exception e) {
            log.error(e);
        }

    }

    @Override
    public TransferEventResponse extractData(List<String> topics, String data) {
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
        TransferEventResponse typedResponse = new TransferEventResponse();
        typedResponse.from = (Address) eventValues.getIndexedValues().get(0);
        typedResponse.to = (Address) eventValues.getIndexedValues().get(1);
        typedResponse.value = (Uint256) eventValues.getNonIndexedValues().get(0);
        return typedResponse;
    }

    static class TransferEventResponse {
        public Address from;

        public Address to;

        public Uint256 value;
    }
}
