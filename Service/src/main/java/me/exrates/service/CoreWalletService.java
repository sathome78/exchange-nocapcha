package me.exrates.service;

import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.BtcWalletInfoDto;
import me.exrates.model.dto.TxReceivedByAddressFlatDto;
import me.exrates.model.dto.btcTransactionFacade.BtcTransactionDto;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by OLEG on 14.03.2017.
 */
public interface CoreWalletService {
  void initCoreClient(String nodePropertySource);
  
  void initBtcdDaemon(Consumer<String> blockHandler, Consumer<BtcTransactionDto> walletHandler, Consumer<BtcTransactionDto> instantSendHandler);
  
  String getNewAddress(String walletPassword);
  
  @Scheduled(initialDelay = 5 * 60000, fixedDelay = 12 * 60 * 60000)
  void backupWallet(String backupFolder);
  
  Optional<BtcTransactionDto> handleTransactionConflicts(String txId);
  
  BtcTransactionDto getTransaction(String txId);
  
  BtcWalletInfoDto getWalletInfo();
  
  List<TxReceivedByAddressFlatDto> listReceivedByAddress(Integer minConfirmations);
  
  List<BtcTransactionHistoryDto> listAllTransactions();
  
  BigDecimal estimateFee(int blockCount);
  
  BigDecimal getActualFee();
  
  void setTxFee(BigDecimal fee);
  
  void submitWalletPassword(String password);
  
  String sendToAddressAuto(String address, BigDecimal amount, String walletPassword);
  
  String sendToMany(Map<String, BigDecimal> payments);
}
