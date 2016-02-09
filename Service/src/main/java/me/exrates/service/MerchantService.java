package me.exrates.service;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;

import java.util.List;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface MerchantService {

    Merchant create(Merchant merchant);

    List<Merchant> findAllByCurrency(Currency currency);

    Map<Integer,List<Merchant>> mapMerchantsToCurrency(List<Currency> currencies);
}