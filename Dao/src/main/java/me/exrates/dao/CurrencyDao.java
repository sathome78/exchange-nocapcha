package me.exrates.dao;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.dto.mobileApiDto.dashboard.CurrencyPairWithLimitsDto;
import me.exrates.model.enums.CurrencyWarningTopicEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;
import java.util.List;

public interface CurrencyDao {

	List<Currency> getCurrList();

	int getCurrencyId(int walletId);

	String getCurrencyName(int currencyId);

	Currency findByName(String name);

	Currency findById(int id);

	List<Currency> findAllCurrencies();
  
  List<CurrencyLimit> retrieveCurrencyLimitsForRoles(List<Integer> roleIds, OperationType operationType);

    List<TransferLimitDto> retrieveMinTransferLimits(List<Integer> currencyIds, Integer roleId);

    BigDecimal retrieveMinLimitForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId);

    void updateCurrencyLimit(int currencyId, OperationType operationType, List<Integer> roleIds, BigDecimal minAmount, Integer maxDailyRequest);

	List<CurrencyPair> getAllCurrencyPairs();

	CurrencyPair getCurrencyPairById(int currency1Id, int currency2Id);

	CurrencyPair findCurrencyPairById(int currencyPairId);

	List<UserCurrencyOperationPermissionDto> findCurrencyOperationPermittedByUserAndDirection(Integer userId, String operationDirection);

  List<UserCurrencyOperationPermissionDto> findCurrencyOperationPermittedByUserList(Integer userId);
  
  List<String> getWarningForCurrency(Integer currencyId, CurrencyWarningTopicEnum currencyWarningTopicEnum);

  CurrencyPair findCurrencyPairByOrderId(int orderId);
  
  CurrencyPairLimitDto findCurrencyPairLimitForRoleByPairAndType(Integer currencyPairId, Integer roleId, Integer orderTypeId);
  
  List<CurrencyPairLimitDto> findLimitsForRolesByType(List<Integer> roleIds, Integer orderTypeId);
  
  void setCurrencyPairLimit(Integer currencyPairId, List<Integer> roleIds, Integer orderTypeId,
                            BigDecimal minRate, BigDecimal maxRate);
  
  List<CurrencyPairWithLimitsDto> findAllCurrencyPairsWithLimits(Integer roleId);

    List<Currency> findAllCurrenciesWithHidden();
}