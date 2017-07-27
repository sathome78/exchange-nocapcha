package me.exrates.service;

import me.exrates.model.*;
import me.exrates.model.dto.MerchantCurrencyLifetimeDto;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

  Merchant findByName(String name);

  List<MerchantCurrency> getAllUnblockedForOperationTypeByCurrencies(List<Integer> currenciesId, OperationType operationType);

  List<MerchantCurrencyApiDto> findNonTransferMerchantCurrencies(Integer currencyId);
  
  List<TransferMerchantApiDto> findTransferMerchants();
  
  List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions();

  Map<String, String> formatResponseMessage(CreditsOperation creditsOperation);

  Map<String, String> formatResponseMessage(Transaction transaction);

  void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType);

  void setBlockForAll(OperationType operationType, boolean blockStatus);

  void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus);

  BigDecimal getMinSum(Integer merchantId, Integer currencyId);

  void checkAmountForMinSum(Integer merchantId, Integer currencyId, BigDecimal amount);

  List<MerchantCurrencyLifetimeDto> getMerchantCurrencyWithRefillLifetime();

  MerchantCurrencyLifetimeDto getMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId);

  MerchantCurrencyScaleDto getMerchantCurrencyScaleByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId);

  void checkMerchantIsBlocked(Integer merchantId, Integer currencyId, OperationType operationType);

  List<String> retrieveBtcCoreBasedMerchantNames();

  String retrieveCoreWalletCurrencyNameByMerchant(String merchantName);

    Map<String, String> computeCommissionAndMapAllToString(BigDecimal amount,
                                                           OperationType type,
                                                           String currency,
                                                           String merchant);

    void checkDestinationTag(Integer merchantId, String memo);
}
