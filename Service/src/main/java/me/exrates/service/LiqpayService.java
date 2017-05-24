package me.exrates.service;


import me.exrates.service.merchantStrategy.IMerchantService;

public interface LiqpayService extends IMerchantService {

  @Override
  default Boolean createdRefillRequestRecordNeeded() {
    return null;
  }

  @Override
  default Boolean needToCreateRefillRequestRecord() {
    return null;
  }

  @Override
  default Boolean toMainAccountTransferringConfirmNeeded() {
    return null;
  }

  @Override
  default Boolean generatingAdditionalRefillAddressAvailable() {
    return null;
  }

  @Override
  default Boolean withdrawTransferringConfirmNeeded() {
    return null;
  }
}
