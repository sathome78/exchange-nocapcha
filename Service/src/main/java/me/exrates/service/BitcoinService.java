package me.exrates.service;

import me.exrates.service.merchantStrategy.IMerchantService;

public interface BitcoinService extends IMerchantService {

  int CONFIRMATION_NEEDED_COUNT = 4;

}
