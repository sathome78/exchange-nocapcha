package me.exrates.service;

import me.exrates.service.merchantStrategy.IMerchantService;

import java.util.Map;

public interface InterkassaService extends IMerchantService {

    /**
     * Confirms payment in DB
     * @param params
     * @return true if checks accepted, false if none
     */
    boolean confirmPayment(Map<String, String> params);

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
