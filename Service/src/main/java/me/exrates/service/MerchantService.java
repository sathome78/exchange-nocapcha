package me.exrates.service;

import me.exrates.model.*;
import me.exrates.model.dto.MerchantCurrencyCommissionDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.WithdrawalRequestStatus;
import me.exrates.model.vo.CacheData;

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

    Map<String, Object> declineWithdrawalRequest(int requestId, Locale locale, String email);

    List<WithdrawRequest> findAllWithdrawRequests();

    List<Merchant> findAllByCurrency(Currency currency);

    Map<String, String> withdrawRequest(CreditsOperation creditsOperation, Locale locale, String userEmail);
    List<Merchant> findAll();

    String resolveTransactionStatus(Transaction transaction, Locale locale);

    String sendWithdrawalNotification(WithdrawRequest withdrawRequest ,WithdrawalRequestStatus status, Locale locale);

    String sendDepositNotification(String toWallet, String email,
        Locale locale, CreditsOperation creditsOperation, String depositNotification);

    Map<Integer, List<Merchant>> mapMerchantsToCurrency(List<Currency> currencies);

    Merchant findById(int id);

    List<MerchantCurrency> findAllByCurrencies(List<Integer> currenciesId, OperationType operationType);

    List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId);

    List<MerchantCurrencyCommissionDto> findMerchantCurrencyCommissions();

    Map<String, String> formatResponseMessage(CreditsOperation creditsOperation);

    Map<String, String> formatResponseMessage(Transaction transaction);

    Map<String, String> computeCommissionAndMapAllToString(BigDecimal amount, OperationType operationType, String currency, String merchant);

    Optional<CreditsOperation> prepareCreditsOperation(Payment payment, String userEmail);

    /**
     * Returns the list of input/output orders for user
     * Used for displaying in History page
     * @param cacheData stores the cach params and is used for caching result
     * @param email is user email. Used as the user identifier
     * @param offset used for pagination
     * @param limit used for pagination
     * @param locale used for formatting number
     * @return list of input/output orders
     */
    List<MyInputOutputHistoryDto> getMyInputOutputHistory(CacheData cacheData, String email, Integer offset, Integer limit, Locale locale);

    List<MyInputOutputHistoryDto> getMyInputOutputHistory(String email, Integer offset, Integer limit, Locale locale);

    boolean checkInputRequestsLimit(int merchantId, String email);
}
