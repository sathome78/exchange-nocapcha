package me.exrates.service.btcCore.btcDaemon;

import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.Transaction;

import java.util.function.Consumer;

public interface BtcDaemon {


    void init(Consumer<Block> blockHandler,
              Consumer<Transaction> walletHandler,
              Consumer<Transaction> instantSendHandler);
}
