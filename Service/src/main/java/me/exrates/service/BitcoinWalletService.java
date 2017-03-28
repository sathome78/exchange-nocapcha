package me.exrates.service;

import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.BtcWalletInfoDto;
import me.exrates.model.dto.TxReceivedByAddressFlatDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by OLEG on 14.03.2017.
 */
public interface BitcoinWalletService {
  void initBitcoin();
  
  String getNewAddress();
  
  BtcWalletInfoDto getWalletInfo();
  
  List<TxReceivedByAddressFlatDto> listReceivedByAddress(Integer minConfirmations);
  
  List<BtcTransactionHistoryDto> listAllTransactions();
  
  BigDecimal estimateFee(int blockCount);
  
  void submitWalletPassword(String password);
  
  String sendToAddress(String address, BigDecimal amount);
  
  String sendToMany(Map<String, BigDecimal> payments);
}
