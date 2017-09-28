package me.exrates.service.impl;

import me.exrates.service.events.EthPendingTransactionsEvent;
import me.exrates.service.merchantStrategy.IMerchantService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.web3j.protocol.core.methods.response.Transaction;

import java.util.List;

/**
 * Created by Maks on 19.09.2017.
 */
public interface EthTokenService {

    List<String> getContractAddress();

    void tokenTransaction(Transaction transaction);

    EthTokenServiceImpl.TransferEventResponse extractData(List<String> topics, String data);
}
