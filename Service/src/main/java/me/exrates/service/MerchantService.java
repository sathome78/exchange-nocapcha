package me.exrates.service;

import me.exrates.model.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface MerchantService {

    Merchant create(Merchant merchant);

    List<Merchant> findAllByCurrency(Currency currency);

    Map<String, String> withdrawRequest(CreditsOperation creditsOperation, Locale locale, Principal principal);

    String sendWithdrawalNotification(final int requestId, String mail,
                                      Locale locale, CreditsOperation creditsOperation);

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
