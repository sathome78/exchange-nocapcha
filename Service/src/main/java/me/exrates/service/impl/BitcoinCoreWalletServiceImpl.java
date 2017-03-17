package me.exrates.service.impl;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.neemre.btcdcli4j.daemon.BtcdDaemon;
import com.neemre.btcdcli4j.daemon.BtcdDaemonImpl;
import com.neemre.btcdcli4j.daemon.event.BlockListener;
import com.neemre.btcdcli4j.daemon.event.WalletListener;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.BitcoinWalletService;
import me.exrates.service.exception.BitcoinCoreException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by OLEG on 14.03.2017.
 */
@Component("BitcoinCoreService")
@Log4j2
public class BitcoinCoreWalletServiceImpl implements BitcoinWalletService {
    
  @Autowired
  private BtcdClient btcdClient;
  private BtcdDaemon daemon;
  
  private Map<Integer, String> transactionsWaitingForConfirmations = new ConcurrentHashMap<>();
  
  
  
  @Override
  public void initBitcoin() {
  
    try {
      daemon = new BtcdDaemonImpl(btcdClient);
      daemon.addBlockListener(new BlockListener() {
        @Override
        public void blockDetected(Block block) {
          log.debug(String.format("Block detected: hash %s ", block.getHash()));
          log.debug(String.format("Block %s ", block.toString()));
          
        }
      });
      daemon.addWalletListener(new WalletListener() {
        @Override
        public void walletChanged(Transaction transaction) {
          log.debug(String.format("Wallet change: tx id %s", transaction.getTxId()));
          log.debug(String.format("TX %s ", transaction.toString()));
        }
      });
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
    }
  
  }
  
  @Override
  public String getNewAddress() {
    try {
      return btcdClient.getNewAddress();
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException("Cannot generate new address!");
    }
  }
  
  @PreDestroy
  public void shutdownDaemon() {
    daemon.removeAlertListeners();
    daemon.removeBlockListeners();
    daemon.removeWalletListeners();
    daemon.shutdown();
  }
  
  
  
}
