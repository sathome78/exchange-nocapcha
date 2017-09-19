package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.RefillRequestPutOnBchExamDto;
import me.exrates.service.EthereumCommonService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.ethTokensWrappers.Eos;
import me.exrates.service.events.EthPendingTransactionsEvent;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Maks on 19.09.2017.
 */
@Log4j2
@Service
public class EthTokenServiceImpl implements EthTokenService{

    private Merchant merchant;
    private Currency currency;
    private final List<String> accounts = new ArrayList<>();

    private String contractAddress;
    private String merchantName;
    private String currencyName;
    private Web3j web3j;

    @Autowired
    private RefillService refillService;
    @Autowired
    private CurrencyServiceImpl currencyService;
    @Autowired
    private MerchantService merchantService;
    @Qualifier(value = "ethereumServiceImpl")
    @Autowired
    private EthereumCommonService ethereumCommonService;

    @PostConstruct
    public void init() {
        merchant = merchantService.findByName(merchantName);
        currency = currencyService.findByName(currencyName);
        web3j = ethereumCommonService.getWeb3j();
    }

    public EthTokenServiceImpl(String contractAddress, String merchantName, String currencyName) {
        this.contractAddress = contractAddress;
        this.merchantName = merchantName;
        this.currencyName = currencyName;
    }

    @Override
    @Async
    @EventListener
    public void onPendingTransaction(EthPendingTransactionsEvent event) {
        Transaction transaction = (Transaction) event.getSource();
        if (transaction.getTo() != null && transaction.getTo().equals(contractAddress)){
            try {
                TransactionReceipt transactionReceipt = new TransactionReceipt();
                transactionReceipt = web3j.ethGetTransactionReceipt(transaction.getHash()).send().getResult();
                if (transactionReceipt == null) {
                    log.error("receipt null " + transaction.getHash());
                    return;
                }
                List<Log> logsList = transactionReceipt.getLogs();
                logsList.forEach(l -> {
                    TransferEventResponse response = extractData(l.getTopics(), l.getData());
                    if (response == null) {
                        log.debug("response null " + transaction.getHash());
                        return;
                    }

                    refillService.findAllAddresses(merchant.getId(), currency.getId()).forEach(accounts::add);
                    List<RefillRequestFlatDto> pendingTransactions = refillService.getInExamineByMerchantIdAndCurrencyIdList(merchant.getId(), currency.getId());

                    String contractRecipient = response.to.toString();
                    if (accounts.contains(contractRecipient)){
                        if (!refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(
                                contractRecipient,
                                merchant.getId(),
                                currency.getId(),
                                transaction.getHash()).isPresent()){
                            BigDecimal amount = Convert.fromWei(response.value.getValue().toString(), Convert.Unit.ETHER);
                            log.debug(merchantName + " recipient: " + contractRecipient + ", amount: " + amount);

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
                                log.error(e);
                            }
                            pendingTransactions.add(refillService.getFlatById(requestId));
                        }
                    }
                });
            } catch (Exception e) {
               log.error(e);
            }
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
