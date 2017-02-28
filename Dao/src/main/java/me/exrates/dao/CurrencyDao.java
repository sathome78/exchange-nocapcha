package me.exrates.dao;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
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

    boolean updateMinWithdraw(int currencyId, BigDecimal minAmount);

    List<CurrencyLimit> retrieveCurrencyLimitsForRoles(List<Integer> roleIds, OperationType operationType);

    List<TransferLimitDto> retrieveMinTransferLimits(List<Integer> currencyIds, Integer roleId);

    BigDecimal retrieveMinLimitForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId);

    void updateCurrencyLimit(int currencyId, OperationType operationType, List<Integer> roleIds, BigDecimal minAmount);

	List<CurrencyPair> getAllCurrencyPairs();

	CurrencyPair getCurrencyPairById(int currency1Id, int currency2Id);

	CurrencyPair findCurrencyPairById(int currencyPairId);

	List<UserCurrencyOperationPermissionDto> findWithOperationPermissionByUserAndDirection(Integer userId, String operationDirection);
}