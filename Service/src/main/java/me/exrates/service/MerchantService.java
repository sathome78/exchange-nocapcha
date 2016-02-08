package me.exrates.service;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface MerchantService {

    Merchant create(Merchant merchant);

    List<Merchant> findAllByCurrency(Currency currency);
}