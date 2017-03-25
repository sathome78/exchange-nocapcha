package me.exrates.service.impl.bitcoinWallet;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import com.neemre.btcdcli4j.core.domain.*;
import com.neemre.btcdcli4j.core.domain.enums.PaymentCategories;
import com.neemre.btcdcli4j.daemon.BtcdDaemon;
import com.neemre.btcdcli4j.daemon.BtcdDaemonImpl;
import com.neemre.btcdcli4j.daemon.event.BlockListener;
import com.neemre.btcdcli4j.daemon.event.WalletListener;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.PendingPayment;
import me.exrates.model.dto.BtcWalletInfoDto;
import me.exrates.model.dto.TxReceivedByAddressFlatDto;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.BtcTransactionShort;
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

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 14.03.2017.
 */
@Log4j2(topic = "bitcoin_core")
public class BitcoinCoreWalletServiceImpl implements BitcoinWalletService {
    
 
  @Autowired
  private BitcoinTransactionService bitcoinTransactionService;
  
  private BtcdClient btcdClient;
  
  private BtcdDaemon daemon;
  
  
  @Override
  public void initBitcoin() {
  
    try {
      log.debug("Starting Bitcoin Core client");
      initBitcoindClient();
      initBtcdDaemon();
      checkUnpaidBtcPayments();
      
    } catch (BitcoindException | CommunicationException | IOException e) {
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
  
  
  private void initBitcoindClient() throws BitcoindException, CommunicationException, IOException {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    CloseableHttpClient httpProvider = HttpClients.custom().setConnectionManager(cm)
            .build();
    Properties nodeConfig = new Properties();
    nodeConfig.load(getClass().getClassLoader().getResourceAsStream("node_config.properties"));
    log.debug("Node config: " + nodeConfig);
    btcdClient = new BtcdClientImpl(httpProvider, nodeConfig);
  }
  
  private void initBtcdDaemon() throws BitcoindException, CommunicationException {
    daemon = new BtcdDaemonImpl(btcdClient);
    daemon.addBlockListener(new BlockListener() {
      @Override
      public void blockDetected(Block block) {
        log.debug(String.format("Block detected: hash %s ", block.toString()));
        processBlock();
      }
    });
    daemon.addWalletListener(new WalletListener() {
      @Override
      public void walletChanged(Transaction transaction) {
        log.debug(String.format("Wallet change: tx id %s", transaction.toString()));
        processIncomingPayment(transaction);
      }
    });
  }
  
  private void processIncomingPayment(Transaction transaction) {
    InvoiceStatus beginStatus = PendingPaymentStatusEnum.getBeginState();
    transaction.getDetails().stream().filter(payment -> payment.getCategory() == PaymentCategories.RECEIVE)
            .map(PaymentOverview::getAddress).forEach(address -> {
      if (bitcoinTransactionService.existsPendingPaymentWithStatusAndAddress(beginStatus, address) && transaction.getConfirmations() == 0) {
        bitcoinTransactionService.markStartConfirmationProcessing(address, transaction.getTxId());
      }
    });
  }
  
  private void processBlock() {
    List<PendingPayment> unconfirmedPayments = bitcoinTransactionService.findUnconfirmedBtcPayments();
    unconfirmedPayments.forEach(log::debug);
    List<BtcTransactionShort> paymentsToUpdate = new ArrayList<>();
    try {
      final Map<String, Address> receivedByAddress = listReceivedByAddressMapped(1);
      receivedByAddress.forEach(log::debug);
      unconfirmedPayments.stream().filter(payment -> StringUtils.isNotEmpty(payment.getHash())).forEach(payment -> {
        Address address = receivedByAddress.get(payment.getAddress());
        log.debug("address " + address);
        if (address != null && address.getTxIds().contains(payment.getHash()) && address.getTxIds().size() == 1) {
          log.debug("Getting info from address!");
          paymentsToUpdate.add(new BtcTransactionShort(payment.getInvoiceId(), address.getTxIds().get(0),
                  address.getAmount(), address.getConfirmations()));
          
        } else {
          log.debug("Retrieving tx from blockchain!");
          try {
            Transaction tx = btcdClient.getTransaction(payment.getHash());
            if (tx.getDetails().size() == 1) {
              paymentsToUpdate.add(new BtcTransactionShort(payment.getInvoiceId(), tx.getTxId(), tx.getAmount(), tx.getConfirmations()));
            } else {
              tx.getDetails().stream().filter(paymentOverview -> payment.getAddress().equals(paymentOverview.getAddress()))
                      .findFirst().ifPresent(paymentOverview ->
                      paymentsToUpdate.add(new BtcTransactionShort(payment.getInvoiceId(), tx.getTxId(), paymentOverview.getAmount(), tx.getConfirmations())));
            }
          } catch (BitcoindException | CommunicationException e) {
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
  
  private void changeConfirmationsOrProvide(BtcTransactionShort simpleBtcPayment) {
    changeConfirmationsOrProvide(simpleBtcPayment.getInvoiceId(), simpleBtcPayment.getBtcTransactionIdHash(),
            simpleBtcPayment.getAmount(), simpleBtcPayment.getConfirmations());
  }
  
  
  
  private void changeConfirmationsOrProvide(Integer invoiceId, String txId, BigDecimal amount, Integer confirmations) {
    bitcoinTransactionService.changeTransactionConfidenceForPendingPayment(invoiceId, confirmations);
    if (confirmations >= BitcoinService.CONFIRMATION_NEEDED_COUNT) {
      try {
        bitcoinTransactionService.provideBtcTransaction(invoiceId, txId, amount, null);
      } catch (IllegalInvoiceAmountException | IllegalOperationTypeException | IllegalTransactionProvidedStatusException e) {
        log.error(e);
      }
    }
  }
  
  
  
  //Required to check if there were any incoming payments while the application was not running
  private void checkUnpaidBtcPayments() {
    log.debug("Checking unpaid pending payments");
    try {
      List<PendingPayment> unpaidPayments = bitcoinTransactionService.findUnpaidBtcPayments();
      Map<String, Address> received = listReceivedByAddressMapped(0);
      
      unpaidPayments.stream().filter(payment -> payment.getAddress() != null).forEach(payment -> {
        Optional.ofNullable(received.get(payment.getAddress())).ifPresent(address -> {
          if (address.getTxIds().size() == 1) {
            processUnpaidPayment(payment.getInvoiceId(), payment.getAddress(), address.getTxIds().get(0), address.getAmount(), address.getConfirmations());
          } else {
            address.getTxIds().stream().map(this::getTransactionByTxId).filter(Objects::nonNull).findAny().ifPresent(tx -> {
              if (tx.getDetails().size() == 1) {
                processUnpaidPayment(payment.getInvoiceId(), payment.getAddress(), tx.getTxId(), tx.getAmount(), tx.getConfirmations());
              } else {
                tx.getDetails().forEach(paymentOverview -> processUnpaidPayment(payment.getInvoiceId(), payment.getAddress(), tx.getTxId(),
                        paymentOverview.getAmount(), tx.getConfirmations()));
              }
            });
          }
        });
      });
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
    }
  }
  
  private Transaction getTransactionByTxId(String txId) {
    try {
      return btcdClient.getTransaction(txId);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      return null;
    }
  }
  
  private Map<String, Address> listReceivedByAddressMapped(int minConfirmations) throws BitcoindException, CommunicationException {
    return btcdClient.listReceivedByAddress(minConfirmations).stream().filter(address -> !address.getTxIds().isEmpty())
            .collect(Collectors.toMap(Address::getAddress, address -> address));
  }
  
  private void processUnpaidPayment(Integer paymentId, String address, String txId, BigDecimal amount, Integer confirmations) {
    bitcoinTransactionService.markStartConfirmationProcessing(address, txId);
    if (confirmations > 0) {
      changeConfirmationsOrProvide(paymentId, txId, amount, confirmations);
    }
  }
  
  @Override
  public BtcWalletInfoDto getWalletInfo() {
    try {
      BtcWalletInfoDto dto = new BtcWalletInfoDto();
      WalletInfo walletInfo = btcdClient.getWalletInfo();
      BigDecimal unconfirmedBalance = btcdClient.getUnconfirmedBalance();
      dto.setBalance(BigDecimalProcessing.formatNonePoint(walletInfo.getBalance(), true));
      dto.setUnconfirmedBalance(BigDecimalProcessing.formatNonePoint(unconfirmedBalance, true));
      dto.setTransactionCount(walletInfo.getTxCount());
      return dto;
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  @Override
  public List<TxReceivedByAddressFlatDto> listReceivedByAddress(Integer minConfirmations) {
    try {
      List<Address> received = btcdClient.listReceivedByAddress(minConfirmations);
      return received.stream().flatMap(address -> address.getTxIds().stream().map(txId -> {
        TxReceivedByAddressFlatDto dto = new TxReceivedByAddressFlatDto();
        dto.setAccount(address.getAccount());
        dto.setAddress(address.getAddress());
        dto.setAmount(address.getAmount());
        dto.setConfirmations(address.getConfirmations());
        dto.setTxId(txId);
        return dto;
      })).collect(Collectors.toList());
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  @Override
  public List<BtcTransactionHistoryDto> listAllTransactions() {
    try {
      return btcdClient.listSinceBlock().getPayments().stream()
              .map(payment -> {
        BtcTransactionHistoryDto dto = new BtcTransactionHistoryDto();
        dto.setTxId(payment.getTxId());
        dto.setAddress(payment.getAddress());
        dto.setCategory(payment.getCategory().getName());
        dto.setAmount(BigDecimalProcessing.formatNonePoint(payment.getAmount(), true));
        dto.setFee(BigDecimalProcessing.formatNonePoint(payment.getFee(), true));
        dto.setConfirmations(payment.getConfirmations());
        dto.setTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(payment.getTime()), ZoneId.systemDefault()));
        return dto;
      }).collect(Collectors.toList());
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  @Override
  public BigDecimal estimateFee(int blockCount) {
    try {
      return btcdClient.estimateFee(blockCount);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  @Override
  public void submitWalletPassword(String password) {
    try {
      btcdClient.walletPassphrase(password, 60);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException("Incorrect password!");
    }
  }
  
  @Override
  public String sendToAddress(String address, BigDecimal amount) {
    try {
      String result = btcdClient.sendToAddress(address, amount);
      btcdClient.walletLock();
      return result;
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  
  
}
