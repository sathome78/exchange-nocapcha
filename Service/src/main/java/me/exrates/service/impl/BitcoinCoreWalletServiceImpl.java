package me.exrates.service.impl;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
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
import me.exrates.service.BitcoinService;
import me.exrates.service.BitcoinTransactionService;
import me.exrates.service.BitcoinWalletService;
import me.exrates.service.exception.BitcoinCoreException;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.IllegalTransactionProvidedStatusException;
import me.exrates.service.exception.invoice.IllegalInvoiceAmountException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by OLEG on 14.03.2017.
 */
@Component("BitcoinCoreService")
@Log4j2
public class BitcoinCoreWalletServiceImpl implements BitcoinWalletService {
    
  @Autowired
  private BtcdClient btcdClient;
  
  @Autowired
  private BitcoinTransactionService bitcoinTransactionService;
  
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
          List<PendingPayment> unconfirmedPayments = bitcoinTransactionService.findUnconfirmedBtcPayments();
          unconfirmedPayments.forEach(log::debug);
          final List<Output> unspentOutputs;
          try {
            unspentOutputs = btcdClient.listUnspent(0);
            unconfirmedPayments.stream().filter(payment -> StringUtils.isNotEmpty(payment.getHash())).forEach(payment -> {
              log.debug(payment);
              Optional<Output> outputResult = unspentOutputs.stream().filter(output -> payment.getHash().equals(output.getTxId()))
                      .findFirst();
              log.debug(outputResult);
              if (outputResult.isPresent()) {
                log.debug("Get the output");
                Output output = outputResult.get();
                log.debug(String.format("Output: %s", output));
                changeConfirmationsOrProvide(payment.getInvoiceId(), output.getTxId(), output.getAmount(), output.getConfirmations());
              } else {
                String txId = payment.getHash();
                try {
                  log.debug("Start retrieving tx from blockchain");
                  Transaction transaction = btcdClient.getTransaction(txId);
                  log.debug(String.format("Transaction: %s", transaction));
                  changeConfirmationsOrProvide(payment.getInvoiceId(), txId, transaction.getAmount(), transaction.getConfirmations());
                } catch (BitcoindException | CommunicationException e ) {
                  log.error(e);
                }
              }
            });
          } catch (BitcoindException | CommunicationException e ) {
            log.error(e);
          }
          
          
  
  
        }
      });
      daemon.addWalletListener(new WalletListener() {
        @Override
        public void walletChanged(Transaction transaction) {
          log.debug(String.format("Wallet change: tx id %s", transaction.getTxId()));
          log.debug(String.format("TX %s ", transaction.toString()));
          InvoiceStatus beginStatus = PendingPaymentStatusEnum.getBeginState();
          //TODO check why List of PaymentOverview
          String address = transaction.getDetails().get(0).getAddress();
          if (bitcoinTransactionService.existsPendingPaymentWithStatusAndAddress(beginStatus, address) && transaction.getConfirmations() == 0) {
            /*PendingPaymentStatusDto pendingPayment = */bitcoinTransactionService.markStartConfirmationProcessing(address, transaction.getTxId());
          }
        }
      });
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
    }
  
  }
  
  private void changeConfirmationsOrProvide(Integer invoiceId, String txId, BigDecimal amount, Integer confirmations) {
    if (confirmations < BitcoinService.CONFIRMATION_NEEDED_COUNT) {
      bitcoinTransactionService.changeTransactionConfidenceForPendingPayment(invoiceId, confirmations);
    } else {
      try {
        bitcoinTransactionService.provideBtcTransaction(invoiceId, txId, amount, null);
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
  
  
  
}
