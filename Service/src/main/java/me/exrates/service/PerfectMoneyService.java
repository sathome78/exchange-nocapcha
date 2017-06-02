package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface PerfectMoneyService extends IRefillable, IWithdrawable {

    Map<String,String> getPerfectMoneyParams(Transaction transaction);

    void provideOutputPayment(Payment payment, CreditsOperation creditsOperation);

    Transaction preparePaymentTransactionRequest(CreditsOperation creditsOperation);

    boolean provideTransaction(int transaction);

    void invalidateTransaction(Transaction transaction);

    String computePaymentHash(Map<String, String> perfectMoneyParams);

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