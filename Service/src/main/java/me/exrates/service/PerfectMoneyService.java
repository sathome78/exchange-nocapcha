package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.service.merchantStrategy.IMerchantService;

import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface PerfectMoneyService extends IMerchantService {

    Map<String,String> getPerfectMoneyParams(Transaction transaction);

    void provideOutputPayment(Payment payment, CreditsOperation creditsOperation);

    Transaction preparePaymentTransactionRequest(CreditsOperation creditsOperation);

    boolean provideTransaction(int transaction);

    void invalidateTransaction(Transaction transaction);

    String computePaymentHash(Map<String, String> perfectMoneyParams);

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