package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.Payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface MerchantService {

    Merchant create(Merchant merchant);

    List<Merchant> findAllByCurrency(Currency currency);

    Map<Integer,List<Merchant>> mapMerchantsToCurrency(List<Currency> currencies);

    Merchant findById(int id);

    Optional<CreditsOperation> prepareCreditsOperation (Payment payment, String userEmail);
}