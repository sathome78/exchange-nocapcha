package me.exrates.service;

import me.exrates.model.*;

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

    List<MerchantCurrency> findAllByCurrencies(List<Integer> currenciesId);

    Optional<CreditsOperation> prepareCreditsOperation (Payment payment, String userEmail);
}