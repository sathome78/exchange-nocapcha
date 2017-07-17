package me.exrates.service.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.Merchant;
import me.exrates.model.dto.CommissionDataDto;
import me.exrates.model.dto.CommissionShortEditDto;
import me.exrates.model.dto.EditMerchantCommissionDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.CommissionService;
import me.exrates.service.MerchantService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.InvalidAmountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ZERO;
import static me.exrates.model.enums.ActionType.*;
import static me.exrates.model.enums.OperationType.*;

@Service
public class CommissionServiceImpl implements CommissionService {
  @Autowired
  CommissionDao commissionDao;

  @Autowired
  UserService userService;

  @Autowired
  UserRoleService userRoleService;

  @Autowired
  MerchantService merchantService;

  @Override
  public Commission findCommissionByTypeAndRole(OperationType operationType, UserRole userRole) {
    return commissionDao.getCommission(operationType, userRole);
  }

  @Override
  public Commission getDefaultCommission(OperationType operationType) {
    return commissionDao.getDefaultCommission(operationType);
  }

  @Override
  @Transactional
  public BigDecimal getCommissionMerchant(String merchant, String currency, OperationType operationType) {
    if (!(operationType == OperationType.INPUT || operationType == OperationType.OUTPUT)) {
      throw new IllegalArgumentException("Invalid operation type");
    }
    return commissionDao.getCommissionMerchant(merchant, currency, operationType);
  }

  @Override
  @Transactional
  public BigDecimal getCommissionMerchant(Integer merchantId, Integer currencyId, OperationType operationType) {
    if (!(operationType == OperationType.INPUT || operationType == OperationType.OUTPUT || operationType == OperationType.USER_TRANSFER)) {
      throw new IllegalArgumentException("Invalid operation type: "+operationType);
    }
    return commissionDao.getCommissionMerchant(merchantId, currencyId, operationType);
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
  public BigDecimal getMinFixedCommission(Integer currencyId, Integer merchantId) {
    return commissionDao.getMinFixedCommission(currencyId, merchantId);
  }

  @Override
  @Transactional
  public Map<String, String> computeCommissionAndMapAllToString(
      Integer userId,
      BigDecimal amount,
      OperationType type,
      Integer currencyId,
      Integer merchantId,
      Locale locale) {
    Map<String, String> result = new HashMap<>();
    CommissionDataDto commissionData = normalizeAmountAndCalculateCommission(userId, amount, type, currencyId, merchantId);
    result.put("amount", commissionData.getAmount().toPlainString());
    result.put("merchantCommissionRate", "("
        .concat(BigDecimalProcessing.formatLocale(commissionData.getMerchantCommissionRate(), locale, false))
        .concat(commissionData.getMerchantCommissionUnit())
        .concat(")"));
    result.put("merchantCommissionAmount", commissionData.getMerchantCommissionAmount().toPlainString());
    result.put("companyCommissionRate", "("
        .concat(BigDecimalProcessing.formatLocale(commissionData.getCompanyCommissionRate(), locale, false))
        .concat(commissionData.getCompanyCommissionUnit())
        .concat(")"));
    result.put("companyCommissionAmount", commissionData.getCompanyCommissionAmount().toPlainString());
    result.put("totalCommissionAmount", commissionData.getTotalCommissionAmount().toPlainString());
    result.put("resultAmount", commissionData.getResultAmount().toPlainString());
    return result;
  }

  @Override
  @Transactional
  public CommissionDataDto normalizeAmountAndCalculateCommission(
      Integer userId,
      BigDecimal amount,
      OperationType type,
      Integer currencyId,
      Integer merchantId) {
    Map<String, String> result = new HashMap<>();
    Commission companyCommission = findCommissionByTypeAndRole(type, userService.getUserRoleFromDB(userId));
    BigDecimal companyCommissionRate = companyCommission.getValue();
    String companyCommissionUnit = "%";
    Merchant merchant = merchantService.findById(merchantId);
    String merchantProcessType = merchant.getProcessType();
    if (!"CRYPTO".equals(merchantProcessType) || amount.compareTo(BigDecimal.ZERO) != 0) {
      BigDecimal merchantCommissionRate = getCommissionMerchant(merchantId, currencyId, type);
      BigDecimal merchantCommissionAmount;
      BigDecimal companyCommissionAmount;
      String merchantCommissionUnit = "%";
      if (type == INPUT) {
        int currencyScale = merchantService.getMerchantCurrencyScaleByMerchantIdAndCurrencyId(merchantId, currencyId).getScaleForRefill();
        amount = amount.setScale(currencyScale, ROUND_HALF_UP);
        merchantCommissionAmount = BigDecimalProcessing.doAction(amount, merchantCommissionRate, MULTIPLY_PERCENT);
        companyCommissionAmount = BigDecimalProcessing.doAction(amount.subtract(merchantCommissionAmount), companyCommissionRate, MULTIPLY_PERCENT);
      } else if (type == OUTPUT) {
        int currencyScale = merchantService.getMerchantCurrencyScaleByMerchantIdAndCurrencyId(merchantId, currencyId).getScaleForWithdraw();
        amount = amount.setScale(currencyScale, ROUND_HALF_UP);
        companyCommissionAmount = BigDecimalProcessing.doAction(amount, companyCommissionRate, MULTIPLY_PERCENT).setScale(currencyScale, ROUND_HALF_UP);
        merchantCommissionAmount = BigDecimalProcessing.doAction(amount.subtract(companyCommissionAmount), merchantCommissionRate, MULTIPLY_PERCENT).setScale(currencyScale, ROUND_HALF_UP);
        BigDecimal merchantMinFixedCommission = getMinFixedCommission(currencyId, merchantId);
        if (merchantCommissionAmount.compareTo(merchantMinFixedCommission) < 0) {
          merchantCommissionAmount = merchantMinFixedCommission;
          merchantCommissionUnit = "";
        }
      } else if (type == USER_TRANSFER) {
        int currencyScale = merchantService.getMerchantCurrencyScaleByMerchantIdAndCurrencyId(merchantId, currencyId).getScaleForTransfer();
        amount = amount.setScale(currencyScale, ROUND_HALF_UP);
        companyCommissionAmount = BigDecimal.ZERO;
        merchantCommissionAmount = BigDecimalProcessing.doAction(amount, merchantCommissionRate, MULTIPLY_PERCENT).setScale(currencyScale, ROUND_HALF_UP);
        BigDecimal merchantMinFixedCommission = getMinFixedCommission(currencyId, merchantId);
        if (merchantCommissionAmount.compareTo(merchantMinFixedCommission) < 0) {
          merchantCommissionAmount = merchantMinFixedCommission;
          merchantCommissionUnit = "";
        }
      } else {
        throw new IllegalOperationTypeException(type.name());
      }
      BigDecimal totalCommissionAmount = BigDecimalProcessing.doAction(merchantCommissionAmount, companyCommissionAmount, ADD);
      BigDecimal totalAmount = BigDecimalProcessing.doAction(amount, totalCommissionAmount, SUBTRACT);
      if (totalAmount.compareTo(ZERO) <= 0) {
        throw new InvalidAmountException(amount.toString());
      }
      return new CommissionDataDto(
          amount,
          merchantCommissionRate,
          merchantCommissionUnit,
          merchantCommissionAmount,
          companyCommission,
          companyCommissionRate,
          companyCommissionUnit,
          companyCommissionAmount,
          totalCommissionAmount,
          totalAmount
      );
    } else {
      return new CommissionDataDto(
          ZERO,
          ZERO,
          "",
          ZERO,
          companyCommission,
          companyCommissionRate,
          companyCommissionUnit,
          ZERO,
          ZERO,
          ZERO
      );
    }
  }

  @Override
  @Transactional
  public BigDecimal calculateCommissionForRefillAmount(BigDecimal amount, Integer commissionId) {
    BigDecimal companyCommissionRate = commissionDao.getCommissionById(commissionId).getValue();
    return BigDecimalProcessing.doAction(amount, companyCommissionRate, MULTIPLY_PERCENT);
  }

}
