package me.exrates.service;

import me.exrates.model.UserTransfer;

import java.math.BigDecimal;

/**
 * Created by maks on 15.03.2017.
 */
public interface UserTransferService {

    UserTransfer createUserTransfer(int fromUserId,
                                    int toUserId,
                                    int currencyId,
                                    BigDecimal amount,
                                    BigDecimal commissionAmount);
}
