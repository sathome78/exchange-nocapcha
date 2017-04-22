package me.exrates.dao;

import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.dto.MerchantCurrencyAutoParamDto;
import me.exrates.model.dto.MerchantCurrencyLifetimeDto;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface MerchantDao {

  Merchant create(Merchant merchant);

  Merchant findById(int id);

  Merchant findByName(String name);

  List<Merchant> findAll();

  List<Merchant> findAllByCurrency(int currencyId);

  BigDecimal getMinSum(int merchant, int currency);

  List<MerchantCurrency> findAllByCurrencies(List<Integer> currenciesId, OperationType operationType);

  List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId, UserRole userRole);

  List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions();

  boolean checkInputRequests(int currencyId, String email);

  boolean checkOutputRequests(int currencyId, String email);

  void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType);

  void setBlockForAll(OperationType operationType, boolean blockStatus);

  void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus);

  boolean checkMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType);

  void setAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId, Boolean withdrawAutoEnabled, Integer withdrawAutoDelaySeconds, BigDecimal withdrawAutoThresholdAmount);

  MerchantCurrencyAutoParamDto findAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId);

  List<MerchantCurrencyLifetimeDto> findMerchantCurrencyWithRefillLifetime();

  MerchantCurrencyLifetimeDto findMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId);
}