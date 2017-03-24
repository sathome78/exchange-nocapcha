package me.exrates.service;

import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.BtcWalletInfoDto;
import me.exrates.model.dto.TxReceivedByAddressFlatDto;

import java.util.List;

/**
 * Created by OLEG on 14.03.2017.
 */
public interface BitcoinWalletService {
  void initBitcoin();
  
  String getNewAddress();
  
  BtcWalletInfoDto getWalletInfo();
  
  List<TxReceivedByAddressFlatDto> listReceivedByAddress(Integer minConfirmations);
  
  List<BtcTransactionHistoryDto> listAllTransactions();
}
