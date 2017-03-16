package me.exrates.service;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.enums.CurrencyWarningType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface CurrencyService {

    String getCurrencyName(int currencyId);

    List<Currency> getAllCurrencies();

    Currency findByName(String name);

    Currency findById(int id);

    List<Currency> findAllCurrencies();

    boolean updateMinWithdraw(int currencyId, BigDecimal minAmount);

    void updateCurrencyLimit(int currencyId, OperationType operationType, String roleName, BigDecimal minAmount);

    List<CurrencyLimit> retrieveCurrencyLimitsForRole(String roleName, OperationType operationType);

    BigDecimal retrieveMinLimitForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId);

    List<CurrencyPair> getAllCurrencyPairs();

    CurrencyPair findCurrencyPairById(int currencyPairId);

    String amountToString(BigDecimal amount, String currency);

    int resolvePrecision(String currency);

    List<TransferLimitDto> retrieveMinTransferLimits(List<Integer> currencyIds);

    List<UserCurrencyOperationPermissionDto> getCurrencyOperationPermittedForRefill(String userEmail);

    List<UserCurrencyOperationPermissionDto> getCurrencyOperationPermittedForWithdraw(String userEmail);

  List<UserCurrencyOperationPermissionDto> findWithOperationPermissionByUserAndDirection(Integer userId, InvoiceOperationDirection operationDirection);

  Set<String> getCurrencyPermittedNameList(String userEmail);

  List<UserCurrencyOperationPermissionDto> getCurrencyPermittedOperationList(Integer userId);

  Set<String> getCurrencyPermittedNameList(Integer userId);
  
  Optional<String> getWarningForCurrency(Integer currencyId, CurrencyWarningType currencyWarningType);

  Currency getById(int id);
}
