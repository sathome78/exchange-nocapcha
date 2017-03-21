package me.exrates.service.impl;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.Output;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.neemre.btcdcli4j.daemon.BtcdDaemon;
import com.neemre.btcdcli4j.daemon.BtcdDaemonImpl;
import com.neemre.btcdcli4j.daemon.event.BlockListener;
import com.neemre.btcdcli4j.daemon.event.WalletListener;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.PendingPayment;
import me.exrates.model.dto.onlineTableDto.PendingPaymentStatusDto;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;
import me.exrates.model.vo.SimpleBtcPayment;
import me.exrates.service.BitcoinService;
import me.exrates.service.BitcoinTransactionService;
import me.exrates.service.BitcoinWalletService;
import me.exrates.service.exception.BitcoinCoreException;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.IllegalTransactionProvidedStatusException;
import me.exrates.service.exception.invoice.IllegalInvoiceAmountException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by OLEG on 14.03.2017.
 */
@Component("BitcoinCoreService")
@Log4j2(topic = "bitcoin_core")
public class BitcoinCoreWalletServiceImpl implements BitcoinWalletService {
    
  private BtcdClient btcdClient;
  
  @Autowired
  private BitcoinTransactionService bitcoinTransactionService;
  
  private BtcdDaemon daemon;
  
  
  @Override
  public void initBitcoin() {
  
    try {
      btcdClient = initBitcoindClient();
      daemon = new BtcdDaemonImpl(btcdClient);
      daemon.addBlockListener(new BlockListener() {
        @Override
        public void blockDetected(Block block) {
          log.debug(String.format("Block detected: hash %s ", block.toString()));
          List<PendingPayment> unconfirmedPayments = bitcoinTransactionService.findUnconfirmedBtcPayments();
          List<SimpleBtcPayment> paymentsToUpdate = new ArrayList<>();
          final List<Output> unspentOutputs;
          try {
            unspentOutputs = btcdClient.listUnspent(0);
            unconfirmedPayments.stream().filter(payment -> StringUtils.isNotEmpty(payment.getHash())).forEach(payment -> {
              Optional<Output> outputResult = unspentOutputs.stream().filter(output -> payment.getHash().equals(output.getTxId()))
                      .findFirst();
              if (outputResult.isPresent()) {
                log.debug("Get the output");
                Output output = outputResult.get();
                log.debug(String.format("Output: %s", output));
                paymentsToUpdate.add(new SimpleBtcPayment(payment.getInvoiceId(), output.getTxId(), output.getAmount(), output.getConfirmations()));
              } else {
                String txId = payment.getHash();
                try {
                  log.debug("Start retrieving tx from blockchain");
                  Transaction transaction = btcdClient.getTransaction(txId);
                  log.debug(String.format("Transaction: %s", transaction));
                  paymentsToUpdate.add(new SimpleBtcPayment(payment.getInvoiceId(), txId, transaction.getAmount(), transaction.getConfirmations()));
                } catch (BitcoindException | CommunicationException e ) {
                  log.error(e);
                }
              }
            });
          } catch (BitcoindException | CommunicationException e ) {
            log.error(e);
          }
          
          paymentsToUpdate.forEach(payment -> {
            log.debug(String.format("Payment to update: %s", payment));
            changeConfirmationsOrProvide(payment);
          });
          
          
        }
      });
      daemon.addWalletListener(new WalletListener() {
        @Override
        public void walletChanged(Transaction transaction) {
          log.debug(String.format("Wallet change: tx id %s", transaction.toString()));
          InvoiceStatus beginStatus = PendingPaymentStatusEnum.getBeginState();
          String address = transaction.getDetails().get(0).getAddress();
          if (bitcoinTransactionService.existsPendingPaymentWithStatusAndAddress(beginStatus, address) && transaction.getConfirmations() == 0) {
            bitcoinTransactionService.markStartConfirmationProcessing(address, transaction.getTxId());
          }
        }
      });
    } catch (BitcoindException | CommunicationException | IOException e) {
      log.error(e);
    }
  
  }
  
  private void changeConfirmationsOrProvide(SimpleBtcPayment simpleBtcPayment) {
    bitcoinTransactionService.changeTransactionConfidenceForPendingPayment(simpleBtcPayment.getInvoiceId(), simpleBtcPayment.getConfirmations());
    if (simpleBtcPayment.getConfirmations() >= BitcoinService.CONFIRMATION_NEEDED_COUNT) {
      try {
        bitcoinTransactionService.provideBtcTransaction(simpleBtcPayment.getInvoiceId(), simpleBtcPayment.getBtcTransactionIdHash(),
                simpleBtcPayment.getAmount(), null);
      } catch (IllegalInvoiceAmountException | IllegalOperationTypeException | IllegalTransactionProvidedStatusException e) {
        log.error(e);
      }
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
  
  
  private BtcdClient initBitcoindClient() throws BitcoindException, CommunicationException, IOException {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    CloseableHttpClient httpProvider = HttpClients.custom().setConnectionManager(cm)
            .build();
    Properties nodeConfig = new Properties();
    nodeConfig.load(getClass().getClassLoader().getResourceAsStream("node_config.properties"));
    log.debug(nodeConfig);
    return new BtcdClientImpl(httpProvider, nodeConfig);
  }
  
  
}
