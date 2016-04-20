package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.model.WithdrawRequest;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.WithdrawalRequestStatus;

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

    Map<String, String> acceptWithdrawalRequest(int requestId, Locale locale,Principal principal);

    Map<String, String> declineWithdrawalRequest(int requestId, Locale locale);

    List<WithdrawRequest> findAllWithdrawRequests();

    List<Merchant> findAllByCurrency(Currency currency);

    Map<String, String> withdrawRequest(CreditsOperation creditsOperation, Locale locale, Principal principal);

    String resolveTransactionStatus(Transaction transaction, Locale locale);

    String sendWithdrawalNotification(WithdrawRequest withdrawRequest ,WithdrawalRequestStatus status, Locale locale);

    String sendDepositNotification(String toWallet, String email,
        Locale locale, CreditsOperation creditsOperation);

    Map<Integer, List<Merchant>> mapMerchantsToCurrency(List<Currency> currencies);

    Merchant findById(int id);

    List<MerchantCurrency> findAllByCurrencies(List<Integer> currenciesId);

    Map<String, String> formatResponseMessage(CreditsOperation creditsOperation);

    Map<String, String> formatResponseMessage(Transaction transaction);

    Map<String, String> computeCommissionAndMapAllToString(BigDecimal amount, OperationType operationType, String currency);

    Optional<CreditsOperation> prepareCreditsOperation(Payment payment, String userEmail);
}
