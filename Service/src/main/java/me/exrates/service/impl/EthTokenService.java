package me.exrates.service.impl;

import me.exrates.service.events.EthPendingTransactionsEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * Created by Maks on 19.09.2017.
 */
public interface EthTokenService {


    @Async
    @EventListener
    void onPendingTransaction(EthPendingTransactionsEvent event);

    EthTokenServiceImpl.TransferEventResponse extractData(List<String> topics, String data);
}
