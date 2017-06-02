package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

public interface OkPayService extends IRefillable, IWithdrawable {

    RedirectView preparePayment(CreditsOperation creditsOperation, String email);

    public boolean confirmPayment(Map<String,String> params);

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
