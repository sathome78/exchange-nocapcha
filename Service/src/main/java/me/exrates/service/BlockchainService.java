package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.PendingPayment;
import me.exrates.service.merchantStrategy.IMerchantService;

import java.util.Map;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface BlockchainService  {

    int CONFIRMATIONS = 4;
}
