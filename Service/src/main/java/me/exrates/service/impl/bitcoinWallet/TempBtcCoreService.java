package me.exrates.service.impl.bitcoinWallet;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.neemre.btcdcli4j.daemon.BtcdDaemon;
import com.neemre.btcdcli4j.daemon.BtcdDaemonImpl;
import com.neemre.btcdcli4j.daemon.event.BlockListener;
import com.neemre.btcdcli4j.daemon.event.WalletListener;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.exception.BitcoinCoreException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by OLEG on 27.03.2017.
 */
//TODO remove class after BtcCore update is ready

@Log4j2(topic = "bitcoin_core")
public class TempBtcCoreService {
  private BtcdClient btcdClient;
  private BtcdDaemon daemon;
  
  public void initClientAndDaemon() {
    try {
      PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
      CloseableHttpClient httpProvider = HttpClients.custom().setConnectionManager(cm)
              .build();
      Properties nodeConfig = new Properties();
      nodeConfig.load(getClass().getClassLoader().getResourceAsStream("node_config.properties"));
      log.debug("Node config: " + nodeConfig);
      btcdClient = new BtcdClientImpl(httpProvider, nodeConfig);
    
      daemon = new BtcdDaemonImpl(btcdClient);
      daemon.addBlockListener(new BlockListener() {
        @Override
        public void blockDetected(Block block) {
          log.debug(String.format("Block detected: hash %s ", block.toString()));
        }
      });
      daemon.addWalletListener(new WalletListener() {
        @Override
        public void walletChanged(Transaction transaction) {
          log.debug(String.format("Wallet change: tx id %s", transaction.toString()));
        }
      });
    } catch (IOException | BitcoindException | CommunicationException e) {
      log.error(e.getMessage());
      e.printStackTrace();
    }
  }
  
  public String getInfo() {
    try {
      return btcdClient.getInfo().toString();
    } catch (BitcoindException | CommunicationException e) {
      log.error(e.getMessage());
      e.printStackTrace();
      throw new BitcoinCoreException(e.getMessage());
    }
  }
}
