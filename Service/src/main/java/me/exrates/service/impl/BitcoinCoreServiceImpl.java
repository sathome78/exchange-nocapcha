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
import me.exrates.service.BitcoinCoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by OLEG on 14.03.2017.
 */
@Service
@Log4j2
public class BitcoinCoreServiceImpl implements BitcoinCoreService {
    
  @Autowired
  private BtcdClient btcdClient;
  
  
  @PostConstruct
  public void init() throws BitcoindException, CommunicationException {
  
    BtcdDaemon daemon = new BtcdDaemonImpl(btcdClient);
    daemon.addBlockListener(new BlockListener() {
      @Override
      public void blockDetected(Block block) {
        log.debug(String.format("Block detected: hash %s ", block.getHash()));
        try {
          Block fullBlock = btcdClient.getBlock(block.getHash());
          log.debug(fullBlock);
        } catch (BitcoindException | CommunicationException e) {
          log.error(e);
        }
      }
    });
    daemon.addWalletListener(new WalletListener() {
      @Override
      public void walletChanged(Transaction transaction) {
        log.debug(String.format("Wallet change: tx id %s", transaction.getTxId()));
        
        try {
          Transaction fullTransaction = btcdClient.getTransaction(transaction.getTxId());
          log.debug(fullTransaction);
        } catch (BitcoindException | CommunicationException e) {
          log.error(e);
        }
      }
    });
    
  }
  }
