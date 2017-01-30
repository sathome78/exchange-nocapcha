package me.exrates.service.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.dto.CommissionShortEditDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.service.CommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class CommissionServiceImpl implements CommissionService {


	@Autowired  
	CommissionDao commissionDao;

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
		return commissionDao.getEditableCommissionsByRoles(resolveRoleIdsByName(roleName), locale);
	}

	@Override
	public List<Integer> resolveRoleIdsByName(String roleName) {
		List<UserRole> userRoles;
		if ("ADMIN".equals(roleName)) {
			userRoles = Arrays.asList(UserRole.ADMINISTRATOR, UserRole.ACCOUNTANT, UserRole.ADMIN_USER);
		} else {
			userRoles = Collections.singletonList(UserRole.valueOf(roleName));
		}
		return userRoles.stream().map(UserRole::getRole).collect(Collectors.toList());
	}


	@Override
	@Transactional
	public void updateCommission(Integer id, BigDecimal value) {
		commissionDao.updateCommission(id, value);
	}

	@Override
	@Transactional
	public void updateCommission(OperationType operationType, String roleName, BigDecimal value) {
		commissionDao.updateCommission(operationType, resolveRoleIdsByName(roleName), value);
	}


	@Override
	@Transactional
	public void updateMerchantCommission(Integer merchantId, Integer currencyId, BigDecimal inputValue, BigDecimal outputValue) {
		commissionDao.updateMerchantCurrencyCommission(merchantId, currencyId, inputValue, outputValue);
	}

}
