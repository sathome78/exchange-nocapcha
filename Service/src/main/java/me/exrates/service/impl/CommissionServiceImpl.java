package me.exrates.service.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.dto.CommissionShortEditDto;
import me.exrates.model.dto.EditMerchantCommissionDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.ROUND_DOWN;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ZERO;
import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.OperationType.USER_TRANSFER;

@Service
public class CommissionServiceImpl implements CommissionService {


  @Autowired
  CommissionDao commissionDao;

  @Autowired
  UserService userService;

  @Autowired
  private CurrencyService currencyService;

  @Autowired
  UserRoleService userRoleService;

  @Override
  public Commission findCommissionByTypeAndRole(OperationType operationType, UserRole userRole) {
    return commissionDao.getCommission(operationType, userRole);
  }

  @Override
  public Commission getDefaultCommission(OperationType operationType) {
    return commissionDao.getDefaultCommission(operationType);
  }

  @Override
  public BigDecimal getCommissionMerchant(String merchant, String currency, OperationType operationType) {
    if (!(operationType == OperationType.INPUT || operationType == OperationType.OUTPUT)) {
      throw new IllegalArgumentException("Invalid operation type");
    }
    return commissionDao.getCommissionMerchant(merchant, currency, operationType);
  }

  @Override
  public List<Commission> getEditableCommissions() {
    return commissionDao.getEditableCommissions();
  }

  @Override
  public List<CommissionShortEditDto> getEditableCommissionsByRole(String roleName, Locale locale) {
    return commissionDao.getEditableCommissionsByRoles(userRoleService.getRealUserRoleIdByBusinessRoleList(roleName), locale);
  }

  @Override
  @Transactional
  public void updateCommission(Integer id, BigDecimal value) {
    commissionDao.updateCommission(id, value);
  }

  @Override
  @Transactional
  public void updateCommission(OperationType operationType, String roleName, BigDecimal value) {
    commissionDao.updateCommission(operationType, userRoleService.getRealUserRoleIdByBusinessRoleList(roleName), value);
  }


  @Override
  @Transactional
  public void updateMerchantCommission(EditMerchantCommissionDto editMerchantCommissionDto) {
    commissionDao.updateMerchantCurrencyCommission(editMerchantCommissionDto);
  }

  @Override
  public BigDecimal getMinFixedCommission(String merchant, String currency) {
    return commissionDao.getMinFixedCommission(merchant, currency);
  }

  @Override
  @Transactional
  public Map<String, String> computeCommissionAndMapAllToString(
      BigDecimal amount,
      OperationType type,
      String currency,
      String merchant) {
    Map<String, String> result = new HashMap<>();
    BigDecimal commission = findCommissionByTypeAndRole(type, userService.getUserRoleFromSecurityContext()).getValue();
    BigDecimal commissionMerchant = type == USER_TRANSFER ? ZERO : getCommissionMerchant(merchant, currency, type);
    BigDecimal commissionTotal = commission.add(commissionMerchant).setScale(currencyService.resolvePrecision(currency), ROUND_HALF_UP);
    BigDecimal commissionAmount = amount.multiply(commissionTotal).divide(new BigDecimal(100L)).setScale(currencyService.resolvePrecision(currency), ROUND_HALF_UP);
    String commissionString = Stream.of("(", commissionTotal.stripTrailingZeros().toString(), "%)").collect(Collectors.joining(""));
    if (type == OUTPUT) {
      BigDecimal merchantMinFixedCommission = getMinFixedCommission(merchant, currency);
      if (commissionAmount.compareTo(merchantMinFixedCommission) < 0) {
        commissionAmount = merchantMinFixedCommission;
        commissionString = "";
      }
    }
    final BigDecimal resultAmount = type != OUTPUT ? amount.add(commissionAmount).setScale(currencyService.resolvePrecision(currency), ROUND_HALF_UP) :
        amount.subtract(commissionAmount).setScale(currencyService.resolvePrecision(currency), ROUND_DOWN);
    result.put("amount", amount.toString());
    result.put("commission", commissionString);
    result.put("commissionAmount", currencyService.amountToString(commissionAmount, currency));
    result.put("totalAmount", currencyService.amountToString(resultAmount, currency));
    return result;
  }

}
