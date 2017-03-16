package me.exrates.service;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;

/**
 * Created by OLEG on 14.03.2017.
 */
public interface BitcoinWalletService {
  void initBitcoin();
  
  String getNewAddress();
}
