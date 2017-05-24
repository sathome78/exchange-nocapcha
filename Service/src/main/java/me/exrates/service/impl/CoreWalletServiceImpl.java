package me.exrates.service.impl;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import com.neemre.btcdcli4j.core.domain.Address;
import com.neemre.btcdcli4j.core.domain.Block;
import com.neemre.btcdcli4j.core.domain.Transaction;
import com.neemre.btcdcli4j.core.domain.WalletInfo;
import com.neemre.btcdcli4j.daemon.BtcdDaemon;
import com.neemre.btcdcli4j.daemon.BtcdDaemonImpl;
import com.neemre.btcdcli4j.daemon.event.BlockListener;
import com.neemre.btcdcli4j.daemon.event.InstantSendListener;
import com.neemre.btcdcli4j.daemon.event.WalletListener;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.BtcWalletInfoDto;
import me.exrates.model.dto.TxReceivedByAddressFlatDto;
import me.exrates.model.dto.btcTransactionFacade.BtcTransactionDto;
import me.exrates.model.dto.btcTransactionFacade.BtcTxPaymentDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.CoreWalletService;
import me.exrates.service.exception.BitcoinCoreException;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.exception.invoice.MerchantException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 14.03.2017.
 */
@Component
@Scope("prototype")
@Log4j2(topic = "bitcoin_core")
public class CoreWalletServiceImpl implements CoreWalletService {
  
  private static final int KEY_POOL_LOW_THRESHOLD = 10;
  private static final int MIN_CONFIRMATIONS_FOR_SPENDING = 3;
  
  private BtcdClient btcdClient;
  private BtcdDaemon daemon;
  
  
  @Override
  public void initCoreClient(String nodePropertySource) {
    
    try {
      PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
      CloseableHttpClient httpProvider = HttpClients.custom().setConnectionManager(cm)
              .build();
      Properties nodeConfig = new Properties();
      nodeConfig.load(getClass().getClassLoader().getResourceAsStream(nodePropertySource));
      log.debug("Node config: " + nodeConfig);
      btcdClient = new BtcdClientImpl(httpProvider, nodeConfig);
    } catch (Exception e) {
      log.error(e);
    }
    
  }
  
  @Override
  public void initBtcdDaemon(Consumer<String> blockHandler, Consumer<BtcTransactionDto> walletHandler, Consumer<BtcTransactionDto> instantSendHandler)  {
    try {
      daemon = new BtcdDaemonImpl(btcdClient);
      daemon.addBlockListener(new BlockListener() {
        @Override
        public void blockDetected(Block block) {
          log.debug(String.format("Block detected: hash %s, height %s ", block.getHash(), block.getHeight()));
          blockHandler.accept(block.getHash());
        }
      });
      daemon.addWalletListener(new WalletListener() {
        @Override
        public void walletChanged(Transaction transaction) {
          log.debug(String.format("Wallet change: tx %s", transaction.toString()));
          walletHandler.accept(convert(transaction));
        }
      });
      daemon.addInstantSendListener(new InstantSendListener() {
        @Override
        public void transactionBlocked(Transaction transaction) {
          log.debug(String.format("Transaction blocked: tx %s", transaction.toString()));
          instantSendHandler.accept(convert(transaction));
        }
      });
    } catch (Exception e) {
      log.error(e);
    }
    
  }
  
  
  @Override
  public String getNewAddress(String walletPassword) {
    try {
      WalletInfo walletInfo = btcdClient.getWalletInfo();
      Integer keyPoolSize = walletInfo.getKeypoolSize();
      
      /*
      * If wallet is encrypted and locked, pool of private keys is not refilled
      * Keys are automatically refilled on unlocking
      * */
      if (keyPoolSize < KEY_POOL_LOW_THRESHOLD) {
        unlockWallet(walletPassword, 1);
      }
      return btcdClient.getNewAddress();
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException("Cannot generate new address!");
    }
  }
  
  @Override
  public void backupWallet(String backupFolder) {
    try {
      String filename = new StringJoiner("").add(backupFolder).add("backup_")
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
  
  @Override
  public Optional<BtcTransactionDto> handleTransactionConflicts(String txId) {
    try {
      return handleConflicts(btcdClient.getTransaction(txId)).map(this::convert);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
    }
    return Optional.empty();
  }
  
  private BtcTransactionDto convert(Transaction tx) {
    List<BtcTxPaymentDto> payments = tx.getDetails().stream()
            .map((payment) -> new BtcTxPaymentDto(payment.getAddress(), payment.getCategory().getName(), payment.getAmount(), payment.getFee()))
            .collect(Collectors.toList());
    return new BtcTransactionDto(tx.getAmount(), tx.getFee(), tx.getConfirmations(), tx.getTxId(), tx.getWalletConflicts(), tx.getTime(), tx.getTimeReceived(),
            tx.getComment(), tx.getTo(), payments);
  }
  
 
  
  //Required to check if there were any incoming payments while the application was not running
  private void checkUnpaidBtcPayments() {
    /*log.debug("Checking unpaid pending payments");
   /* try {
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
    }*/
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
   /* try {
      bitcoinTransactionService.markStartConfirmationProcessing(address, txId, amount);
      if (confirmations > 0) {
       // changeConfirmationsOrProvide(paymentId, txId, amount, confirmations);
      }
    } catch (IllegalInvoiceAmountException e) {
      log.error(ExceptionUtils.getStackTrace(e));
    }*/
  }
  
  @Override
  public BtcWalletInfoDto getWalletInfo() {
    try {
      BtcWalletInfoDto dto = new BtcWalletInfoDto();
      WalletInfo walletInfo = btcdClient.getWalletInfo();
      BigDecimal spendableBalance = btcdClient.getBalance("", MIN_CONFIRMATIONS_FOR_SPENDING);
      BigDecimal confirmedNonSpendableBalance = BigDecimalProcessing.doAction(walletInfo.getBalance(), spendableBalance, ActionType.SUBTRACT);
      BigDecimal unconfirmedBalance = btcdClient.getUnconfirmedBalance();
      
      dto.setBalance(BigDecimalProcessing.formatNonePoint(spendableBalance, true));
      dto.setConfirmedNonSpendableBalance(BigDecimalProcessing.formatNonePoint(confirmedNonSpendableBalance, true));
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
      unlockWallet(password, 60);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
  
  /*
  * Using sendMany instead of sendToAddress allows to send only UTXO with certain number of confirmations.
  * DO NOT use immutable map creation methods like Collections.singletonMap(...), it will cause an error within lib code
  * */
  @Override
  public String sendToAddressAuto(String address, BigDecimal amount, String walletPassword) {
    
    try {
      unlockWallet(walletPassword, 1);
      Map<String, BigDecimal> payments = new HashMap<>();
      payments.put(address, amount);
      return btcdClient.sendMany("", payments, MIN_CONFIRMATIONS_FOR_SPENDING);
    } catch (BitcoindException e) {
      log.error(e);
      if (e.getCode() == -5) {
        throw new InvalidAccountException();
      }
      if (e.getCode() == -6) {
        throw new InsufficientCostsInWalletException();
      }
      throw new MerchantException(e.getMessage());
    }
    catch (CommunicationException e) {
      log.error(e);
      throw new MerchantException(e.getMessage());
    }
  }
  
  private void unlockWallet(String password, int authTimeout) throws BitcoindException, CommunicationException {
    Long unlockedUntil = btcdClient.getWalletInfo().getUnlockedUntil();
    if (unlockedUntil != null && unlockedUntil == 0) {
      btcdClient.walletPassphrase(password, authTimeout);
    }
  }
  
  @Override
  public String sendToMany(Map<String, BigDecimal> payments) {
    try {
      return btcdClient.sendMany("", payments, MIN_CONFIRMATIONS_FOR_SPENDING);
    } catch (BitcoindException | CommunicationException e) {
      log.error(e);
      throw new BitcoinCoreException(e.getMessage());
    }
  }
  
 
}
