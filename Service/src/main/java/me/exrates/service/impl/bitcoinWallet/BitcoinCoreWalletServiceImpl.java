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
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 14.03.2017.
 */
@Log4j2(topic = "bitcoin_core")
@PropertySource("classpath:/merchants/btc_wallet.properties")
public class BitcoinCoreWalletServiceImpl implements BitcoinWalletService {
  
  private static final int KEY_POOL_LOW_THRESHOLD = 10;
  
  @Value("${btc.wallet.password}")
  private String walletPassword;
  
  @Value("${btc.backup.folder}")
  private String btcBackupFolder;
    
 
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
      WalletInfo walletInfo = btcdClient.getWalletInfo();
      Integer keyPoolSize = walletInfo.getKeypoolSize();
      
      /*
      * If wallet is encrypted and locked, pool of private keys is not refilled
      * Keys are automatically refilled on unlocking
      * */
      if (keyPoolSize < KEY_POOL_LOW_THRESHOLD) {
        btcdClient.walletPassphrase(walletPassword, 1);
      }
      return btcdClient.getNewAddress();
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException("Cannot generate new address!");
    }
  }
  
  @Override
  @Scheduled(initialDelay = 5 * 60000, fixedDelay = 12 * 60 * 60000)
  public void backupWallet() {
    try {
      String filename = new StringJoiner("").add(btcBackupFolder).add("backup_")
              .add((LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))))
              .add(".dat").toString();
      log.debug("Backing up wallet to file: " + filename);
      btcdClient.backupWallet(filename);
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
        log.debug(String.format("Block detected: hash %s, height %s ", block.getHash(), block.getHeight()));
        processBlock();
      }
    });
    daemon.addWalletListener(new WalletListener() {
      @Override
      public void walletChanged(Transaction transaction) {
        log.debug(String.format("Wallet change: tx %s", transaction.toString()));
        processIncomingPayment(transaction);
      }
    });
  }
  
  private void processIncomingPayment(Transaction transaction) {
    Optional<Transaction> targetTxResult = handleConflicts(transaction);
    if (targetTxResult.isPresent()) {
      Transaction targetTx = targetTxResult.get();
      InvoiceStatus beginStatus = PendingPaymentStatusEnum.getBeginState();
      targetTx.getDetails().stream().filter(payment -> payment.getCategory() == PaymentCategories.RECEIVE)
              .forEach(payment -> {
        String address = payment.getAddress();
        if (bitcoinTransactionService.existsPendingPaymentWithStatusAndAddress(beginStatus, address) && targetTx.getConfirmations() == 0) {
          try {
            bitcoinTransactionService.markStartConfirmationProcessing(address, targetTx.getTxId(), payment.getAmount());
          } catch (IllegalInvoiceAmountException e) {
            log.error(ExceptionUtils.getStackTrace(e));
          }
        }
      });
    } else {
      log.error("Invalid transaction");
    }
  }
  
  private Optional<Transaction> handleConflicts(Transaction transaction) {
    if (transaction.getConfirmations() < 0 && !transaction.getWalletConflicts().isEmpty()) {
      log.warn("Wallet conflicts present");
      for (String txId : transaction.getWalletConflicts()) {
        try {
          Transaction conflicted = btcdClient.getTransaction(txId);
          if (conflicted.getConfirmations() >= 0) {
            return Optional.of(conflicted);
          }
        } catch (BitcoindException | CommunicationException e) {
          log.error(e);
        }
      }
      return Optional.empty();
    } else {
      return Optional.of(transaction);
    }
  }
  
  private void processBlock() {
    List<PendingPayment> unconfirmedPayments = bitcoinTransactionService.findUnconfirmedBtcPayments();
    unconfirmedPayments.forEach(log::debug);
    List<BtcTransactionShort> paymentsToUpdate = new ArrayList<>();
    log.debug("Start retrieving TXs");
    unconfirmedPayments.stream().filter(payment -> StringUtils.isNotEmpty(payment.getHash())).forEach(payment -> {
      log.debug("Retrieving tx from blockchain!");
      try {
        Optional<Transaction> txResult = handleConflicts(btcdClient.getTransaction(payment.getHash()));
        if (txResult.isPresent()) {
          Transaction tx = txResult.get();
          if (!payment.getHash().equals(tx.getTxId())) {
            bitcoinTransactionService.updatePendingPaymentHash(payment.getInvoiceId(), tx.getTxId());
          }
          if (tx.getDetails().size() == 1) {
            paymentsToUpdate.add(new BtcTransactionShort(payment.getInvoiceId(), tx.getTxId(), tx.getAmount(), tx.getConfirmations()));
          } else {
            tx.getDetails().stream().filter(paymentOverview -> payment.getAddress().equals(paymentOverview.getAddress()))
                    .findFirst().ifPresent(paymentOverview ->
                    paymentsToUpdate.add(new BtcTransactionShort(payment.getInvoiceId(), tx.getTxId(), paymentOverview.getAmount(), tx.getConfirmations())));
          }
        } else {
          log.error("No valid transactions available!");
        }
      } catch (BitcoindException | CommunicationException e) {
        log.error(e);
      }
    });
    
    log.debug("Start updating payments in DB");
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
    try {
      bitcoinTransactionService.markStartConfirmationProcessing(address, txId, amount);
      if (confirmations > 0) {
        changeConfirmationsOrProvide(paymentId, txId, amount, confirmations);
      }
    } catch (IllegalInvoiceAmountException e) {
      log.error(ExceptionUtils.getStackTrace(e));
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
        dto.setTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(payment.getTime() * 1000L), ZoneId.systemDefault()));
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
  public BigDecimal getActualFee() {
    try {
      return btcdClient.getInfo().getPayTxFee();
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  @Override
  public void setTxFee(BigDecimal fee) {
    try {
      btcdClient.setTxFee(fee);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  @Override
  public void submitWalletPassword(String password) {
    try {
      Long unlockedUntil = btcdClient.getWalletInfo().getUnlockedUntil();
      if (unlockedUntil != null && unlockedUntil == 0) {
        btcdClient.walletPassphrase(password, 60);
      }
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
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
  
  @Override
  public String sendToAddressAuto(String address, BigDecimal amount) {
    
    try {
      Long unlockedUntil = btcdClient.getWalletInfo().getUnlockedUntil();
      if (unlockedUntil != null && unlockedUntil == 0) {
        btcdClient.walletPassphrase(walletPassword, 1);
      }
      return btcdClient.sendToAddress(address, amount);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  @Override
  public String sendToMany(Map<String, BigDecimal> payments) {
    try {
      return btcdClient.sendMany("", payments);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  
  
}
