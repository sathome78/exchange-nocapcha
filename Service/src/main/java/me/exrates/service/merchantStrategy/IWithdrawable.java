package me.exrates.service.merchantStrategy;

import me.exrates.model.dto.WithdrawMerchantOperationDto;

import java.util.Map;

/**
 * Created by ValkSam on 24.03.2017.
 */
public interface IWithdrawable extends IMerchantService {

  Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception;

  Boolean additionalTagForWithdrawAddressIsUsed();

  Boolean withdrawTransferringConfirmNeeded();

  default String additionalWithdrawFieldName() {
    return "MEMO";
  };

}
