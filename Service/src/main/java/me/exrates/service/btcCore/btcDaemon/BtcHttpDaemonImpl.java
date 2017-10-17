package me.exrates.service.btcCore.btcDaemon;

import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.neemre.btcdcli4j.daemon.BtcdDaemon;
import com.neemre.btcdcli4j.daemon.BtcdDaemonImpl;
import com.neemre.btcdcli4j.daemon.event.BlockListener;
import com.neemre.btcdcli4j.daemon.event.InstantSendListener;
import com.neemre.btcdcli4j.daemon.event.WalletListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Log4j2(topic = "bitcoin_core")
public class BtcHttpDaemonImpl implements BtcDaemon {

    private BtcdClient btcdClient;

    private BtcdDaemon daemon;

    public BtcHttpDaemonImpl(BtcdClient btcdClient) {
        this.btcdClient = btcdClient;
    }

    @Override
    public void init(Consumer<Block> blockHandler,
                     Consumer<Transaction> walletHandler,
                     Consumer<Transaction> instantSendHandler) {
        try {
            daemon = new BtcdDaemonImpl(btcdClient);
            daemon.addBlockListener(new BlockListener() {
                @Override
                public void blockDetected(Block block) {
                    log.debug(String.format("Block detected: hash %s, height %s ", block.getHash(), block.getHeight()));
                    blockHandler.accept(block);
                }
            });
            daemon.addWalletListener(new WalletListener() {
                @Override
                public void walletChanged(Transaction transaction) {
                    log.debug(String.format("Wallet change: tx %s", transaction.getTxId()));
                    walletHandler.accept(transaction);
                }
            });
            daemon.addInstantSendListener(new InstantSendListener() {
                @Override
                public void transactionBlocked(Transaction transaction) {
                    log.debug(String.format("Transaction blocked: tx %s", transaction.getTxId()));
                    instantSendHandler.accept(transaction);
                }
            });
        } catch (Exception e) {
            log.error(e);
        }
    }

}
