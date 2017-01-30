package me.exrates.service;

import me.exrates.model.Commission;
import me.exrates.model.dto.CommissionShortEditDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public interface CommissionService {
	
	Commission findCommissionByTypeAndRole(OperationType operationType, UserRole userRole);

	Commission getDefaultCommission(OperationType operationType);

	/**
	 * Returns individual commission for current merchant
	 * @param merchant
	 * @param currency
	 * @param operationType
	 * @return BigDecimal commission
	 */
	BigDecimal getCommissionMerchant(String merchant, String currency, OperationType operationType);

    List<Commission> getEditableCommissions();

    List<CommissionShortEditDto> getEditableCommissionsByRole(String role, Locale locale);

    List<Integer> resolveRoleIdsByName(String roleName);

    void updateCommission(Integer id, BigDecimal value);

	void updateCommission(OperationType operationType, String roleName, BigDecimal value);

    @Transactional
    void updateMerchantCommission(Integer merchantId, Integer currencyId, BigDecimal inputValue, BigDecimal outputValue);
}
