package me.exrates.service;

import me.exrates.model.*;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.OperationType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface MerchantService {

    List<Merchant> findAllByCurrency(Currency currency);

    List<Merchant> findAll();

    String resolveTransactionStatus(Transaction transaction, Locale locale);

    String sendDepositNotification(String toWallet, String email,
        Locale locale, CreditsOperation creditsOperation, String depositNotification);

    Merchant findById(int id);

    Merchant findByNName(String name);

    List<MerchantCurrency> findAllByCurrencies(List<Integer> currenciesId, OperationType operationType);

    List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId);

    List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions();

    Map<String, String> formatResponseMessage(CreditsOperation creditsOperation);

    Map<String, String> formatResponseMessage(Transaction transaction);

    Map<String, String> computeCommissionAndMapAllToString(BigDecimal amount, OperationType operationType, String currency, String merchant);

    Optional<CreditsOperation> prepareCreditsOperation(Payment payment, BigDecimal addition, String userEmail);

    Optional<CreditsOperation> prepareCreditsOperation(Payment payment, String userEmail);
  
  boolean checkInputRequestsLimit(int currencyId, String email);

    boolean checkOutputRequestsLimit(int currencyId, String email);

    void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType);

    @Transactional
    void setBlockForAll(OperationType operationType, boolean blockStatus);

    @Transactional
    void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus);
  
  List<String> retrieveBtcCoreBasedMerchantNames();
  
  String retrieveCoreWalletCurrencyNameByMerchant(String merchantName);
}
