package me.exrates.service.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.dto.CommissionShortEditDto;
import me.exrates.model.dto.EditMerchantCommissionDto;
import me.exrates.model.enums.BusinessUserRoleEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.service.CommissionService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
public class CommissionServiceImpl implements CommissionService {


	@Autowired  
	CommissionDao commissionDao;

	@Autowired
	UserService userService;

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

}
