package me.exrates.service;

import me.exrates.model.*;
import me.exrates.model.dto.MerchantCurrencyLifetimeDto;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
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

  List<MerchantCurrency> getAllUnblockedForOperationTypeByCurrencies(List<Integer> currenciesId, OperationType operationType);

  List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId);

  List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions();

  Map<String, String> formatResponseMessage(CreditsOperation creditsOperation);

  Map<String, String> formatResponseMessage(Transaction transaction);

  void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType);

  void setBlockForAll(OperationType operationType, boolean blockStatus);

  void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus);

  BigDecimal getMinSum(Integer merchantId, Integer currencyId);

  List<MerchantCurrencyLifetimeDto> getMerchantCurrencyWithRefillLifetime();

  MerchantCurrencyLifetimeDto getMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId);

  int getMerchantCurrencyScaleByMerchantIdAndCurrencyIdAndOperationType(Integer merchantId, Integer currencyId, OperationType operationType);

  void checkMerchantIsBlocked(Integer merchantId, Integer currencyId, OperationType operationType);
}
