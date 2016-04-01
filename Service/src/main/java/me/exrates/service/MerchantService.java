package me.exrates.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface MerchantService {

    Merchant create(Merchant merchant);

    List<Merchant> findAllByCurrency(Currency currency);

    String sendDepositNotification(String toWallet, String email,
        Locale locale, CreditsOperation creditsOperation);

    String sendDepositNotification(String toWallet, String email,
        Locale locale, CreditsOperation creditsOperation, BigDecimal externalFee);

    Map<Integer,List<Merchant>> mapMerchantsToCurrency(List<Currency> currencies);

    Merchant findById(int id);

    List<MerchantCurrency> findAllByCurrencies(List<Integer> currenciesId);

    Map<String, String> formatResponseMessage(CreditsOperation creditsOperation);

    Map<String, String> formatResponseMessage(Transaction transaction);

    Optional<CreditsOperation> prepareCreditsOperation (Payment payment, String userEmail);
}