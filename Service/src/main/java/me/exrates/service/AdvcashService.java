package me.exrates.service;

import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;
import org.springframework.stereotype.Service;

@Service
public interface AdvcashService extends IRefillable, IWithdrawable {

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
  default Boolean additionalTagForWithdrawAddressIsUsed() {
    return false;
  }

  @Override
  default Boolean withdrawTransferringConfirmNeeded() {
    return null;
  }
}
