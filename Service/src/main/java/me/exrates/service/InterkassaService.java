package me.exrates.service;

import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.util.Map;

public interface InterkassaService extends IRefillable, IWithdrawable {
public interface InterkassaService extends IMerchantService {

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
