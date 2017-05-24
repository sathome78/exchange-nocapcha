package me.exrates.service;

import me.exrates.service.merchantStrategy.IMerchantService;
import org.springframework.stereotype.Service;

@Service
public interface AdvcashService extends IMerchantService {

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
