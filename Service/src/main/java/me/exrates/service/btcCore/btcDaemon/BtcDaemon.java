package me.exrates.service.btcCore.btcDaemon;

import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.Transaction;
import reactor.core.publisher.Flux;

public interface BtcDaemon {


    void init();

    void destroy();

    Flux<Block> blockFlux(String port);

    Flux<Transaction> walletFlux(String port);

    Flux<Transaction> instantSendFlux(String port);
}
