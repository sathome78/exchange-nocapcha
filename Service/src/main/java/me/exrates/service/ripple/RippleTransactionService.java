package me.exrates.service.ripple;

import me.exrates.model.dto.WithdrawMerchantOperationDto;

/**
 * Created by maks on 11.05.2017.
 */
public interface RippleTransactionService {
    void withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto);

}
