package me.exrates.service.merchantStrategy;

import me.exrates.model.dto.WithdrawMerchantOperationDto;

import java.io.IOException;

/**
 * Created by ValkSam on 24.03.2017.
 */
public interface IMerchantService {
  void withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception;
}
