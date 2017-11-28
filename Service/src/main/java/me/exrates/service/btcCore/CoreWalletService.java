package me.exrates.service.btcCore;

import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.BtcWalletInfoDto;
import me.exrates.model.dto.TxReceivedByAddressFlatDto;
import me.exrates.model.dto.merchants.btc.BtcBlockDto;
import me.exrates.model.dto.merchants.btc.BtcPaymentFlatDto;
import me.exrates.model.dto.merchants.btc.BtcTransactionDto;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by OLEG on 14.03.2017.
 */
public interface CoreWalletService {
  void initCoreClient(String nodePropertySource);
  
  void initBtcdDaemon(boolean zmqEnabled);
  
  String getNewAddress(String walletPassword);
  
  @Scheduled(initialDelay = 5 * 60000, fixedDelay = 12 * 60 * 60000)
  void backupWallet(String backupFolder);
  
  Optional<BtcTransactionDto> handleTransactionConflicts(String txId);
  
  BtcTransactionDto getTransaction(String txId);
  
  BtcWalletInfoDto getWalletInfo();
  
  List<TxReceivedByAddressFlatDto> listReceivedByAddress(Integer minConfirmations);
  
  List<BtcTransactionHistoryDto> listAllTransactions();
  
  List<BtcPaymentFlatDto> listSinceBlock(String blockHash, Integer merchantId, Integer currencyId);
  
  BigDecimal estimateFee(int blockCount);
  
  BigDecimal getActualFee();
  
  void setTxFee(BigDecimal fee);
  
  void submitWalletPassword(String password);
  
  String sendToAddressAuto(String address, BigDecimal amount, String walletPassword);
  
  String sendToMany(Map<String, BigDecimal> payments);

    Flux<BtcBlockDto> blockFlux();

    Flux<BtcTransactionDto> walletFlux();

  Flux<BtcTransactionDto> instantSendFlux();
}
