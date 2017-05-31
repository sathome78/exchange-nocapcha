package me.exrates.service;

import me.exrates.model.Transaction;
import me.exrates.service.merchantStrategy.IMerchantService;

import java.util.Map;

public interface NixMoneyService extends IMerchantService {

    boolean confirmPayment(Map<String,String> params);

    void invalidateTransaction(Transaction transaction);

  @Override
  default Boolean createdRefillRequestRecordNeeded() {
    return true;
  }

  @Override
  default Boolean needToCreateRefillRequestRecord() {
    return true;
  }

  @Override
  default Boolean toMainAccountTransferringConfirmNeeded() {
    return false;
  }

  @Override
  default Boolean generatingAdditionalRefillAddressAvailable() {
    return null;
  }

  @Override
  default Boolean additionalTagForWithdrawAddressIsUsed() {
    return false;
  }

  @Override
  default Boolean withdrawTransferringConfirmNeeded() {
    return false;
  }
}
