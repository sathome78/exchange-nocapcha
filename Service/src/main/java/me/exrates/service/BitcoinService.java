package me.exrates.service;

import me.exrates.model.dto.BtcTransactionHistoryDto;
import me.exrates.model.dto.BtcWalletInfoDto;
import me.exrates.model.dto.merchants.btc.BtcBlockDto;
import me.exrates.model.dto.merchants.btc.BtcTransactionDto;
import me.exrates.model.dto.merchants.btc.BtcWalletPaymentItemDto;
import me.exrates.service.events.BtcBlockEvent;
import me.exrates.service.events.BtcWalletEvent;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.List;

public interface BitcoinService extends IRefillable, IWithdrawable {

  int CONFIRMATION_NEEDED_COUNT = 4;

    @EventListener(value = BtcWalletEvent.class)
    void onPayment(BtcTransactionDto transactionDto);

    @EventListener(value = BtcBlockEvent.class)
    void onIncomingBlock(BtcBlockDto blockDto);

    @Scheduled(initialDelay = 5 * 60000, fixedDelay = 12 * 60 * 60000)
  void backupWallet();
  
  BtcWalletInfoDto getWalletInfo();
  
  List<BtcTransactionHistoryDto> listAllTransactions();
  
  BigDecimal estimateFee(int blockCount);
  
  BigDecimal getActualFee();
  
  void setTxFee(BigDecimal fee);
  
  void submitWalletPassword(String password);
  
  List<String> sendToMany(List<BtcWalletPaymentItemDto> payments);

  @Override
  default Boolean createdRefillRequestRecordNeeded() {
    return false;
  }

  @Override
  default Boolean needToCreateRefillRequestRecord() {
    return false;
  }

  @Override
  default Boolean toMainAccountTransferringConfirmNeeded() {
    return false;
  }

  @Override
  default Boolean generatingAdditionalRefillAddressAvailable() {
    return true;
  }

  @Override
  default Boolean additionalTagForWithdrawAddressIsUsed() {
    return false;
  }

  @Override
  default Boolean withdrawTransferringConfirmNeeded() {
    return false;
  }

  @Override
  default Boolean additionalFieldForRefillIsUsed() {
    return false;
  }

  String getNewAddressForAdmin();
}
